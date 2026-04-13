package com.example.cinemakiosk.service.AdminService;

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
    void getRoles() {
        adminRoleService.getRoles().forEach(log::info);
    }

    @Test
    void getAdminRoleMaps() {
        adminRoleService.getAdminRoleMaps(2L).forEach(log::info);
    }

    @Test
    void addRole() {
    }

    @Test
    void deleteRole() {
    }
}