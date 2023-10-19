package com.orderManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.BookAlreadyExistsException;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.model.Book;
import com.orderManagement.service.BookService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private OrderService orderService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetBookApi() throws Exception{
        Book book = new Book();
        book.setBookName("book");

        String bookObject = objectMapper.writeValueAsString(book);

        when(bookService.findByName(matches("book"))).thenReturn(Optional.of(book));

        mockMvc.perform(get("/book/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(bookObject));

        //Simulate book not present
        when(bookService.findByName(matches("book"))).thenReturn(Optional.empty());

        mockMvc.perform(get("/book/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveBookApi() throws Exception{

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/book/save/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Book saved successfully");

        //Simulate book already exists scenario
        doThrow(new BookAlreadyExistsException("Book exists"))
                .when(bookService)
                .save(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/save/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .save(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/save/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testCloseBookApi() throws Exception{

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/book/close/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Book closed successfully");

        //Simulate book does not exists scenario
        doThrow(new BookDoesNotExistsException("Book doest not exists"))
                .when(bookService)
                .closeBook(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/close/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .closeBook(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/close/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testOpenBookApi() throws Exception{

        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/book/open/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Book opened successfully");

        //Simulate book not available scenario
        when(bookService.isBookAvailable(matches("book"))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/book/open/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate can not open book because of pending orders
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(Arrays.asList(new OrderEntity()));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/open/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isFailedDependency());

        //Simulate internal server error scenario
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(new ArrayList<>());

        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .openBook(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/open/book")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        
    }
}
