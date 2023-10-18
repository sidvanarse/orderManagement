package com.orderManagement.controller;

import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;
import com.orderManagement.service.ExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Execution API", description = "API to manage executions")
@RequestMapping("/execution")
@Slf4j
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }
    @Operation(summary = "Triggers a execution against a book orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully triggered an execution."),
            @ApiResponse(responseCode = "400", description = "Order book is still open. Cant run executions.|| Book doest not exists exception."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while saving a order.")
    })
    @PostMapping("/triggerExecution")
    public ResponseEntity<?> triggerExecution(@RequestBody Execution execution) {
        try{
            log.info("Received execution trigger request ",execution);
            executionService.triggerExecution(execution);
            String success = "Execution triggered successfully";
            log.info(success);
            return ResponseEntity.ok(success);
        }
        catch (BookDoesNotExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (BookOpenException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while executing trigger for book {}. Exception {}",execution.getBookName(),exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
