package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountPolicyEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountType;
import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPolicyDTO {
    private Long id; // 할인 정책 인덱스
    private String policyName; // 정책이름
    private DiscountType discountType; // 할인 방식
    private Long discountValue; // 할인 값
    private ConditionType conditionType; // 할인 유형
    private LocalDateTime startAt; // 시작일
    private LocalDateTime endAt; // 만료일
    private boolean activation; // 활성화 여부
    private List<CouponDTO> coupons; // resultMap(collection)


    /**
     * DTO -> Entity
     * @param discountPolicyDTO DTO
     * @return Entity
     */
    public static DiscountPolicyEntity toEntity(DiscountPolicyDTO discountPolicyDTO) {
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<CouponDTO> couponDTOs = discountPolicyDTO.getCoupons();
        List<CouponEntity> couponEntities = new ArrayList<>();


        for (CouponDTO coupon : couponDTOs){
            //pk 만 받아오기.
            CouponEntity couponEntity = CouponEntity.builder()
                    .couponNum(coupon.getCouponNum())
                    .build();

            couponEntities.add(couponEntity);
        }


        return DiscountPolicyEntity.builder()
                .id(discountPolicyDTO.getId())
                .policyName(discountPolicyDTO.getPolicyName())
                .discountType(discountPolicyDTO.getDiscountType())
                .discountValue(discountPolicyDTO.getDiscountValue())
                .conditionType(discountPolicyDTO.getConditionType())
                .startAt(discountPolicyDTO.getStartAt())
                .endAt(discountPolicyDTO.getEndAt())
                .activation(discountPolicyDTO.isActivation())
                .coupons(couponEntities)
                .build();
    }

    /**
     * DTO -> VO
     * @param discountPolicyDTO DTO
     * @return VO
     */
    public static DiscountPolicyVO toVO(DiscountPolicyDTO discountPolicyDTO) {
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<CouponDTO> couponDTOs = discountPolicyDTO.getCoupons();
        List<CouponVO> couponVOs = new ArrayList<>();


        for (CouponDTO coupon : couponDTOs){
            //pk 만 받아오기.
            CouponVO couponVO = CouponVO.builder()
                    .couponNum(coupon.getCouponNum())
                    .build();

            couponVOs.add(couponVO);
        }


        return DiscountPolicyVO.builder()
                .id(discountPolicyDTO.getId())
                .policyName(discountPolicyDTO.getPolicyName())
                .discountType(discountPolicyDTO.getDiscountType())
                .discountValue(discountPolicyDTO.getDiscountValue())
                .conditionType(discountPolicyDTO.getConditionType())
                .startAt(discountPolicyDTO.getStartAt())
                .endAt(discountPolicyDTO.getEndAt())
                .activation(discountPolicyDTO.isActivation())
                .coupons(couponVOs)
                .build();
    }
}
