package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.domain.adminDomain.AdminEntity;
import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import com.example.cinemakiosk.repository.AdminRepository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    /**
     * 관리자 아이디와 비밀번호를 검증 하는 메서드
     * @param loginId 관리자아이디
     * @return 존재한다면 관리자 아이디와 비밀번호 반환
     * @throws UsernameNotFoundException 없으면 예외 던지기
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        AdminEntity admin = adminRepository.findByLoginId(loginId).orElseThrow(
                () -> new UsernameNotFoundException("관리자 아이디가 없습니다")
        );

        log.info("로그인 시도 관리자 아이디 : {}", admin);

        // 총 관리자일 경우 모든 권한을 부여함
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (!admin.isLevel()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MASTER"));
        } else {
            List<AdminRoleMapEntity> adminRoleMapEntityList = admin.getAdminRoleMapEntity();
            for (AdminRoleMapEntity adminRoleMapEntity : adminRoleMapEntityList) {
                authorities.add(new SimpleGrantedAuthority(adminRoleMapEntity.getAdminRoleEntity().getRoleName()));
            }
        }

        return new AdminDetails(
                admin.getLoginId(),
                admin.getPassword(),
                admin.isLevel(),
                authorities
        );
    }
}
