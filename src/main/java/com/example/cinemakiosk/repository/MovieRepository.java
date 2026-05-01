package com.example.cinemakiosk.repository;


import com.example.cinemakiosk.domain.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
    public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

        Optional<MovieEntity> findByTitle(String title);
}
