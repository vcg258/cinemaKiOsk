package com.example.cinemakiosk.domain.admindomain;

import com.example.cinemakiosk.dto.adminDTO.AdminDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@ToString (exclude = "adminRoleMapEntity")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin")
public class AdminEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id", columnDefinition = "BIGINT UNSIGNED")
    private Long adminId;         // 관리자 인덱스 (PK)

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;       // 관리자 아이디

    @Column(name = "password", nullable = false)
    private String password;      // 관리자 비밀번호

    @Column(name = "name", nullable = false)
    private String name;          // 관리자 이름

    @Column(name = "admin_phone", nullable = false, unique = true)
    private String adminPhone;    // 전화번호

    @Column(name = "level", nullable = false)
    private boolean level;        // 권한 레벨 (false: 마스터 0, true: 알바 1)

    @Column(name = "uuid")
    private String uuid;          // 자동 로그인 토큰

    @CreatedDate
    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime createAt; // 계정 생성 일자

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "adminEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<AdminRoleMapEntity> adminRoleMapEntity; // 관리자 아이디 FK


    public void changeUUID() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void changeUUIDNull() {
        this.uuid = null;
    }

    /**
     * Entity -> DTO
     * @param adminEntity
     * @return DTO
     */
    public static AdminDTO toDTO(AdminEntity adminEntity) {
        return AdminDTO.builder()
                .adminId(adminEntity.getAdminId())
                .loginId(adminEntity.getLoginId())
                .password(adminEntity.getPassword())  // 암호화된 비밀번호 저장
                .name(adminEntity.getName())
                .adminPhone(adminEntity.getAdminPhone())
                .level(adminEntity.isLevel())
                .uuid(adminEntity.getUuid())
                .createAt(adminEntity.getCreateAt())
                .build();
    }
}