package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    // 장르별 조회
    List<MovieEntity> findByGenre(String genre);

    // 제목 키워드 검색
    List<MovieEntity> findByTitleContaining(String keyword);

    // 현재 상영 중인 영화
    @Query("SELECT m FROM MovieEntity m WHERE m.startAt <= :now AND m.endAt >= :now")
    List<MovieEntity> findNowPlaying(@Param("now") LocalDateTime now);
}