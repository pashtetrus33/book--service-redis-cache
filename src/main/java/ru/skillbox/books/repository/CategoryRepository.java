package ru.skillbox.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.books.entity.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
}