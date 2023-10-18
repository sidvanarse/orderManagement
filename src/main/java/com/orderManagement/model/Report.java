package com.orderManagement.model;

import com.orderManagement.entity.ExecutionEntity;
import com.orderManagement.entity.OrderEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Report {

    private String bookName;

    private String bookStatus;

    private List<OrderEntity> completedOrders;

    private List<OrderEntity> pendingOrders;

    private List<ExecutionEntity> triggeredExecutions;

}
