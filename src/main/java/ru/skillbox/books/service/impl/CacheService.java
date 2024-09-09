package ru.skillbox.books.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.books.configuration.CacheUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;
    private final CacheUtils cacheUtils;

    public void clearCacheForCategory(String categoryName) {
        String pattern = "booksByCategory::" + cacheUtils.encodeBase64(categoryName) + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    // Очищаем все ключи, соответствующие паттерну "books::"
    public void clearCacheForBooks() {

        // Паттерн для поиска ключей
        String pattern = "books::";

        // Получаем все ключи, соответствующие паттерну
        Set<String> keys = redisTemplate.keys(pattern + "*");

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}