package com.orderManagement.service;

import com.orderManagement.entity.BookEntity;
import com.orderManagement.exceptions.BookAlreadyExistsException;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.model.Book;
import com.orderManagement.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The BookServiceImpl implements BookService.
 * This service manages all book related operations.
 */
@Service
public class BookServiceImpl implements BookService, SmartLifecycle {

    private final BookRepository bookRepository;

    private ConcurrentHashMap<String,Book> booksMap = new ConcurrentHashMap<>();

    private volatile boolean isRunning;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public Optional<Book> findByName(String bookName) {
        BookEntity bookEntity =  bookRepository.findByBookName(bookName);
        if (!Objects.isNull(bookEntity)) {
            return Optional.of(bookEntity.toBean());
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void save(String bookName) throws BookAlreadyExistsException{
        if(isBookAvailable(bookName)){
            throw new BookAlreadyExistsException("Book with the name " + bookName + " already exists.");
        }
        addBookIfDoesNotExists(bookName);
    }

    @Override
    public boolean isBookClosed(String bookName) throws BookDoesNotExistsException {
        if(isBookAvailable(bookName)){
            return booksMap.get(bookName).isClosed();
        }
        throw new BookDoesNotExistsException("Book with the name " + bookName + " does not exists.");
    }
    @Override
    public boolean isBookAvailable(String bookName){
        if(booksMap.containsKey(bookName)){
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    public void addBookIfDoesNotExists(String bookName){
        booksMap.computeIfAbsent(bookName,key->{
            Book book = new Book();
            book.setBookName(bookName);
            book.setClosed(false);
            BookEntity bookEntity = bookRepository.save(BookEntity.toEntity(book));
            return bookEntity.toBean();
        });
    }
    @Override
    @Transactional
    public void closeBook(String bookName){
        if(isBookAvailable(bookName)){
            Book book = booksMap.get(bookName);
            book.setClosed(true);
            BookEntity bookEntity = bookRepository.save(BookEntity.toEntity(book));
            booksMap.put(bookName,bookEntity.toBean());
            return;
        }
        throw new BookDoesNotExistsException("Book with the name " + bookName + " does not exists.");
    }

    @Override
    @Transactional
    public void openBook(String bookName){
        Book book = booksMap.get(bookName);
        book.setClosed(false);
        BookEntity bookEntity = bookRepository.save(BookEntity.toEntity(book));
        booksMap.put(bookName,bookEntity.toBean());
    }

    @Override
    public void start() {
        List<BookEntity> books = bookRepository.findAll();
        books.stream().forEach(book-> booksMap.put(book.getBookName(),book.toBean()));
        isRunning = true;
    }

    @Override
    public void stop() {
        booksMap.clear();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
