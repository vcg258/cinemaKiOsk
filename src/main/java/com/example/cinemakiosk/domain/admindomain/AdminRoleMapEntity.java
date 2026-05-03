package com.example.cinemakiosk.domain.admindomain;

import com.example.cinemakiosk.dto.adminDTO.AdminRoleMapDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@ToString (exclude = {"adminEntity", "adminRoleEntity"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_role_map", uniqueConstraints = @UniqueConstraint(columnNames = {"admin_id", "role_id"})) // 복합 UNIQUE
public class AdminRoleMapEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private Long id; // 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false, foreignKey = @ForeignKey(name = "fk_admin_role_map_admin"))
    private AdminEntity adminEntity; // 관리자 아이디 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_admin_role_map_admin_role"))
    private AdminRoleEntity adminRoleEntity; // 권한 아이디 FK


    /**
     * Entity -> DTO
     * @param adminRoleMapEntity Entity
     * @return DTO
     */
    public static AdminRoleMapDTO toDTO(AdminRoleMapEntity adminRoleMapEntity) {

        return AdminRoleMapDTO.builder()
                .id(adminRoleMapEntity.getId())
                .adminId(adminRoleMapEntity.getAdminEntity().getAdminId())
                .roleId(adminRoleMapEntity.getAdminRoleEntity().getId())
                .build();
    }
}

