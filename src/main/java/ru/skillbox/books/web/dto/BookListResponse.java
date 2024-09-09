package ru.skillbox.books.web.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookListResponse implements Serializable {
    private List<BookResponse> books = new ArrayList<>();

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}