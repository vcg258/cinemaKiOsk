package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.vo.PointHistoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointHistoryMapper {
    // 포인트 적립이 된 영화명까지 포함 (페이징)
    List<PointHistoryVO> selectByMovieNameAll(int page);

    // 포인트 환불을 위한 메서드 (거래내역까지 조회)
    List<PointHistoryVO> selectByPayment(String paymentId);
}
