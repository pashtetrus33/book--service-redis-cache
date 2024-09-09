package ru.skillbox.books.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse implements Serializable {

    private UUID id;

    private String name;

    private String author;

    private String category;

}