package com.orderManagement.entity;


import com.orderManagement.converter.BooleanConverter;
import com.orderManagement.model.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
/**
 * Represents a book entity in the Order Management System.
 * A book entity corresponds to a book record in the database.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="BOOKS")
public class BookEntity {
    @Id
    private String bookName;
    @Convert(converter = BooleanConverter.class)
    private boolean isClosed;
    /**
     * Converts a Book model object to a BookEntity.
     *
     * @param book The Book model to convert.
     * @return The corresponding BookEntity.
     */
    public static BookEntity toEntity(Book book){
        BookEntity bookEntity = new BookEntity();
        BeanUtils.copyProperties(book,bookEntity);
        return bookEntity;
    }
    /**
     * Converts this BookEntity to a Book model object.
     *
     * @return The corresponding Book model object.
     */
    public Book toBean(){
        Book book = new Book();
        BeanUtils.copyProperties(this,book);
        return book;
    }
}
