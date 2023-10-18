package com.orderManagement.service;

import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;

import java.util.List;

/**
 * The ExecutionService interface defines the contract for services that manage execution-related operations.
 */
public interface ExecutionService {
    /**
     * Trigger a new execution for a book in order Management system if book closed.
     *
     * @param execution Execution that needs to be triggered.
     */
     void triggerExecution(Execution execution) throws BookDoesNotExistsException, BookOpenException;
    /**
     * Given a bookName it retrieves all executed executions
     *
     * @param bookName name of the book for which executions needs to be fetched.
     * @return List of executed executions  for a book.
     */
     List<ExecutionEntity> getTriggeredExecutionsForBook(String bookName);
}
