package ru.skillbox.books.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.books.configuration.CacheUtils;
import ru.skillbox.books.configuration.properties.AppCacheProperties;
import ru.skillbox.books.entity.Book;
import ru.skillbox.books.entity.Category;
import ru.skillbox.books.exeption.EntityNotFoundException;
import ru.skillbox.books.mapper.BookMapper;
import ru.skillbox.books.repository.BookRepository;
import ru.skillbox.books.repository.CategoryRepository;
import ru.skillbox.books.service.BookService;
import ru.skillbox.books.web.dto.BookListResponse;
import ru.skillbox.books.web.dto.BookResponse;
import ru.skillbox.books.web.dto.UpsertBookRequest;

import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final CacheService cacheService;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = AppCacheProperties.CacheNames.BOOKS, key = "#page + '_' + #size")
    public BookListResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookMapper.bookListToBookListResponse(bookRepository.findAll(pageable));
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = AppCacheProperties.CacheNames.BOOK_BY_NAME_AND_AUTHOR, key = "@cacheUtils.encodeBase64(#name + '_' + #author)")
    public BookResponse findByNameAndAuthor(String name, String author) {
        return bookMapper.bookToResponse(bookRepository.findBookByNameAndAuthor(name, author)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Книга с названием {0} и автором {1} не найдена", name, author))));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = AppCacheProperties.CacheNames.BOOKS_BY_CATEGORY, key = "@cacheUtils.encodeBase64(#name) + '_' + #page + '_' + #size")
    public BookListResponse findAllByCategory(String name, int page, int size) {
        // Проверяем, существует ли категория
        if (categoryRepository.findByName(name).isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("Категория {0} не найдена", name));
        }

        Pageable pageable = PageRequest.of(page, size);

        // Выполняем запрос для поиска книг по категории
        Page<Book> bookPage = bookRepository.findAllByCategoryName(name, pageable);

        // Преобразуем результат в DTO
        return bookMapper.bookPageToBookListResponse(bookPage);
    }


    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOK_BY_NAME_AND_AUTHOR,
                    key = "@cacheUtils.encodeBase64(#request.name + '_' + #request.author)",
                    condition = "#request.name != null  and #request.author != null"),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOKS, allEntries = true)
    })
    public BookResponse save(UpsertBookRequest request) {
        // Получаем категорию, если существует, или создаем новую
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(request.getCategoryName());
                    return categoryRepository.save(newCategory); // сразу сохраняем новую категорию
                });

        // Очищаем кэш по категории
        cacheService.clearCacheForCategory(request.getCategoryName());

        // Преобразуем запрос в сущность Book с помощью маппера
        Book book = bookMapper.requestToBook(request);

        // Устанавливаем категорию книги
        book.setCategory(category);

        // Сохраняем книгу и возвращаем её через маппер
        return bookMapper.bookToResponse(bookRepository.save(book));
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOK_BY_NAME_AND_AUTHOR,
                    key = "#request.name + '_' + #request.author",
                    condition = "#request.name != null  and #request.author != null"),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOKS, allEntries = true)
    })
    public BookResponse update(UUID id, UpsertBookRequest request) {
        // Получаем существующую книгу или выбрасываем исключение, если не найдена
        Book existingBook = bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Книга с ID {0} не найдена", id)));

        // Очищаем кэш по категории
        cacheService.clearCacheForCategory(request.getCategoryName());

        // Обновляем поля существующей книги значениями из запроса
        bookMapper.updateBookFromRequest(existingBook, request);

        // Обновляем категорию, если она изменилась
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(request.getCategoryName());
                    return categoryRepository.save(newCategory); // сохраняем новую категорию
                });
        existingBook.setCategory(category);

        // Сохраняем обновленную книгу
        return bookMapper.bookToResponse(bookRepository.save(existingBook));
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        // Проверяем наличие книги по id, иначе выбрасывается EntityNotFoundException
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Книга с ID {0} не найдена", id)));

        // Удаляем книгу, если она существует
        bookRepository.delete(book);

        // Очищаем кэш по книге
        deleteBook(book);

        // Очищаем кэш по категории
        cacheService.clearCacheForCategory(book.getCategory().getName());

        // Очищаем кэш по всем книгам
        cacheService.clearCacheForBooks();
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOKS, allEntries = true, beforeInvocation = true),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.BOOK_BY_NAME_AND_AUTHOR,
                    key = "@cacheUtils.encodeBase64(#book.name + '_' + #book.author)",
                    condition = "#book.name != null and #book.author != null", beforeInvocation = true)
    })
    void deleteBook(Book book) {
    }
}