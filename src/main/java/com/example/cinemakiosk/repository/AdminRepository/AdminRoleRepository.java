package com.example.cinemakiosk.repository.AdminRepository;

import com.example.cinemakiosk.domain.adminDomain.AdminRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRoleRepository extends JpaRepository<AdminRoleEntity, Long> {
}
