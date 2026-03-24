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

}


