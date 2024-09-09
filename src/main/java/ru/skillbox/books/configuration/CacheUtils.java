package ru.skillbox.books.configuration;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class CacheUtils {

    // Кодирование строки в Base64
    public String encodeBase64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public String decodeBase64(String encodedValue) {
        return new String(Base64.getDecoder().decode(encodedValue));
    }
}