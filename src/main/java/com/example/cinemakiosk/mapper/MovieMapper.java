package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface MovieMapper {

    // 전체 영화 목록 조회
    @Select("SELECT * FROM movie ORDER BY create_at DESC")
    List<MovieEntity> findAll();

    // 장르별 조회
    @Select("SELECT * FROM movie WHERE genre = #{genre}")
    List<MovieEntity> findByGenre(@Param("genre") String genre);

    // 현재 상영 중인 영화 조회 (오늘 날짜 기준)
    @Select("SELECT * FROM movie WHERE start_at <= NOW() AND end_at >= NOW()")
    List<MovieEntity> findNowPlaying();

    // 영화 등록
    @Insert("""
            INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at)
            VALUES (#{title}, #{genre}, #{rating}, #{runtime}, #{director}, #{actors}, #{description}, #{startAt}, #{endAt}, NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "movieId")
    int insert(MovieEntity movie);

    // 영화 정보 수정
    @Update("""
            UPDATE movie
            SET title = #{title},
                genre = #{genre},
                rating = #{rating},
                runtime = #{runtime},
                director = #{director},
                actors = #{actors},
                description = #{description},
                start_at = #{startAt},
                end_at = #{endAt}
            WHERE movie_id = #{movieId}
            """)
    int update(MovieEntity movie);

    // 영화 삭제
    @Delete("DELETE FROM movie WHERE movie_id = #{movieId}")
    int delete(@Param("movieId") Long movieId);

    // 제목 키워드 검색
    @Select("SELECT * FROM movie WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<MovieEntity> searchByTitle(@Param("keyword") String keyword);
}