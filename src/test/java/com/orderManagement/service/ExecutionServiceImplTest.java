package com.orderManagement.service;

import com.orderManagement.entity.BookEntity;
import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;
import com.orderManagement.model.ExecutionType;
import com.orderManagement.model.OrderType;
import com.orderManagement.repository.ExecutionRepository;
import com.orderManagement.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExecutionServiceImplTest {
    @Mock
    private BookService bookService;
    @Mock
    private OrderService orderService;
    @Mock
    private ExecutionRepository executionRepository;
    @InjectMocks
    private ExecutionServiceImpl executionService;

    @BeforeEach
    void setupBeforeEach() {
        ExecutionEntity executionEntity = new ExecutionEntity();
        executionEntity.setBookName("book");
        executionEntity.setType(ExecutionType.OFFER);
        executionEntity.setPrice(32.5);
        executionEntity.setInstrumentId(42);
        executionEntity.setExecutionId(Long.valueOf(1));
        List<ExecutionEntity> list = new ArrayList<>();
        list.add(executionEntity);
        when(executionRepository.findAll()).thenReturn(list);
        executionService.start();
    }

    @Test
    public void testCanNotTriggerExecutionIfBookIsNotClosed(){
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        Execution execution = new Execution();
        execution.setBookName("book");
        assertThrows(BookOpenException.class, () -> {
            executionService.triggerExecution(execution);
        });
    }

    @Test
    public void testCanNotTriggerExecutionIfBookIsNotAvailable(){
        when(bookService.isBookAvailable(matches("book"))).thenReturn(false);
        Execution execution = new Execution();
        execution.setBookName("book");
        assertThrows(BookDoesNotExistsException.class, () -> {
            executionService.triggerExecution(execution);
        });
    }

    @Test
    public void testOfferExecutionTrigger(){
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        List<OrderEntity> activeOrders = TestUtils.mockActiveOrders();
        when(orderService.getActiveOrdersForBook(matches("book"))).thenReturn(activeOrders);
        Execution execution = new Execution();
        execution.setPrice(39);
        execution.setQuantity(70);
        execution.setBookName("book");
        execution.setInstrumentId(42);
        execution.setType(ExecutionType.OFFER);
        when(executionRepository.save(any())).thenReturn(ExecutionEntity.toEntity(execution));
        executionService.triggerExecution(execution);
        ArgumentCaptor<OrderEntity> argumentCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderService,times(2)).updateOrder(argumentCaptor.capture());
        List<OrderEntity> executedOrders =  argumentCaptor.getAllValues();
        assertEquals(executedOrders.size(),2);
        assertEquals(executedOrders.get(0).getOrderId(),Long.valueOf(1));
        assertEquals(executedOrders.get(0).getRemainingQuantity(),0);
        assertEquals(executedOrders.get(1).getOrderId(),Long.valueOf(4));
        assertEquals(executedOrders.get(1).getRemainingQuantity(),20);
    }

    @Test
    public void testAskExecutionTrigger(){
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        List<OrderEntity> activeOrders = TestUtils.mockActiveOrders();
        when(orderService.getActiveOrdersForBook(matches("book"))).thenReturn(activeOrders);
        Execution execution = new Execution();
        execution.setPrice(39);
        execution.setQuantity(30);
        execution.setBookName("book");
        execution.setInstrumentId(42);
        execution.setType(ExecutionType.ASK);
        when(executionRepository.save(any())).thenReturn(ExecutionEntity.toEntity(execution));
        executionService.triggerExecution(execution);
        ArgumentCaptor<OrderEntity> argumentCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderService,times(1)).updateOrder(argumentCaptor.capture());
        List<OrderEntity> executedOrders =  argumentCaptor.getAllValues();
        assertEquals(executedOrders.size(),1);
        assertEquals(executedOrders.get(0).getOrderId(),Long.valueOf(3));
        assertEquals(executedOrders.get(0).getRemainingQuantity(),20);
    }

    @Test
    public void testStopExecutionService(){
        assertTrue(executionService.isRunning());
        assertEquals(executionService.getTriggeredExecutionsForBook("book").size(),1);
        executionService.stop();
        assertFalse(executionService.isRunning());
        //check map is cleared
        assertEquals(executionService.getTriggeredExecutionsForBook("book").size(),0);
    }
}
