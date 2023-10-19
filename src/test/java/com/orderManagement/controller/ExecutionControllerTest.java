package com.orderManagement.controller;

import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;
import com.orderManagement.service.ExecutionService;
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
public class ExecutionControllerTest {
    @Mock
    private ExecutionService executionService;
    @InjectMocks
    private ExecutionController executionController;

    @Test
    public void testTriggerExecutionApi(){
        Execution execution = new Execution();
        ResponseEntity<?> result =  executionController.triggerExecution(execution);
        assertEquals(result.getStatusCode(), HttpStatus.OK);


        //Simulate book doest not exists scenario.
        doThrow(new BookDoesNotExistsException("Book does not exists"))
                .when(executionService)
                .triggerExecution(any());
        result =  executionController.triggerExecution(execution);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate book is still open scenario
        doThrow(new BookOpenException("Book open exception"))
                .when(executionService)
                .triggerExecution(any());
        result =  executionController.triggerExecution(execution);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(executionService)
                .triggerExecution(any());

        result =  executionController.triggerExecution(execution);
        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
