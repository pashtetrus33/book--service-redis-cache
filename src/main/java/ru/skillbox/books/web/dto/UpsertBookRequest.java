package ru.skillbox.books.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpsertBookRequest {

    @NotBlank(message = "Название книги должно быть указано!")
    private String name;

    @NotBlank(message = "Автор книги должен быть указан!")
    private String author;

    @NotBlank(message = "Категория книги должна быть указана!")
    private String categoryName;
}