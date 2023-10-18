package com.orderManagement.controller;

import com.orderManagement.exceptions.BookAlreadyExistsException;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.model.Book;
import com.orderManagement.service.BookService;
import com.orderManagement.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Book API", description = "API to manage books")
@RequestMapping("/book")
@Slf4j
public class BookController {
    private final BookService bookService;

    private final OrderService orderService;

    @Autowired
    public BookController(BookService bookService, OrderService orderService) {
        this.bookService = bookService;
        this.orderService = orderService;
    }
    @Operation(summary = "Gets a book by bookName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book"),
            @ApiResponse(responseCode = "404", description = "The book does not exist")
    })
    @GetMapping("/{bookName}")
    public ResponseEntity<Book> getBook(@PathVariable String bookName) {
        log.info("Received get book request for {}",bookName);
        Optional<Book> book = bookService.findByName(bookName);
        if(book.isEmpty()){
            log.warn("Book {} not available in system ",bookName);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully retried book for {}",bookName);
        return ResponseEntity.ok(book.get());
    }
    @Operation(summary = "Saves a given book if doest not exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved book."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while saving a book."),
            @ApiResponse(responseCode = "409", description = "Book already exists.")
    })
    @PostMapping("/save/{bookName}")
    public ResponseEntity<String> saveBook(@PathVariable String bookName) {
        try{
            log.info("Received request to save book for {}",bookName);
            bookService.save(bookName);
        }
        catch (BookAlreadyExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while saving book {}. Exception {}",bookName,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving the book");
        }
        log.info("Successfully saved book {}",bookName);
        return ResponseEntity.ok("Book saved successfully");
    }
    @Operation(summary = "Closes the given book if exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully closed book."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while closing a book."),
            @ApiResponse(responseCode = "400", description = "Book doest not exists.")
    })
    @PostMapping("/close/{bookName}")
    public ResponseEntity<String> closeBook(@PathVariable String bookName) {
        try{
            log.info("Received request to close book for {}",bookName);
            bookService.closeBook(bookName);
        }
        catch (BookDoesNotExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while closing book {}. Exception {}",bookName,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while closing the book");
        }
        log.info("Book closed successfully {}",bookName);
        return ResponseEntity.ok("Book closed successfully");
    }
    @Operation(summary = "opens the given book if exists and all orders are completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully opened book."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while opening a book."),
            @ApiResponse(responseCode = "400", description = "Book doest not exists."),
            @ApiResponse(responseCode = "424", description = "Book can not be opened as it contains pending orders")
    })
    @PostMapping("/open/{bookName}")
    public ResponseEntity<String> openBook(@PathVariable String bookName) {
        try{
            log.info("Received request to open book for {}",bookName);
            if(!bookService.isBookAvailable(bookName)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested book doest not exists in system");
            }
            if(orderService.getPendingOrdersForBook(bookName).size() > 0){
                return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Book can not be opened as it contains pending orders");
            }
            bookService.openBook(bookName);
        }
        catch (Exception exception){
            log.error("Error occurred while opening book {}. Exception {}",bookName,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while opening the book");
        }
        log.info("Book opened successfully {}",bookName);
        return ResponseEntity.ok("Book opened successfully");
    }
}
