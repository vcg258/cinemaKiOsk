package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.AdminEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface AdminMapper {

    // 전체 관리자 조회
    @Select("SELECT * FROM admin ORDER BY create_at DESC")
    List<AdminEntity> findAll();

    // ID로 조회
    @Select("SELECT * FROM admin WHERE admin_id = #{adminId}")
    AdminEntity findById(@Param("adminId") Long adminId);

    // 로그인 아이디로 조회 (로그인 처리용)
    @Select("SELECT * FROM admin WHERE login_id = #{loginId}")
    AdminEntity findByLoginId(@Param("loginId") String loginId);

    // uuid로 조회 (자동 로그인 처리용)
    @Select("SELECT * FROM admin WHERE uuid = #{uuid}")
    AdminEntity findByUuid(@Param("uuid") String uuid);

    // 권한 레벨로 조회 (false: 마스터, true: 알바)
    @Select("SELECT * FROM admin WHERE level = #{level}")
    List<AdminEntity> findByLevel(@Param("level") boolean level);

    // 관리자 등록
    @Insert("""
            INSERT INTO admin (login_id, password, name, phone_admin, level, uuid, create_at)
            VALUES (#{loginId}, #{password}, #{name}, #{phoneAdmin}, #{level}, #{uuid}, NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "adminId")
    int insert(AdminEntity admin);

    // 관리자 정보 수정
    @Update("""
            UPDATE admin
            SET name = #{name},
                phone_admin = #{phoneAdmin},
                level = #{level}
            WHERE admin_id = #{adminId}
            """)
    int update(AdminEntity admin);

    // 비밀번호 변경
    @Update("UPDATE admin SET password = #{password} WHERE admin_id = #{adminId}")
    int updatePassword(@Param("adminId") Long adminId, @Param("password") String password);

    // uuid 업데이트 (자동 로그인 토큰 갱신)
    @Update("UPDATE admin SET uuid = #{uuid} WHERE admin_id = #{adminId}")
    int updateUuid(@Param("adminId") Long adminId, @Param("uuid") String uuid);

    // uuid 삭제 (로그아웃)
    @Update("UPDATE admin SET uuid = NULL WHERE admin_id = #{adminId}")
    int clearUuid(@Param("adminId") Long adminId);

    // 관리자 삭제
    @Delete("DELETE FROM admin WHERE admin_id = #{adminId}")
    int delete(@Param("adminId") Long adminId);
}