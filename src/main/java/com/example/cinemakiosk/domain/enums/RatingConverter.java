package com.example.cinemakiosk.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class RatingConverter
        implements AttributeConverter<Rating, String>,
        org.springframework.core.convert.converter.Converter<String, Rating> {

    //  JPA: enum -> DB 컬럼
    @Override
    public String convertToDatabaseColumn(Rating attribute) {
        if (attribute == null) return null;
        return attribute.getConversion(); // TWELVE -> "12"
    }

    //  JPA: DB 컬럼 -> enum
    @Override
    public Rating convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Rating.fromConversion(dbData); // "12" -> TWELVE
    }

    //  Spring MVC: 폼 문자열 -> enum
    @Override
    public Rating convert(String source) {
        if (source == null || source.isBlank()) return null;
        return Rating.fromConversion(source.trim()); // "12" -> TWELVE
    }
}