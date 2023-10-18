package com.orderManagement.entity;

import com.orderManagement.converter.ExecutionTypeToStringConverter;
import com.orderManagement.model.Execution;
import com.orderManagement.model.ExecutionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="EXECUTIONS")
public class ExecutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;
    private int instrumentId;
    private int quantity;
    @Convert(converter = ExecutionTypeToStringConverter.class)
    private ExecutionType type;
    private double price;
    private String bookName;

    /**
     * Converts a Execution model object to a ExecutionEntity.
     *
     * @param execution The Execution model to convert.
     * @return The corresponding ExecutionEntity.
     */
    public static ExecutionEntity toEntity(Execution execution){
        ExecutionEntity executionEntity = new ExecutionEntity();
        BeanUtils.copyProperties(execution,executionEntity);
        return executionEntity;
    }
    /**
     * Converts this ExecutionEntity to a Execution model object.
     *
     * @return The corresponding Execution model object.
     */
    public Execution toBean(){
        Execution execution = new Execution();
        BeanUtils.copyProperties(this,execution);
        return execution;
    }
}
