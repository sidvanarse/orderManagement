package com.orderManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;
import com.orderManagement.model.OrderType;
import com.orderManagement.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAddOrderApi() throws Exception{
        Order order = getOrder();
        String orderJson = objectMapper.writeValueAsString(order);
        when(orderService.addOrder(any())).thenReturn(order);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), orderJson);

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .addOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate OrderAlreadyExistsException
        doThrow(new OrderAlreadyExistsException("OrderAlreadyExistsException"))
                .when(orderService)
                .addOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .addOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .addOrder(any());
        mockMvc.perform(MockMvcRequestBuilders.post("/order/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testEditOrderApi() throws Exception{
        Order order = getOrder();
        order.setOrderId(Long.valueOf(2));
        String orderJson = objectMapper.writeValueAsString(order);
        when(orderService.editOrder(any())).thenReturn(order);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), orderJson);

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .editOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate BookDoesNotExistsException
        doThrow(new BookDoesNotExistsException("BookDoesNotExistsException"))
                .when(orderService)
                .editOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .editOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate InactiveOrderException
        doThrow(new InactiveOrderException("InactiveOrderException"))
                .when(orderService)
                .editOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .editOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/editOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testDeleteOrderApi() throws Exception{

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/deleteOrder/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Order deleted successfully");

        //Simulate book closed exception
        doThrow(new BookClosedException("Book is already closed"))
                .when(orderService)
                .deleteOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/deleteOrder/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate BookDoesNotExistsException
        doThrow(new BookDoesNotExistsException("BookDoesNotExistsException"))
                .when(orderService)
                .deleteOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/deleteOrder/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate OrderNotAvailableException
        doThrow(new OrderNotAvailableException("OrderNotAvailableException"))
                .when(orderService)
                .deleteOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/deleteOrder/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(orderService)
                .deleteOrder(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/deleteOrder/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    private Order getOrder(){
        Order order = new Order();
        order.setBookName("book");
        order.setQuantity(40);
        order.setPrice(39.9);
        order.setType(OrderType.BUY);
        order.setInstrumentId(42);
        return order;
    }
}
