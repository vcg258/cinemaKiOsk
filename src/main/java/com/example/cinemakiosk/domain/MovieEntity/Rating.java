package com.example.cinemakiosk.domain.MovieEntity;


public enum Rating {
    ALL,
    TWELVE,
    FIFTEEN,
    NINETEEN;


    public String getConversion() {
        return switch (this) {
            case ALL -> "ALL";
            case TWELVE -> "12";
            case FIFTEEN -> "15";
            case NINETEEN -> "18";
        };
    }

    // String → enum 역변환
    public static Rating fromConversion(String value) {
        return switch (value) {
            case "ALL" -> ALL;
            case "12" -> TWELVE;
            case "15" -> FIFTEEN;
            case "19" -> NINETEEN;
            default -> throw new IllegalArgumentException("Unknown rating: " + value);
        };
    }

}


