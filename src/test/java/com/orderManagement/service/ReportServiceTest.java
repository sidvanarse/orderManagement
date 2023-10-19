package com.orderManagement.service;

import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.model.Report;
import com.orderManagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private BookService bookService;
    @Mock
    private OrderService orderService;
    @Mock
    private ExecutionService executionService;
    @InjectMocks
    private ReportService reportService;

    @Test
    public void testGenerateReportForBook(){
        when(bookService.isBookClosed(matches("book"))).thenReturn(true);
        List<OrderEntity> activeOrders =  TestUtils.mockActiveOrders();
        List<OrderEntity> completedOrders = new ArrayList<>();
        List<OrderEntity> pendingOrders = new ArrayList<>();
        List<ExecutionEntity> executionEntities = new ArrayList<>();
        pendingOrders.add(activeOrders.get(0));
        pendingOrders.add(activeOrders.get(1));
        pendingOrders.add(activeOrders.get(2));
        pendingOrders.add(activeOrders.get(3));
        completedOrders.add(activeOrders.get(4));
        completedOrders.add(activeOrders.get(5));
        executionEntities.add(new ExecutionEntity());
        when(orderService.getCompletedOrdersForBook(matches("book"))).thenReturn(completedOrders);
        when(orderService.getPendingOrdersForBook(matches("book"))).thenReturn(pendingOrders);
        when(executionService.getTriggeredExecutionsForBook(matches("book"))).thenReturn(executionEntities);
        Report report = reportService.generateReportForBook("book");
        assertEquals(report.getBookName(),"book");
        assertEquals(report.getBookStatus(),"Closed");
        assertEquals(report.getCompletedOrders().size(),2);
        assertEquals(report.getPendingOrders().size(),4);
        assertEquals(report.getTriggeredExecutions().size(),1);
    }
}
