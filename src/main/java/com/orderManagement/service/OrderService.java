package com.orderManagement.service;

import com.orderManagement.entity.OrderEntity;
import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;

import java.util.List;

/**
 * The OrderService interface defines the contract for services that manage order-related operations.
 */
public interface OrderService {
    /**
     * Adds a new order in order Management system if book is not closed.
     *
     * @param order Order that needs to be added.
     * @return Order object after new order is successfully placed or throws BookClosedException,OrderAlreadyExistsException.
     */
    Order addOrder(Order order) throws BookClosedException, OrderAlreadyExistsException,OrderNotAvailableException;
    /**
     * Edits an order Management system if book is not closed.
     *
     * @param order new edited order.
     * @return Edited object order after order is successfully edited or throws BookDoesNotExistsException,BookClosedException,OrderNotAvailableException.
     */
    Order editOrder(Order order) throws BookDoesNotExistsException,BookClosedException, OrderNotAvailableException, InactiveOrderException;
    /**
     * Deletes an order Management system if book is not closed.
     *
     * @param orderId id of Order to be deleted.
     */
    void deleteOrder(Long orderId) throws BookDoesNotExistsException,BookClosedException,OrderNotAvailableException;
    /**
     * Given a bookName it retrieves all active orders.
     *
     * @param bookName name of the book for which active orders needs to be fetched.
     * @return List of active orders  for a book.
     */
    List<OrderEntity> getActiveOrdersForBook(String bookName);
    /**
     * Updates data for an order object.
     *
     * @param orderEntity new order object with updated data.
     */
    void updateOrder(OrderEntity orderEntity);

    /**
     * Given a bookName it retrieves all completed orders.
     *
     * @param bookName name of the book for which completed orders needs to be fetched.
     * @return List of completed orders  for a book.
     */
    List<OrderEntity> getCompletedOrdersForBook(String bookName);

    /**
     * Given a bookName it retrieves all pending orders.
     *
     * @param bookName name of the book for which pending orders needs to be fetched.
     * @return List of pending orders  for a book.
     */
    List<OrderEntity> getPendingOrdersForBook(String bookName);
}
