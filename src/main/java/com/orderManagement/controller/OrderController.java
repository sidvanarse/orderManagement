package com.orderManagement.controller;

import com.orderManagement.exceptions.*;
import com.orderManagement.model.Order;
import com.orderManagement.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Order API", description = "API to manage orders")
@RequestMapping("/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @Operation(summary = "Adds a given order if doest not exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved order."),
            @ApiResponse(responseCode = "400", description = "Order book is already closed. Cant add order. || Unknown order received."),
            @ApiResponse(responseCode = "409", description = "Order already exists."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while saving a order.")
    })
    @PostMapping("/addOrder")
    public ResponseEntity<?> addOrder(@RequestBody Order order) {
        try{
            log.info("Received add order {}", order);
            Order placedOrder = orderService.addOrder(order);
            log.info("Successfully added order in system {}", order);
            return ResponseEntity.ok(placedOrder);
        }
        catch (BookClosedException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (OrderAlreadyExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
        catch (OrderNotAvailableException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while adding order {}. Exception is {} ",order,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
    @Operation(summary = "Edits an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited order."),
            @ApiResponse(responseCode = "400", description = "Order book is already closed. Cant edit order. || Book doest not exists exception || Order to edit not available. || Order is in inactive status"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while editing an order.")
    })
    @PostMapping("/editOrder")
    public ResponseEntity<?> editOrder(@RequestBody Order order) {
        try{
            log.info("Received edit order {}", order);
            Order placedOrder = orderService.editOrder(order);
            log.info("Successfully edited order in system {}", order);
            return ResponseEntity.ok(placedOrder);
        }
        catch (BookClosedException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (BookDoesNotExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (OrderNotAvailableException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (InactiveOrderException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while editing order {}. Exception is {} ",order,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
    @Operation(summary = "Deletes an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted order."),
            @ApiResponse(responseCode = "400", description = "Order book is already closed. Cant delete order. || Book doest not exists exception || Order to delete not available."),
            @ApiResponse(responseCode = "500", description = "Exception occurred while deleting a order.")
    })
    @PostMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try{
            log.info("Received delete order {}", orderId);
            orderService.deleteOrder(orderId);
            log.info("Successfully deleted order in system {}", orderId);
            return ResponseEntity.ok("Order deleted successfully");
        }
        catch (BookClosedException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (BookDoesNotExistsException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (OrderNotAvailableException exception){
            log.warn(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch (Exception exception){
            log.error("Error occurred while deleting order {}. Exception is {} ",orderId,exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
