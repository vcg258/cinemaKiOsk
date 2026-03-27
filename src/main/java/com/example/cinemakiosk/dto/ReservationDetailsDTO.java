package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.domain.ReservationSeatEntity;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.example.cinemakiosk.vo.ReservationSeatVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsDTO {
    private String id;                     // 예매 고유번호(uuid)
    private ScheduleDTO schedule;           //  스케쥴 정보
    private MemberDTO phone;                  //  회원 번호
    private LocalDateTime reservationTime; //  예약 시간
    private List<ReservationSeatDTO> seats; //  예매한 좌석들의 정보
    private List<PaymentDetailsDTO> paymentDetails; // 1:다

    /**
     * DTO -> Entity
     * @param reservationDetailsDTO
     * @return Entity
     */
    public static ReservationDetailsEntity toEntity(ReservationDetailsDTO reservationDetailsDTO) {

        List<ReservationSeatDTO> reservationSeatDTOs = reservationDetailsDTO.getSeats();
        List<ReservationSeatEntity> reservationSeatEntitys = new ArrayList<>();

        for (ReservationSeatDTO reservationSeatDTO : reservationSeatDTOs) {
            ReservationSeatEntity reservationSeatEntity = ReservationSeatEntity.builder()
                    .id(reservationSeatDTO.getId())
                    .seatNumber(reservationSeatDTO.getSeatNumber())
                    .build();

            reservationSeatEntitys.add(reservationSeatEntity);
        }

        return ReservationDetailsEntity.builder()
                .id(reservationDetailsDTO.getId())
                .scheduleEntity(ScheduleDTO.toEntity(reservationDetailsDTO.getSchedule()))
                .memberEntity(MemberDTO.toEntity(reservationDetailsDTO.getPhone()))
                .createAt(reservationDetailsDTO.getReservationTime())
                .reservationSeatEntity(reservationSeatEntitys)
                .build();
    }

    /**
     * DTO -> VO
     * @param reservationDetailsDTO
     * @return VO
     */
    public static ReservationDetailsVO toVO(ReservationDetailsDTO reservationDetailsDTO) {

        List<ReservationSeatDTO> reservationSeatDTOs = reservationDetailsDTO.getSeats();
        List<ReservationSeatVO> reservationSeatVOs = new ArrayList<>();

        for (ReservationSeatDTO reservationSeatDTO : reservationSeatDTOs) {
            ReservationSeatVO reservationSeatVO = ReservationSeatVO.builder()
                    .id(reservationSeatDTO.getId())
                    .seatNumber(reservationSeatDTO.getSeatNumber())
                    .build();
            
            reservationSeatVOs.add(reservationSeatVO);
        }

        return ReservationDetailsVO.builder()
                .id(reservationDetailsDTO.getId())
                .schedule(ScheduleDTO.toVO(reservationDetailsDTO.getSchedule()))
                .phone(MemberDTO.toVO(reservationDetailsDTO.getPhone()))
                .reservationTime(reservationDetailsDTO.getReservationTime())
                .seats(reservationSeatVOs)
                .build();
    }

    /**
     * 해당 객체가 가진 seat의 Name을 반환받기 위해서 사용함.
     * @return seats변수 속의 seatName
     */
    public List<String> getSeatName(){
        List<String> seatName = new ArrayList<>();
        for (ReservationSeatDTO reservationSeatDTO : this.seats){
            seatName.add(reservationSeatDTO.getSeatNumber());
        }

        return seatName;
    }

}
