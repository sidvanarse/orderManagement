package com.orderManagement.repository;

import com.orderManagement.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for managing {@link OrderEntity} instances.
 * Extends {@link JpaRepository} and uses the {@code @Repository} annotation.
 */
@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity,Long> {
}
