package com.orderManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.entity.OrderEntity;
import com.orderManagement.model.ExecutionType;
import com.orderManagement.model.Report;
import com.orderManagement.service.ReportService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportService reportService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetReportApi() throws Exception {
        Report report = getReport();

        String object = objectMapper.writeValueAsString(report);

        when(reportService.generateReportForBook(matches("book"))).thenReturn(report);

        mockMvc.perform(get("/report/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(object));

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(reportService)
                .generateReportForBook(anyString());

        mockMvc.perform(get("/report/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    private static Report getReport() {
        OrderEntity completeOrder = new OrderEntity();
        completeOrder.setRemainingQuantity(0);
        completeOrder.setBookName("book");
        completeOrder.setPrice(35.0);
        completeOrder.setQuantity(42);

        OrderEntity pendingOrder = new OrderEntity();
        pendingOrder.setRemainingQuantity(14);
        pendingOrder.setBookName("book");
        pendingOrder.setPrice(35.0);
        pendingOrder.setQuantity(42);

        ExecutionEntity executionEntity = new ExecutionEntity();
        executionEntity.setBookName("book");
        executionEntity.setInstrumentId(42);
        executionEntity.setType(ExecutionType.OFFER);
        executionEntity.setQuantity(16);
        executionEntity.setPrice(34);

        Report report = new Report();
        report.setBookName("book");
        report.setBookStatus("Closed");
        report.setCompletedOrders(Arrays.asList(completeOrder));
        report.setPendingOrders(Arrays.asList(pendingOrder));
        report.setTriggeredExecutions(Arrays.asList(executionEntity));
        return report;
    }
}
