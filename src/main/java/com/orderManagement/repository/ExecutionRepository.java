package com.orderManagement.repository;

import com.orderManagement.entity.ExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for managing {@link ExecutionEntity} instances.
 * Extends {@link JpaRepository} and uses the {@code @Repository} annotation.
 */
@Repository
public interface ExecutionRepository extends JpaRepository<ExecutionEntity,Long> {
}
