package com.example.cinemakiosk.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RatingConverter implements AttributeConverter<Rating, String> { // Java타입, DB타입
    /**
     * enum -> DB로 넘길때 변환 (TWELVE -> "12")
     * @param attribute Rating 속성
     * @return String타입 숫자
     */
    @Override
    public String convertToDatabaseColumn(Rating attribute) {
        if (attribute == null) return null;
        return attribute.getConversion();
    }

    /**
     * DB -> enum로 넘길때 변환 (DB에서 읽어 오는거 "12" -> TWELVE)
     * @param dbData DB (String)
     * @return 단어로 변환
     */
    @Override
    public Rating convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Rating.fromConversion(dbData);
    }
}


