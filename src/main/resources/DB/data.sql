-- 초기 데이터
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
values (1, 1, '관리자', '010-1234-5678', '0', null, now());

-- JPA가 생성한 movie.rating ENUM을 DB 기준값으로 변경
-- JPA는 ENUM 이름(FIFTEEN 등)으로 생성하므로 실제 서비스 값(15 등)으로 ALTER
ALTER TABLE movie MODIFY COLUMN rating ENUM('ALL', '12', '15', '19') NOT NULL;