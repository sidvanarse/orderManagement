package com.orderManagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * Represents an order in the Order Management System.
 * An order includes details such as the financial instrument ID, quantity, entry date,
 * order type, price, and the associated book name.
 * Lombok annotations are used to generate getter, setter, and constructors for its fields.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Order {
    private Long orderId;
    private int instrumentId;
    private int quantity;
    private LocalDateTime entryDate;
    private OrderType type;
    private double price;
    private String bookName;
}
