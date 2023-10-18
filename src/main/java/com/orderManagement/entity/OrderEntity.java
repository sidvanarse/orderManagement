package com.orderManagement.entity;

import com.orderManagement.converter.BooleanConverter;
import com.orderManagement.converter.OrderTypeToStringConverter;
import com.orderManagement.model.Book;
import com.orderManagement.model.Order;
import com.orderManagement.model.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="ORDERS")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private int instrumentId;
    private int quantity;
    private int remainingQuantity;
    private LocalDateTime entryDate;

    @Convert(converter = BooleanConverter.class)
    private boolean isActive;
    @Convert(converter = OrderTypeToStringConverter.class)
    private OrderType type;
    private double price;
    private String bookName;

    private Long previousOrderId;

    /**
     * Converts a Order model object to a OrderEntity.
     *
     * @param order The Order model to convert.
     * @return The corresponding OrderEntity.
     */
    public static OrderEntity toEntity(Order order){
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(order,orderEntity);
        orderEntity.setRemainingQuantity(order.getQuantity());
        orderEntity.setActive(true);
        orderEntity.setOrderId(null);
        return orderEntity;
    }
    /**
     * Converts this OrderEntity to an Order model object.
     *
     * @return The corresponding Order model object.
     */
    public Order toBean(){
        Order order = new Order();
        BeanUtils.copyProperties(this,order);
        return order;
    }
    /**
     * Checks if order is complete or not.
     *
     * @return boolean value indicating is order is complete or not.
     */
    public boolean isOrderComplete(){
        return this.remainingQuantity == 0;
    }
}
