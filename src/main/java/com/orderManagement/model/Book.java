package com.orderManagement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Represents an order book in the Order Management System.
 * An order book includes a name and a status to determine whether it is open or closed.
 * Lombok annotations are used to generate getter, setter, and constructors for its fields.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Book {
    private String bookName;
    @EqualsAndHashCode.Exclude
    private boolean isClosed;

}
