package com.example.cinemakiosk.repository.admin;

import com.example.cinemakiosk.domain.admindomain.AdminRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRoleRepository extends JpaRepository<AdminRoleEntity, Long> {
}
