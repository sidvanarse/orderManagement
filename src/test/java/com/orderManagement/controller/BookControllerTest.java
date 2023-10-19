package com.orderManagement.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    @Mock
    private BookService bookService;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private BookController bookController;

    @Test
    public void testGetBookApi(){
        Book book = new Book();
        book.setBookName("book");
        when(bookService.findByName(matches("book"))).thenReturn(Optional.of(book));
        ResponseEntity<Book> responseEntity =  bookController.getBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        //Simulate book not present
        when(bookService.findByName(matches("book"))).thenReturn(Optional.empty());
        responseEntity =  bookController.getBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testSaveBookApi(){

        ResponseEntity<String> responseEntity =  bookController.saveBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book already exists scenario
        doThrow(new BookAlreadyExistsException("Book exists"))
                .when(bookService)
                .save(anyString());

        responseEntity =  bookController.saveBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .save(anyString());

        responseEntity =  bookController.saveBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testCloseBookApi(){
        ResponseEntity<String> responseEntity =  bookController.closeBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book does not exists scenario
        doThrow(new BookDoesNotExistsException("Book doest not exists"))
                .when(bookService)
                .closeBook(anyString());

        responseEntity =  bookController.closeBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .closeBook(anyString());

        responseEntity =  bookController.closeBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testOpenBookApi(){
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(new ArrayList<>());
        ResponseEntity<String> responseEntity =  bookController.openBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate book not available scenario
        when(bookService.isBookAvailable(matches("book"))).thenReturn(false);
        responseEntity =  bookController.openBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate can not open book because of pending orders
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(Arrays.asList(new OrderEntity()));
        responseEntity =  bookController.openBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);

        //Simulate internal server error scenario
        when(bookService.isBookAvailable(matches("book"))).thenReturn(true);
        when((orderService.getPendingOrdersForBook(matches("book")))).thenReturn(new ArrayList<>());

        doThrow(new RuntimeException("Internal error"))
                .when(bookService)
                .openBook(anyString());

        responseEntity =  bookController.openBook("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
