package ru.skillbox.books.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.books.service.BookService;
import ru.skillbox.books.web.dto.BookListResponse;
import ru.skillbox.books.web.dto.BookResponse;
import ru.skillbox.books.web.dto.UpsertBookRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BookListResponse> findAll(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(bookService.findAll(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<BookResponse> getBookByNameAndAuthor(
            @RequestParam String name,
            @RequestParam String author) {

        return ResponseEntity.ok(bookService.findByNameAndAuthor(name, author));
    }

    @GetMapping("/category")
    public ResponseEntity<BookListResponse> getBooksByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BookListResponse bookListResponse = bookService.findAllByCategory(category, page, size);
        return ResponseEntity.ok(bookListResponse);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody UpsertBookRequest request) {
        BookResponse bookResponse = bookService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody UpsertBookRequest request) {
        BookResponse bookResponse = bookService.update(id, request);
        return ResponseEntity.ok(bookResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}