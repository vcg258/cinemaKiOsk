package com.example.cinemakiosk.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
public class MovieResponseDTO<E> {

    private List<E> dtoList;

}
