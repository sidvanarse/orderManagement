package com.orderManagement.controller;

import com.orderManagement.model.Book;
import com.orderManagement.model.Report;
import com.orderManagement.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Report API", description = "API to get reports")
@RequestMapping("/report")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Gets a report by bookName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved report"),
            @ApiResponse(responseCode = "500", description = "Error generating the report")
    })
    @GetMapping("/{bookName}")
    public ResponseEntity<?> getReport(@PathVariable String bookName) {
        try{
            log.info("Generating report for the book {}", bookName);
            Report report = reportService.generateReportForBook(bookName);
            log.info("Successfully generated report for the book {}", bookName);
            return ResponseEntity.ok(report);
        }
        catch (Exception exception){
            log.error("Error generating report for the book {}. Exception is {}",bookName,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
