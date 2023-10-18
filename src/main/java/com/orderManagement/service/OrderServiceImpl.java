package com.orderManagement.service;

import com.orderManagement.entity.BookEntity;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;
import com.orderManagement.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The OrderServiceImpl implements OrderService.
 * This service manages all order related operations.
 */
@Service
public class OrderServiceImpl implements OrderService, SmartLifecycle {

    private final BookService bookService;

    private final OrdersRepository ordersRepository;

    private volatile boolean isRunning;

    private ConcurrentHashMap<String, List<OrderEntity>> bookOrdersMap = new ConcurrentHashMap<>();

    @Autowired
    public OrderServiceImpl(BookService bookService, OrdersRepository ordersRepository) {
        this.bookService = bookService;
        this.ordersRepository = ordersRepository;
    }

    @Override
    @Transactional
    public Order addOrder(Order order) throws BookClosedException,OrderAlreadyExistsException,OrderNotAvailableException{
        bookService.addBookIfDoesNotExists(order.getBookName());
        if(bookService.isBookClosed(order.getBookName())){
            throw new BookClosedException("Book with the name " + order.getBookName() + " is closed.");
        }
        if(Objects.nonNull(order.getOrderId())){
            if(!order.getOrderId().equals(Long.valueOf(0))){
                Optional<OrderEntity> orderEntity =  ordersRepository.findById(order.getOrderId());
                if(orderEntity.isPresent()){
                    throw new OrderAlreadyExistsException("Order with the orderID " + order.getOrderId() + " already exist.");
                }
                else{
                    throw new OrderNotAvailableException("Unknown order " + order.getOrderId());
                }
            }
        }
        OrderEntity orderEntity = ordersRepository.save(OrderEntity.toEntity(order));
        bookOrdersMap.putIfAbsent(orderEntity.getBookName(), new ArrayList<>());
        bookOrdersMap.computeIfPresent(orderEntity.getBookName(),(key,value)->{
            value.add(orderEntity);
            return value;
        });
        return orderEntity.toBean();
    }
    @Override
    @Transactional
    public Order editOrder(Order order) throws BookDoesNotExistsException,BookClosedException,OrderNotAvailableException,InactiveOrderException{
        boolean isBookAvailable = bookService.isBookAvailable(order.getBookName());
        if(!isBookAvailable){
            throw new BookDoesNotExistsException("Book with the name " + order.getBookName() + " does not exists.");
        }
        if(bookService.isBookClosed(order.getBookName())){
            throw new BookClosedException("Book with the name " + order.getBookName() + " is closed.");
        }
        Optional<OrderEntity> pastOrder = ordersRepository.findById(order.getOrderId());
        if(pastOrder.isPresent()){
            if(!pastOrder.get().isActive()){
                throw new InactiveOrderException("Order with id " + order.getOrderId() + " is not active, can not edit order.");
            }
            OrderEntity editedOrder = OrderEntity.toEntity(order);
            editedOrder.setPreviousOrderId(order.getOrderId());
            editedOrder = ordersRepository.save(editedOrder);
            pastOrder.get().setActive(false);
            ordersRepository.save(pastOrder.get());
            updateInMemoryMap(pastOrder.get(),editedOrder,order.getBookName());
            return editedOrder.toBean();
        }
        else{
            throw new OrderNotAvailableException("order with order id" + order.getOrderId() + " is not available in the system. Can not edit.");
        }
    }
    @Override
    @Transactional
    public void deleteOrder(Long orderId) throws BookDoesNotExistsException,BookClosedException,OrderNotAvailableException{
        Optional<OrderEntity> pastOrder = ordersRepository.findById(orderId);
        if(pastOrder.isPresent()){
            boolean isBookAvailable = bookService.isBookAvailable(pastOrder.get().getBookName());
            if(!isBookAvailable){
                throw new BookDoesNotExistsException("Book with the name " + pastOrder.get().getBookName() + " does not exists.");
            }
            if(bookService.isBookClosed(pastOrder.get().getBookName())){
                throw new BookClosedException("Book with the name " + pastOrder.get().getBookName() + " is closed.");
            }
            pastOrder.get().setActive(false);
            ordersRepository.save(pastOrder.get());
            updateInMemoryMap(pastOrder.get(),null,pastOrder.get().getBookName());
        }
        else{
            throw new OrderNotAvailableException("order with order id" + orderId + " is not available in the system. Can not edit.");
        }
    }

    @Override
    public List<OrderEntity> getActiveOrdersForBook(String bookName){
        return bookOrdersMap
                .getOrDefault(bookName,new ArrayList<>())
                .stream()
                .collect(Collectors.toList());
    }
    @Override
    public void updateOrder(OrderEntity orderEntity){
        ordersRepository.save(orderEntity);
    }

    @Override
    public List<OrderEntity> getCompletedOrdersForBook(String bookName) {
        return getActiveOrdersForBook(bookName)
                .stream()
                .filter(activeOrder->activeOrder.getRemainingQuantity() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> getPendingOrdersForBook(String bookName) {
        return getActiveOrdersForBook(bookName)
                .stream()
                .filter(activeOrder->activeOrder.getRemainingQuantity() != 0)
                .collect(Collectors.toList());
    }

    private void updateInMemoryMap(OrderEntity orderToRemove, OrderEntity orderToAdd,String bookName){
        bookOrdersMap.computeIfPresent(bookName, (key,currentOrders)->{
            List<OrderEntity> newList =  currentOrders
                    .stream()
                    .filter(currentOrder-> !currentOrder.getOrderId().equals(orderToRemove.getOrderId()))
                    .collect(Collectors.toList());
            if(orderToAdd != null){
                newList.add(orderToAdd);
            }
            return newList;
        });
    }

    @Override
    public void start() {
        List<OrderEntity> orders = ordersRepository.findAll();
        List<OrderEntity> activeOrders = orders
                .stream()
                .filter(order-> order.isActive())
                .collect(Collectors.toList());

        activeOrders.stream().forEach(activeOrder->{
            bookOrdersMap.putIfAbsent(activeOrder.getBookName(), new ArrayList<>());
            bookOrdersMap.computeIfPresent(activeOrder.getBookName(),(key,value)->{
                value.add(activeOrder);
                return value;
            });
        });
        isRunning = true;
    }

    @Override
    public void stop() {
        bookOrdersMap.clear();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
