package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AdminRoleServiceImplTest {
    @Autowired private AdminRoleService adminRoleService;

    @Test
    void getAdmins() {
        adminRoleService.getAdmins().forEach(log::info);
    }

    @Test
    void getRoles() {
        adminRoleService.getRoles().forEach(log::info);
    }

    @Test
    void getAdminRoleMaps() {
        adminRoleService.getAdminRoleMaps(2L).forEach(log::info);
    }

    @Test
    void addRole() {
        AdminRoleMapDTO dto = AdminRoleMapDTO.builder()
                .adminId(3L)
                .roleId(2L)
                .build();
        adminRoleService.addRole(dto);
    }

    @Test
    void deleteRole() {
        AdminRoleMapDTO dto = AdminRoleMapDTO.builder()
                .adminId(3L)
                .roleId(2L)
                .build();
        adminRoleService.deleteRole(dto);
    }
}