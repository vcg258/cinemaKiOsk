//package com.example.cinemakiosk.repository;
//
//import com.example.cinemakiosk.domain.StatisticsEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {
//
//    List<StatisticsEntity> findByScheduleEntity_Id(Long id);
//
//    List<StatisticsEntity> findByDay(String day);
//
//    List<StatisticsEntity> findByDate(LocalDateTime date);
//}
