package ru.skillbox.books.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequest {

    @NotBlank(message = "Название книги должно быть указано!")
    private String name;

    @NotBlank(message = "Автор книги должен быть указан!")
    private String author;

    @NotNull(message = "ID категории должно быть указано")
    private UUID categoryId;
}
