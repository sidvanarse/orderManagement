package com.orderManagement.controller;

import com.orderManagement.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {
    @Mock
    private ReportService reportService;
    @InjectMocks
    private ReportController reportController;

    @Test
    public void testGetReportApi(){
        ResponseEntity<?>  responseEntity = reportController.getReport("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(reportService)
                .generateReportForBook(anyString());

        responseEntity =  reportController.getReport("book");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
