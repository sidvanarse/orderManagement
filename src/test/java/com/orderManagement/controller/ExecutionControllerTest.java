package com.orderManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.exceptions.BookDoesNotExistsException;
import com.orderManagement.exceptions.BookOpenException;
import com.orderManagement.model.Execution;
import com.orderManagement.model.ExecutionType;
import com.orderManagement.service.ExecutionService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(ExecutionController.class)
public class ExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExecutionService executionService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testTriggerExecutionApi() throws Exception{
        Execution execution = new Execution();
        execution.setBookName("book");
        execution.setInstrumentId(42);
        execution.setType(ExecutionType.OFFER);
        execution.setQuantity(16);
        execution.setPrice(34);

        String executionJson = objectMapper.writeValueAsString(execution);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/execution/triggerExecution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(executionJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Execution triggered successfully");

        //Simulate book doest not exists scenario.
        doThrow(new BookDoesNotExistsException("Book does not exists"))
                .when(executionService)
                .triggerExecution(any());

         mockMvc.perform(MockMvcRequestBuilders.post("/execution/triggerExecution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(executionJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate book is still open scenario
        doThrow(new BookOpenException("Book open exception"))
                .when(executionService)
                .triggerExecution(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/execution/triggerExecution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(executionJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        //Simulate internal server error scenario
        doThrow(new RuntimeException("Internal error"))
                .when(executionService)
                .triggerExecution(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/execution/triggerExecution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(executionJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
