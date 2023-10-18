package com.orderManagement.service;

import com.orderManagement.exceptions.BookAlreadyExistsException;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.model.Book;

import java.util.Optional;

/**
 * The BookService interface defines the contract for services that manage book-related operations.
 */
public interface BookService {
    /**
     * Retrieves a book by its name.
     *
     * @param name The name of the book to be retrieved.
     * @return The book with the specified name.
     */
    Optional<Book> findByName(String name);
    /**
     * Saves a new book with the given name.
     *
     * @param bookName The name of the book to be saved.
     */
    void save(String bookName) throws BookAlreadyExistsException;

    /**
     * Checks the status of book either closed or open.
     *
     * @param bookName The name of the book for which status needs to be checked.
     * @return True or false depending on book's status.
     */
    boolean isBookClosed(String bookName) throws BookDoesNotExistsException;
    /**
     * Checks the if the given book present in order management system.
     *
     * @param bookName The name of the book for which availability needs to be checked.
     * @return True or false depending on book's availability.
     */
    boolean isBookAvailable(String bookName);
    /**
     * Adds the book to order management system if does not exist.
     *
     * @param bookName The name of the book which needs to be added to the system.
     */
    void addBookIfDoesNotExists(String bookName);
    /**
     * Closes the book.
     *
     * @param bookName The name of the book which needs to be closed.
     */
    void closeBook(String bookName);

    /**
     * Opens the book.
     *
     * @param bookName The name of the book which needs to be opened.
     */
    void openBook(String bookName);

}
