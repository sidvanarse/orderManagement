package com.orderManagement.service;

import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;
import com.orderManagement.model.ExecutionType;
import com.orderManagement.model.OrderType;
import com.orderManagement.repository.ExecutionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
/**
 * The ExecutionServiceImpl implements ExecutionService.
 * This service manages all execution related operations.
 */
@Service
@Slf4j
public class ExecutionServiceImpl implements ExecutionService, SmartLifecycle {

    private final BookService bookService;

    private final OrderService orderService;

    private final ExecutionRepository executionRepository;

    private volatile boolean isRunning;

    private ConcurrentHashMap<String,List<ExecutionEntity>> executionsMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    @Autowired
    public ExecutionServiceImpl(BookService bookService, OrderService orderService, ExecutionRepository executionRepository) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.executionRepository = executionRepository;
    }

    @Override
    @Transactional
    public void triggerExecution(Execution execution) throws BookDoesNotExistsException,BookOpenException {
        boolean isBookAvailable = bookService.isBookAvailable(execution.getBookName());
        if(!isBookAvailable){
            throw new BookDoesNotExistsException("Book with the name " + execution.getBookName() + " does not exists.");
        }
        if(!bookService.isBookClosed(execution.getBookName())){
            throw new BookOpenException("Book with the name " + execution.getBookName() + " is still open. Can not run execution on open book.");
        }
        saveExecutionInDb(execution);
        //We lock at unique execution as we need to operate on latest data.
        //If we receive two execution of same key at same time then we might get wrong result on remaining quantity.
        //Hence, this lock is required
        synchronized (locks.computeIfAbsent(execution.getKey(), k -> new Object())) {
            List<OrderEntity> orderList = orderService.getActiveOrdersForBook(execution.getBookName());
            if(execution.getType().equals(ExecutionType.OFFER)){
                handleOfferExecutions(orderList,execution);
            }
            else{
                handleAskExecutions(orderList,execution);
            }
        }
    }

    @Override
    public List<ExecutionEntity> getTriggeredExecutionsForBook(String bookName) {
        return executionsMap.getOrDefault(bookName,new ArrayList<>());
    }


    private void saveExecutionInDb(Execution execution){
        ExecutionEntity executionEntity = executionRepository.save(ExecutionEntity.toEntity(execution));
        executionsMap.putIfAbsent(executionEntity.getBookName(), new ArrayList<>());
        executionsMap.computeIfPresent(executionEntity.getBookName(),(key,value)->{
            value.add(executionEntity);
            return value;
        });
    }

    private void handleOfferExecutions(List<OrderEntity> orderList,Execution execution){
        List<OrderEntity> buyOrders = orderList.stream()
                .filter(order-> order.getRemainingQuantity() > 0)
                .filter(order -> order.getType() == OrderType.BUY)
                .filter(order -> order.getInstrumentId() == (execution.getInstrumentId()))
                .filter(order -> order.getPrice() >= execution.getPrice())
                .sorted(Comparator.comparing(OrderEntity::getEntryDate))
                .collect(Collectors.toList());

        int remainingQuantity = execution.getQuantity();
        for (OrderEntity buyOrder : buyOrders) {
            if (remainingQuantity > 0) {
                int buyOrderQuantity = buyOrder.getRemainingQuantity();
                int executionQuantity = Math.min(buyOrderQuantity, remainingQuantity);
                buyOrder.setRemainingQuantity(buyOrderQuantity - executionQuantity);
                orderService.updateOrder(buyOrder);
                remainingQuantity -= executionQuantity;
            } else {
                break;
            }
        }
    }

    private void handleAskExecutions(List<OrderEntity> orderList,Execution execution){
        List<OrderEntity> sellOrders = orderList.stream()
                .filter(order-> order.getRemainingQuantity() > 0)
                .filter(order -> order.getType() == OrderType.SELL)
                .filter(order -> order.getInstrumentId() == (execution.getInstrumentId()))
                .filter(order -> order.getPrice() <= execution.getPrice())
                .sorted(Comparator.comparing(OrderEntity::getEntryDate))
                .collect(Collectors.toList());

        int remainingQuantity = execution.getQuantity();
        for (OrderEntity sellOrder : sellOrders) {
            if (remainingQuantity > 0) {
                int sellOrderQuantity = sellOrder.getRemainingQuantity();
                int executionQuantity = Math.min(sellOrderQuantity, remainingQuantity);
                sellOrder.setRemainingQuantity(sellOrderQuantity - executionQuantity);
                orderService.updateOrder(sellOrder);
                remainingQuantity -= executionQuantity;
            } else {
                break;
            }
        }
    }

    @Override
    public void start() {
        executionRepository.findAll().stream().forEach(execution->{
            executionsMap.putIfAbsent(execution.getBookName(), new ArrayList<>());
            executionsMap.computeIfPresent(execution.getBookName(),(key,value)->{
                value.add(execution);
                return value;
            });
        });
        isRunning = true;
    }

    @Override
    public void stop() {
        executionsMap.clear();
        locks.clear();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
