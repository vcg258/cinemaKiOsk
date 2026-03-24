package com.example.cinemakiosk.repository;


import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

        List<MovieEntity> findByTitleContaining(String keyword);
        List<MovieEntity> findByGenre(String genre);

    }
