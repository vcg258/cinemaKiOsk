package com.example.cinemakiosk.repository;


import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

        List<MovieEntity> findByTitleContaining(String keyword);

        List<MovieEntity> findByRating(Rating rating);

        List<MovieEntity> findByGenre(String genre);

        Optional<MovieEntity> findByTitle(String title);
}
