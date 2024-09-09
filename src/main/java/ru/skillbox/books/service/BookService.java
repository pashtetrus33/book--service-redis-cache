package ru.skillbox.books.service;

import ru.skillbox.books.web.dto.BookListResponse;
import ru.skillbox.books.web.dto.BookResponse;
import ru.skillbox.books.web.dto.UpsertBookRequest;

import java.util.UUID;

public interface BookService {
    BookListResponse findAllByCategory(String category, int page, int size);

    BookResponse findByNameAndAuthor(String name, String author);

    BookResponse save(UpsertBookRequest request);

    BookResponse update(UUID bookId, UpsertBookRequest request);

    void deleteById(UUID id);

    BookListResponse findAll(int page, int size);
}