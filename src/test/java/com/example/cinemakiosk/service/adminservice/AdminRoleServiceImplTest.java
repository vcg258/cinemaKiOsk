package com.example.cinemakiosk.service.adminservice;

import com.example.cinemakiosk.dto.requestDTO.AdminRoleMapRequest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Log4j2
@SpringBootTest
class AdminRoleServiceImplTest {
    @Autowired private AdminRoleService adminRoleService;

    @Test
    void getAdmins() {
        adminRoleService.getAdmins().forEach(log::info);
    }

    @Test
    void getAdmin() {
        log.info(adminRoleService.getAdmin("admin"));
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
        AdminRoleMapRequest dto = new AdminRoleMapRequest();
        dto.setAdminId(2L);
        dto.setRoles(List.of(1L, 2L, 30L));
        adminRoleService.addRole(dto);
    }

    @Test
    void rememberUUID() {
        adminRoleService.rememberMe("admin");
    }
}