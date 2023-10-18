package com.orderManagement.service;

import com.orderManagement.model.Report;
import org.springframework.stereotype.Service;
/**
 * The ReportService class provides methods for generating reports related to books, orders, and executions.
 * It is responsible for creating reports containing information about a specific book, such as its status, completed
 * and pending orders, and triggered executions.
 */
@Service
public class ReportService {
    private final BookService bookService;

    private final OrderService orderService;

    private final ExecutionService executionService;
    /**
     * Constructs a new ReportService with the specified dependencies.
     *
     * @param bookService      An instance of the BookService to access book-related information.
     * @param orderService     An instance of the OrderService to access order-related information.
     * @param executionService An instance of the ExecutionService to access execution-related information.
     */
    public ReportService(BookService bookService, OrderService orderService, ExecutionService executionService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.executionService = executionService;
    }
    /**
     * Generates a report for a specific book, including its status, completed orders, pending orders, and triggered executions.
     *
     * @param bookName The name of the book for which the report is to be generated.
     * @return A Report object containing information about the specified book.
     */
    public Report generateReportForBook(String bookName){
        Report report = new Report();
        boolean isBookClosed = bookService.isBookClosed(bookName);
        report.setBookName(bookName);
        report.setBookStatus(isBookClosed ? "Closed" : "Open");
        report.setCompletedOrders(orderService.getCompletedOrdersForBook(bookName));
        report.setPendingOrders(orderService.getPendingOrdersForBook(bookName));
        report.setTriggeredExecutions(executionService.getTriggeredExecutionsForBook(bookName));
        return report;
    }
}
