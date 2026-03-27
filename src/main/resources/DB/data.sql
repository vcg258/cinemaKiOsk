-- 초기 데이터
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
values (1, 1, '관리자', '010-1234-5678', '0', null, now());