package com.orderManagement.utils;

import com.orderManagement.entity.OrderEntity;
import com.orderManagement.model.OrderType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static List<OrderEntity> mockActiveOrders(){
        OrderEntity orderEntity = createOrderEntity(Long.valueOf(1),42,50,40, OrderType.BUY,50);
        OrderEntity orderEntity1 = createOrderEntity(Long.valueOf(2),43,50,40,OrderType.BUY,50);
        OrderEntity orderEntity2 = createOrderEntity(Long.valueOf(3),42,50,37,OrderType.SELL,50);
        OrderEntity orderEntity3 = createOrderEntity(Long.valueOf(4),42,40,41,OrderType.BUY,40);
        OrderEntity orderEntity4 = createOrderEntity(Long.valueOf(5),44,40,42,OrderType.BUY,0);
        OrderEntity orderEntity5 = createOrderEntity(Long.valueOf(6),44,40,41,OrderType.SELL,0);
        List<OrderEntity> orderEntities = new ArrayList<>();
        orderEntities.add(orderEntity);
        orderEntities.add(orderEntity1);
        orderEntities.add(orderEntity2);
        orderEntities.add(orderEntity3);
        orderEntities.add(orderEntity4);
        orderEntities.add(orderEntity5);
        return orderEntities;
    }

    private static OrderEntity createOrderEntity(Long orderId,int instrumentId, int quantity,double price,OrderType orderType,int remainingQuantity){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setActive(true);
        orderEntity.setInstrumentId(instrumentId);
        orderEntity.setQuantity(quantity);
        orderEntity.setRemainingQuantity(remainingQuantity);
        orderEntity.setPrice(price);
        orderEntity.setType(orderType);
        orderEntity.setEntryDate(LocalDateTime.now());
        orderEntity.setBookName("book");
        return orderEntity;
    }

}
