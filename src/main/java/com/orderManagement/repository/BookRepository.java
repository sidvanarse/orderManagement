package com.orderManagement.repository;

import com.orderManagement.entity.BookEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 * Spring Data JPA repository for managing {@link BookEntity} instances.
 * Extends {@link JpaRepository} and uses the {@code @Repository} annotation.
 */
@Repository
public interface BookRepository  extends JpaRepository<BookEntity,Long> {
    /**
     * Retrieve a {@link BookEntity} by its book name.
     *
     * @param bookName The name of the book to search for.
     * @return The {@link BookEntity} with the specified book name,
     *         or {@code null} if no matching book is found.
     */
    BookEntity findByBookName(String bookName);
}
