package com.orderManagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an execution in the Order Management System.
 * An execution includes details such as the financial instrument ID, quantity, execution type,
 * price, and the associated book name.
 * Lombok annotations are used to generate getter, setter, and constructors for its fields.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Execution {
    private int instrumentId;
    private int quantity;
    private ExecutionType type;
    private double price;
    private String bookName;
}
