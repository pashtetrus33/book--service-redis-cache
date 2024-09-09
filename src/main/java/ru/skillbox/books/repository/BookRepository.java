package ru.skillbox.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.books.entity.Book;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @Query("SELECT b FROM books b JOIN b.category c WHERE c.name = :name")
    Page<Book> findAllByCategoryName(@Param("name") String name, Pageable pageable);

    Optional<Book> findBookByNameAndAuthor(String name, String author);
}