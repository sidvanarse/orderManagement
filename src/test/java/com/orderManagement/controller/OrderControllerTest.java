package com.orderManagement.controller;

import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;
import com.orderManagement.model.OrderType;
import com.orderManagement.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderController orderController;

    @Test
    public void testAddOrderApi(){
        Order order = getOrder();
        ResponseEntity<?>  responseEntity = orderController.addOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .addOrder(any());
        responseEntity =  orderController.addOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate OrderAlreadyExistsException
        doThrow(new OrderAlreadyExistsException("OrderAlreadyExistsException"))
                .when(orderService)
                .addOrder(any());
        responseEntity =  orderController.addOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .addOrder(any());
        responseEntity =  orderController.addOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .addOrder(any());

        responseEntity =  orderController.addOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Test
    public void testEditOrderApi(){
        Order order = getOrder();
        order.setOrderId(Long.valueOf(2));
        ResponseEntity<?>  responseEntity = orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .editOrder(any());
        responseEntity =  orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate BookDoesNotExistsException
        doThrow(new BookDoesNotExistsException("BookDoesNotExistsException"))
                .when(orderService)
                .editOrder(any());
        responseEntity =  orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .editOrder(any());
        responseEntity =  orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate InactiveOrderException
        doThrow(new InactiveOrderException("InactiveOrderException"))
                .when(orderService)
                .editOrder(any());
        responseEntity =  orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .editOrder(any());

        responseEntity =  orderController.editOrder(order);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Test
    public void testDeleteOrderApi(){
        ResponseEntity<?>  responseEntity = orderController.deleteOrder(Long.valueOf(2));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .deleteOrder(any());
        responseEntity =  orderController.deleteOrder(Long.valueOf(2));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate BookDoesNotExistsException
        doThrow(new BookDoesNotExistsException("BookDoesNotExistsException"))
                .when(orderService)
                .deleteOrder(any());
        responseEntity =  orderController.deleteOrder(Long.valueOf(2));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .deleteOrder(any());
        responseEntity =  orderController.deleteOrder(Long.valueOf(2));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .deleteOrder(any());

        responseEntity =  orderController.deleteOrder(Long.valueOf(2));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private Order getOrder(){
        Order order = new Order();
        order.setBookName("book");
        order.setQuantity(40);
        order.setPrice(39.9);
        order.setType(OrderType.BUY);
        order.setInstrumentId(42);
        order.setEntryDate(LocalDateTime.now());
        return order;
    }
}
