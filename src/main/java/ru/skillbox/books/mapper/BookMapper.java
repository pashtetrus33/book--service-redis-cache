package ru.skillbox.books.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import ru.skillbox.books.entity.Book;
import ru.skillbox.books.entity.Category;
import ru.skillbox.books.web.dto.BookListResponse;
import ru.skillbox.books.web.dto.BookResponse;
import ru.skillbox.books.web.dto.UpsertBookRequest;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book requestToBook(UpsertBookRequest request);

    @Mapping(source = "bookId", target = "id")
    Book requestToBook(UUID bookId, UpsertBookRequest request);

    @Mapping(source = "category.name", target = "category")
    BookResponse bookToResponse(Book book);

    void updateBookFromRequest(@MappingTarget Book book, UpsertBookRequest request);

    @Mapping(source = "content", target = "books")
    @Mapping(source = "totalElements", target = "totalElements")
    @Mapping(source = "totalPages", target = "totalPages")
    @Mapping(source = "number", target = "currentPage")
    @Mapping(source = "size", target = "pageSize")
    BookListResponse bookPageToBookListResponse(Page<Book> books);

    default BookListResponse bookListToBookListResponse(Page<Book> bookPage) {
        BookListResponse response = new BookListResponse();
        response.setBooks(bookPage.getContent().stream()
                .map(this::bookToResponse)
                .toList());

        response.setTotalElements(bookPage.getTotalElements());
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(bookPage.getNumber());
        response.setPageSize(bookPage.getSize());
        return response;
    }
}