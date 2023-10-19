package com.orderManagement.service;

import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;
import com.orderManagement.model.OrderType;
import com.orderManagement.repository.OrdersRepository;
import com.orderManagement.utils.TestUtils;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private BookService bookService;
    @Mock
    private OrdersRepository ordersRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setupBeforeEach() {
        List<OrderEntity> activeOrders = TestUtils.mockActiveOrders();
        when(ordersRepository.findAll()).thenReturn(activeOrders);
        orderService.start();
    }

    @Test
    public void testCanNotAddOrdersIfBookIsClosed(){
        Order order = new Order();
        order.setBookName("book");
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        assertThrows(BookClosedException.class, () -> {
            orderService.addOrder(order);
        });
    }

    @Test
    public void testCanNotAddOrdersIfOrderWithSameIdExists(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(order.getOrderId()))).thenReturn(Optional.of(new OrderEntity()));
        assertThrows(OrderAlreadyExistsException.class, () -> {
            orderService.addOrder(order);
        });
    }

    @Test
    public void testCanNotAddOrdersIfOrderWithUnknownIdIsReceived(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(order.getOrderId()))).thenReturn(Optional.empty());
        assertThrows(OrderNotAvailableException.class, () -> {
            orderService.addOrder(order);
        });
    }

    @Test
    public void testAddOrderWhenValidOrderIsReceived(){
        Order order = new Order();
        order.setBookName("book");
        order.setQuantity(40);
        order.setPrice(39.9);
        order.setType(OrderType.BUY);
        order.setInstrumentId(42);
        order.setEntryDate(LocalDateTime.now());
        OrderEntity orderEntity = OrderEntity.toEntity(order);
        orderEntity.setOrderId(Long.valueOf(1));
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.save(any())).thenReturn(orderEntity);
        Order addedOrder = orderService.addOrder(order);
        assertEquals(addedOrder.getOrderId(), Long.valueOf(1));
        assertEquals(addedOrder.getInstrumentId(),42);
        assertEquals(addedOrder.getBookName(),"book");
    }

    @Test
    public void testCanNotEditOrderIfBookIsNotAvailableInSystem(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        when(bookService.isBookAvailable(matches("book"))).thenReturn(false);
        assertThrows(BookDoesNotExistsException.class, () -> {
            orderService.editOrder(order);
        });
    }

    @Test
    public void testCanNotEditOrderIfBookIsClosed(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        assertThrows(BookClosedException.class, () -> {
            orderService.editOrder(order);
        });
    }

    @Test
    public void testCanNotEditOrderIfPastOrderToEditIsNotAvailable(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.empty());
        assertThrows(OrderNotAvailableException.class, () -> {
            orderService.editOrder(order);
        });
    }

    @Test
    public void testCanNotEditOrderIfPastOrderToEditIsNotActive(){
        Order order = new Order();
        order.setBookName("book");
        order.setOrderId(Long.valueOf(2));
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(false);
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.of(pastOrder));
        assertThrows(InactiveOrderException.class, () -> {
            orderService.editOrder(order);
        });
    }

    @Test
    public void testEditOrderWhenValidEditOrderIsReceived(){
        Order order = new Order();
        order.setBookName("book");
        order.setQuantity(40);
        order.setPrice(39.9);
        order.setType(OrderType.BUY);
        order.setInstrumentId(42);
        order.setEntryDate(LocalDateTime.now());
        order.setOrderId(Long.valueOf(2));
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(true);
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.of(pastOrder));
        when(ordersRepository.save(any())).thenReturn(OrderEntity.toEntity(order));
        ArgumentCaptor<OrderEntity> argumentCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        orderService.editOrder(order);
        verify(ordersRepository,times(2)).save(argumentCaptor.capture());
        List<OrderEntity> capturedOrders =  argumentCaptor.getAllValues();
        assertEquals(capturedOrders.size(),2);
        assertEquals(capturedOrders.get(0).getPreviousOrderId(),Long.valueOf(2));
        assertEquals(capturedOrders.get(1).isActive(),false);
    }

    @Test
    public void testCanNotDeleteOrderIfOrderToDeleteIsNotPresentInDb(){
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(true);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.empty());
        assertThrows(OrderNotAvailableException.class, () -> {
            orderService.deleteOrder(Long.valueOf(2));
        });
    }

    @Test
    public void testCanNotDeleteOrderIfBookIsNotAvailableInSystem(){
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(true);
        pastOrder.setBookName("book");
        when(bookService.isBookAvailable(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.of(pastOrder));
        assertThrows(BookDoesNotExistsException.class, () -> {
            orderService.deleteOrder(Long.valueOf(2));
        });
    }

    @Test
    public void testCanNotDeleteOrderIfBookIsClosed(){
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(true);
        pastOrder.setBookName("book");
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.of(pastOrder));
        assertThrows(BookClosedException.class, () -> {
            orderService.deleteOrder(Long.valueOf(2));
        });
    }

    @Test
    public void testCanDeleteOrderIfORderIdIsValid(){
        OrderEntity pastOrder = new OrderEntity();
        pastOrder.setActive(true);
        pastOrder.setBookName("book");
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when(bookService.isBookClosed(matches("book"))).thenReturn(false);
        when(ordersRepository.findById(eq(Long.valueOf(2)))).thenReturn(Optional.of(pastOrder));
        orderService.deleteOrder(Long.valueOf(2));
        ArgumentCaptor<OrderEntity> argumentCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(ordersRepository,times(1)).save(argumentCaptor.capture());
        List<OrderEntity> capturedOrders =  argumentCaptor.getAllValues();
        assertEquals(capturedOrders.size(),1);
        assertEquals(capturedOrders.get(0).isActive(),false);
    }

    @Test
    public void testGetActiveOrdersForBook(){
        assertEquals(orderService.getActiveOrdersForBook("book").size(),6);
    }

    @Test
    public void testGetCompletedOrdersForBook(){
        assertEquals(orderService.getCompletedOrdersForBook("book").size(),2);
    }


    @Test
    public void testGetPendingOrdersForBook(){
        assertEquals(orderService.getPendingOrdersForBook("book").size(),4);
    }

    @Test
    public void testUpdateOrder(){
        orderService.updateOrder(new OrderEntity());
        verify(ordersRepository,times(1)).save(any());
    }

    @Test
    public void testStopOrderService(){
        assertTrue(orderService.isRunning());
        assertEquals(orderService.getActiveOrdersForBook("book").size(),6);
        orderService.stop();
        assertFalse(orderService.isRunning());
        //check map is cleared
        assertEquals(orderService.getActiveOrdersForBook("book").size(),0);
    }



}
