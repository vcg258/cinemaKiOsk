package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_role")
public class AdminRoleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long id; // 인덱스

    @Column(length = 40, nullable = false, unique = true)
    private String roleName; // ROLE_REFUND, ROLE_MOVIE_REG 등 권한 이름 (Security)

    @Column(length = 30, unique = true)
    private String roleDesc; // 권한 이름 (뷰에서 보여줄 이름)

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "adminRoleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private AdminRoleMapEntity adminRoleMapEntity; // 권한 아이디 FK
}
