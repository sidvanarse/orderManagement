package com.orderManagement.service;

import com.orderManagement.entity.BookEntity;
import com.orderManagement.exceptions.BookAlreadyExistsException;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.model.Book;
import com.orderManagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setupBeforeEach() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookName("book");
        bookEntity.setClosed(false);

        BookEntity bookEntity2 = new BookEntity();
        bookEntity2.setBookName("book2");
        bookEntity2.setClosed(true);

        List<BookEntity> bookEntityList = new ArrayList<>();
        bookEntityList.add(bookEntity);
        bookEntityList.add(bookEntity2);

        when(bookRepository.findAll()).thenReturn(bookEntityList);
        bookService.start();
    }

    @Test
    public void testIfBookExistsThenItIsReturned(){
        Optional<Book> optionalBook =  bookService.findByName("book");
        assertTrue(optionalBook.isPresent());
        assertEquals(optionalBook.get().getBookName(),"book");
    }
    @Test
    public void testIfBookDoestNotExistsThenEmptyResultIsReturned(){
        Optional<Book> optionalBook =  bookService.findByName("book3");
        assertFalse(optionalBook.isPresent());
    }

    @Test
    public void testSaveBook(){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookName("book3");
        bookEntity.setClosed(false);
        when(bookRepository.save(any())).thenReturn(bookEntity);
        bookService.save("book3");
        verify(bookRepository,times(1)).save(any());
        Optional<Book> optionalBook =  bookService.findByName("book3");
        assertTrue(optionalBook.isPresent());
        assertEquals(optionalBook.get().getBookName(),"book3");
    }

    @Test
    public void testIfSameBookIsSavedAgainExceptionIsRaised(){
        assertThrows(BookAlreadyExistsException.class, () -> {
            bookService.save("book");
        });
    }

    @Test
    public void testIsBookClosedReturnsCorrectValue(){
        assertEquals(bookService.isBookClosed("book"),false);
        assertEquals(bookService.isBookClosed("book2"),true);
        assertThrows(BookDoesNotExistsException.class, () -> {
            bookService.isBookClosed("book3");
        });
    }

    @Test
    public void testIsBookAvailableReturnsCorrectStatus(){
        assertEquals(bookService.isBookAvailable("book"),true);
        assertEquals(bookService.isBookAvailable("book2"),true);
        assertEquals(bookService.isBookAvailable("book3"),false);
    }

    @Test
    public void testAddBookIfDoesNotExistsOnlyInsertsBookInDbIfDoestNotExits(){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookName("book3");
        bookEntity.setClosed(false);
        when(bookRepository.save(any())).thenReturn(bookEntity);
        bookService.addBookIfDoesNotExists("book");
        verify(bookRepository,times(0)).save(any());
        bookService.addBookIfDoesNotExists("book3");
        ArgumentCaptor<BookEntity> argumentCaptor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository,times(1)).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getBookName(),"book3");
    }

    @Test
    public void testCloseBookClosesBookIfAvailable(){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookName("book");
        bookEntity.setClosed(true);
        assertEquals(bookService.isBookClosed("book"),false);
        when(bookRepository.save(any())).thenReturn(bookEntity);
        bookService.closeBook("book");
        assertEquals(bookService.isBookClosed("book"),true);
        assertThrows(BookDoesNotExistsException.class, () -> {
            bookService.closeBook("book3");
        });
    }

    @Test
    public void testOpenBookOpensBook(){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookName("book2");
        bookEntity.setClosed(false);
        assertEquals(bookService.isBookClosed("book2"),true);
        when(bookRepository.save(any())).thenReturn(bookEntity);
        bookService.openBook("book2");
        assertEquals(bookService.isBookClosed("book2"),false);
    }

    @Test
    public void testStopBookService(){
        assertTrue(bookService.isRunning());
        bookService.stop();
        assertFalse(bookService.isRunning());
        //check map is cleared
        assertEquals(bookService.isBookAvailable("book"),false);
        assertEquals(bookService.isBookAvailable("book2"),false);
    }




}
