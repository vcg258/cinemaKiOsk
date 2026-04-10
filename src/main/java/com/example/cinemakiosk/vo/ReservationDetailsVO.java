package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsVO {
    private String id;                     // 예매 고유번호
    private ScheduleVO schedule;           //  스케쥴 정보
    private MemberVO phone;                  //  회원 번호
    private List<ReservationSeatVO> seats; //  예매한 좌석들의 정보
    private boolean returned;
    private LocalDateTime createAt; //  예약 시간

    /**
     * VO -> DTO
     * @param reservationDetailsVO
     * @return DTO
     */
    public static ReservationDetailsDTO toDTO(ReservationDetailsVO reservationDetailsVO) {

        List<ReservationSeatVO> reservationSeatVOs = reservationDetailsVO.getSeats();
        List<ReservationSeatDTO> reservationSeatDTOs = new ArrayList<>();
        if (reservationSeatVOs != null){
            for (ReservationSeatVO reservationSeatVO : reservationSeatVOs) {
                reservationSeatDTOs.add(ReservationSeatVO.toDTO(reservationSeatVO));
            }
        }

        return ReservationDetailsDTO.builder()
                .id(reservationDetailsVO.getId())
                .schedule(ScheduleVO.toDTO(reservationDetailsVO.getSchedule()))
                .phone(MemberVO.toDTO(reservationDetailsVO.getPhone()))
                .seats(reservationSeatDTOs)
                .returned(reservationDetailsVO.isReturned())
                .createAt(reservationDetailsVO.getCreateAt())
                .build();
    }

    /**
     * 해당 객체가 가진 seat의 Name을 반환받기 위해서 사용함.
     * @return seats변수 속의 seatName 
     */
    public List<String> getSeatName(){
        List<String> seatName = new ArrayList<>();
        for (ReservationSeatVO reservationSeatVO : this.seats){
            seatName.add(reservationSeatVO.getSeatNumber());
        }

        return seatName;
    }
}
