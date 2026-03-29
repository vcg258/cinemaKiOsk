package com.example.cinemakiosk.domain.enums;


public enum Rating {
    ALL,
    TWELVE,
    FIFTEEN,
    NINETEEN;

    /* TODO JPA에서 테이블이 만들어질때 enum에 숫자를 넣으려고 하면 @Converter을 사용해야함(배우지 않음) 안쓴다면 enum타입으로 숫자 넣기 불가능 그래서 해결방법이 있음
        1) 그냥 String을 사용하고 DTO, VO 에서는 enum을 사용해서 지정한 문자를 제외한 값을 막는다.
        2) 깔끔하게 @Converter 배우고 사용하면서 익힌다.
        3) 초기값을 넣어주는 JPA 방식이 있는데 여기서 ALTER 쿼리문을 추가하여 수정한다.
     */
    public String getConversion() {
        return switch (this) {
            case ALL -> "ALL";
            case TWELVE -> "12";
            case FIFTEEN -> "15";
            case NINETEEN -> "19";
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


