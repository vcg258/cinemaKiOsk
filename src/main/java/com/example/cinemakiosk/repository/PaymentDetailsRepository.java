package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetailsEntity, Long>{

//    @Query("select b from Board b where b.title like concat('%',:keyword,'%')")
//    Page<Board> findKeyword(String keyword, Pageable pageable);
//
//    /*1. 쿼리 메서드 : 메서드의 이름 자체가 쿼리의 구문으로 처리되는 기능.*/
//    //https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
//
//    //게시판에서 "6"이 포함된 게시물을 검색. content like '%keyword%'
//    Page<Board> findByContentContaining(String keyword, Pageable pageable);
//
//
//    /*
//    2. JPQL : @Query 를 사용해서 sql과 유사하게 엔티티 클래스의 정보를 이용해서 쿼리를 작성하는 기능.
//
//     */
//    @Query("Select b from Board b where b.content like concat('%', :keyword, '%')")
//    Page<Board> findByContent(String keyword, Pageable pageable);
//
//
//    //@EntityGraph에는 attributePaths라는 속성을 이용해서 같이 로딩해야하는 속성을 명시할 수 있음.
//    @EntityGraph(attributePaths = {"imageSet"})
//    @Query("select b from Board b where b.bno = :bno")
//    Optional<Board> findByBnoWithImage(Long bno);

}

