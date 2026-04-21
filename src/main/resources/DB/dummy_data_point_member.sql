-- 더미 데이터 생성 SQL
-- 목적: 배치 테스트용 (2년간 포인트 내역 없는 회원 삭제)
-- 회원: 100명 / 포인트 내역: 약 10,000건
-- 배치 삭제 대상: 25명 - 마지막 이력이 2년 이전



-- DELIMITER $$ 이거때문에 오류나면 그냥 한번더 실행하면 됨

-- 기존 데이터 초기화 (테스트 재실행 시)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE point_history;
TRUNCATE TABLE member;
TRUNCATE TABLE member_cleanup_log;



insert ignore into member (phone, create_at, point)
    VALUE ('01099999999', current_timestamp, 22222);

insert into point_history (point_id, amount_point, create_at, type, phone, payment_id)
values (1, 200, 2021-03-15, 'EARN', '01012345678', '11111111-1111-1111-1111-111111111111'),
       (2, 200, current_timestamp, 'EARN', '01012345678', '22222222-2222-2222-2222-222222222222'),
       (3, 200, current_timestamp, 'EARN', '01012345678', '33333333-3333-3333-3333-333333333333'),
       (4, 200, current_timestamp, 'EARN', '01099999999', '44444444-4444-4444-4444-444444444444'),
       (5, 200, current_timestamp, 'EARN', '01099999999', '55555555-5555-5555-5555-555555555555'),
       (6, 200, current_timestamp, 'EARN', '01099999999', '66666666-6666-6666-6666-666666666666');
#
# INSERT INTO `member` (`phone`, `point`, `create_at`) VALUES
# -- [활성 회원 75명] 생성일: 5년전 ~ 최근
# ('01011111001', 1200,  '2021-03-15 09:00:00'),
# ('01011111002', 3400,  '2020-07-22 10:30:00'),
# ('01011111003', 500,   '2022-11-05 08:15:00'),
# ('01011111004', 7800,  '2019-06-18 14:00:00'),
# ('01011111005', 200,   '2023-01-10 11:45:00'),
# ('01011111006', 9100,  '2021-09-30 16:20:00'),
# ('01011111007', 4600,  '2020-04-14 09:50:00'),
# ('01011111008', 1300,  '2022-08-27 13:10:00'),
# ('01011111009', 8700,  '2019-12-03 07:30:00'),
# ('01011111010', 2500,  '2023-05-19 15:40:00'),
# ('01022221001', 3300,  '2021-02-08 10:00:00'),
# ('01022221002', 6600,  '2020-10-25 11:20:00'),
# ('01022221003', 1100,  '2022-06-12 14:35:00'),
# ('01022221004', 4400,  '2019-08-07 08:00:00'),
# ('01022221005', 9900,  '2023-03-28 17:15:00'),
# ('01022221006', 2200,  '2021-07-19 12:45:00'),
# ('01022221007', 5500,  '2020-01-31 09:30:00'),
# ('01022221008', 7700,  '2022-04-03 16:00:00'),
# ('01022221009', 3800,  '2019-11-22 10:15:00'),
# ('01022221010', 1600,  '2024-01-07 13:50:00'),
# ('01033331001', 4200,  '2021-05-14 08:30:00'),
# ('01033331002', 8800,  '2020-09-08 15:20:00'),
# ('01033331003', 600,   '2022-12-20 11:00:00'),
# ('01033331004', 5100,  '2019-04-16 09:45:00'),
# ('01033331005', 2900,  '2023-07-04 14:30:00'),
# ('01033331006', 7300,  '2021-11-28 10:10:00'),
# ('01033331007', 1800,  '2020-03-17 16:55:00'),
# ('01033331008', 6200,  '2022-09-09 08:20:00'),
# ('01033331009', 4900,  '2019-07-25 12:40:00'),
# ('01033331010', 3100,  '2024-02-14 09:05:00'),
# ('01044441001', 7600,  '2021-01-20 11:30:00'),
# ('01044441002', 2100,  '2020-06-11 14:00:00'),
# ('01044441003', 9300,  '2022-03-07 07:45:00'),
# ('01044441004', 1400,  '2019-10-30 13:20:00'),
# ('01044441005', 5800,  '2023-09-16 10:35:00'),
# ('01044441006', 3700,  '2021-04-02 08:50:00'),
# ('01044441007', 8400,  '2020-12-23 15:15:00'),
# ('01044441008', 2800,  '2022-07-18 11:40:00'),
# ('01044441009', 6100,  '2019-03-05 09:00:00'),
# ('01044441010', 4300,  '2024-04-01 16:25:00'),
# ('01055551001', 900,   '2021-08-26 12:00:00'),
# ('01055551002', 7100,  '2020-02-19 10:45:00'),
# ('01055551003', 3600,  '2022-10-14 14:10:00'),
# ('01055551004', 8200,  '2019-05-28 08:30:00'),
# ('01055551005', 1700,  '2023-11-02 11:55:00'),
# ('01055551006', 5300,  '2021-12-10 15:30:00'),
# ('01055551007', 2400,  '2020-08-05 09:15:00'),
# ('01055551008', 6900,  '2022-02-28 13:45:00'),
# ('01055551009', 4100,  '2019-09-13 07:00:00'),
# ('01055551010', 8600,  '2024-03-08 10:20:00'),
# ('01066661001', 1900,  '2021-06-07 11:10:00'),
# ('01066661002', 5600,  '2020-11-14 14:50:00'),
# ('01066661003', 3200,  '2022-05-29 08:05:00'),
# ('01066661004', 7500,  '2019-02-14 12:30:00'),
# ('01066661005', 2700,  '2023-08-21 16:45:00'),
# ('01066661006', 9400,  '2021-10-18 09:20:00'),
# ('01066661007', 1500,  '2020-05-07 13:00:00'),
# ('01066661008', 6400,  '2022-01-16 10:35:00'),
# ('01066661009', 4700,  '2019-06-30 15:50:00'),
# ('01066661010', 3000,  '2024-02-28 08:45:00'),
# ('01077771001', 8100,  '2021-03-04 11:20:00'),
# ('01077771002', 2600,  '2020-07-17 14:40:00'),
# ('01077771003', 7200,  '2022-11-08 09:55:00'),
# ('01077771004', 1000,  '2019-08-22 07:15:00'),
# ('01077771005', 5900,  '2023-04-11 12:50:00'),
# ('01077771006', 4000,  '2021-09-25 16:05:00'),
# ('01077771007', 9600,  '2020-01-13 10:25:00'),
# ('01077771008', 3500,  '2022-06-24 13:55:00'),
# ('01077771009', 6800,  '2019-12-09 08:10:00'),
# ('01077771010', 2300,  '2024-01-19 15:30:00'),
# ('01088881001', 7900,  '2021-05-31 11:45:00'),
# ('01088881002', 1100,  '2020-09-20 09:10:00'),
# ('01088881003', 5400,  '2022-04-17 14:20:00'),
# ('01088881004', 3900,  '2019-11-04 07:35:00'),
# ('01088881005', 3900,  '2019-11-04 07:35:00'),
#
# -- [삭제 대상 회원 25명] 생성일: 5년전 ~ 2년전 (오래된 회원들)
# -- 이 회원들의 마지막 포인트 내역은 2년 이전으로 설정됨
#
#
# ('01099990001', 100,     '2020-01-05 10:00:00'),
# ('01099990002', 200,     '2019-03-18 11:30:00'),
# ('01099990003', 200,     '2020-07-22 09:15:00'),
# ('01099990004', 2003,     '2019-11-09 14:00:00'),
# ('01099990005', 2026,     '2020-04-14 08:45:00'),
# ('01099990006', 2026,     '2019-06-27 13:20:00'),
# ('01099990007', 2026,     '2020-09-03 10:50:00'),
# ('01099990008', 2026,     '2019-02-11 16:00:00'),
# ('01099990009', 2026,     '2020-12-19 07:30:00'),
# ('01099990010', 2026,     '2019-08-05 12:15:00'),
# ('01099990011', 2026,     '2020-03-27 11:00:00'),
# ('01099990012', 2026,     '2019-05-16 15:40:00'),
# ('01099990013', 2026,     '2020-10-08 09:25:00'),
# ('01099990014', 2026,     '2019-09-30 14:55:00'),
# ('01099990015', 2026,     '2020-06-15 10:10:00'),
# ('01099990016', 2026,     '2019-01-23 13:45:00'),
# ('01099990017', 2026,     '2020-11-11 08:20:00'),
# ('01099990018', 2026,     '2019-04-08 16:30:00'),
# ('01099990019', 2026,     '2020-08-24 11:05:00'),
# ('01099990020', 2026,     '2019-07-19 09:50:00'),
# ('01099990021', 2026,     '2020-02-14 14:15:00'),
# ('01099990022', 2026,     '2019-10-31 10:35:00'),
# ('01099990023', 2026,     '2020-05-06 07:00:00'),
# ('01099990024', 2026,     '2019-12-25 12:50:00'),
# ('01099990025', 2026,     '2020-01-30 15:20:00');
#

-- ============================================================
-- 2. payment_details 임시 데이터 (FK 충족용)
--    실제 환경에서는 기존 테이블 사용
-- ============================================================
-- 주의: payment_details 테이블이 없을 경우 아래 CREATE 사용
-- CREATE TABLE IF NOT EXISTS payment_details (
--     id CHAR(36) PRIMARY KEY
-- );

-- ============================================================
-- 3. 포인트 내역 생성 (총 약 10,000건)
-- ============================================================

-- -------------------------------------------------------
-- [파트 A] 활성 회원 75명 포인트 내역 (약 7,500건)
--   마지막 이력이 2년 이내 → 배치 삭제 안됨
-- -------------------------------------------------------

-- 프로시저를 활용한 대량 삽입
DROP PROCEDURE IF EXISTS insert_active_point_history;

DELIMITER $$

CREATE PROCEDURE insert_active_point_history()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE v_phone VARCHAR(20);
    DECLARE v_payment_id CHAR(36);
    DECLARE v_type ENUM('EARN','USE','REFUND_EARN','REFUND_USE');
    DECLARE v_amount INT;
    DECLARE v_create_at DATETIME;
    DECLARE v_type_num INT;
    DECLARE v_days_ago INT;

    -- 활성 회원 폰번호 배열 (75명)
    DECLARE phones_cursor CURSOR FOR
SELECT phone FROM member WHERE phone NOT LIKE '01099990%';

DECLARE CONTINUE HANDLER FOR NOT FOUND SET i = 9999;

OPEN phones_cursor;

phone_loop: LOOP
        FETCH phones_cursor INTO v_phone;
        IF i >= 9999 THEN
            LEAVE phone_loop;
END IF;

        -- 각 회원당 약 100건의 포인트 내역 생성
        SET @j = 0;
        WHILE @j < 100 DO
            SET v_payment_id = UUID();
            SET v_type_num   = FLOOR(RAND() * 4);
            SET v_type       = CASE v_type_num
                                   WHEN 0 THEN 'EARN'
                                   WHEN 1 THEN 'USE'
                                   WHEN 2 THEN 'REFUND_EARN'
                                   ELSE        'REFUND_USE'
END;
            SET v_amount     = FLOOR(RAND() * 9901) + 100; -- 100 ~ 10000

            -- 최근 2년 이내 날짜 (0 ~ 700일 전)
            SET v_days_ago   = FLOOR(RAND() * 700);
            SET v_create_at  = DATE_SUB(NOW(), INTERVAL v_days_ago DAY);

            -- payment_details FK 임시 삽입 (없으면 아래 주석 해제)
            -- INSERT IGNORE INTO payment_details (id) VALUES (v_payment_id);

INSERT INTO point_history (payment_id, phone, type, amount_point, create_at)
VALUES (v_payment_id, v_phone, v_type, v_amount, v_create_at);

SET @j = @j + 1;
END WHILE;

END LOOP;

CLOSE phones_cursor;
END$$

DELIMITER ;

CALL insert_active_point_history();
DROP PROCEDURE IF EXISTS insert_active_point_history;


-- -------------------------------------------------------
-- [파트 B] 삭제 대상 회원 25명 포인트 내역 (약 2,500건)
--   마지막 이력이 2년 이전 → 배치 실행 시 삭제됨
--   날짜 범위: 2년 이전 ~ 5년 이전 (730일 ~ 1825일 전)
-- -------------------------------------------------------

DROP PROCEDURE IF EXISTS insert_inactive_point_history;

DELIMITER $$

CREATE PROCEDURE insert_inactive_point_history()
BEGIN
    DECLARE v_phone VARCHAR(20);
    DECLARE v_payment_id CHAR(36);
    DECLARE v_type ENUM('EARN','USE','REFUND_EARN','REFUND_USE');
    DECLARE v_amount INT;
    DECLARE v_create_at DATETIME;
    DECLARE v_type_num INT;
    DECLARE v_days_ago INT;
    DECLARE done INT DEFAULT 0;

    DECLARE phones_cursor CURSOR FOR
SELECT phone FROM member WHERE phone LIKE '01099990%';

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

OPEN phones_cursor;

phone_loop: LOOP
        FETCH phones_cursor INTO v_phone;
        IF done = 1 THEN
            LEAVE phone_loop;
END IF;

        SET @j = 0;
        WHILE @j < 100 DO
            SET v_payment_id = UUID();
            SET v_type_num   = FLOOR(RAND() * 4);
            SET v_type       = CASE v_type_num
                                   WHEN 0 THEN 'EARN'
                                   WHEN 1 THEN 'USE'
                                   WHEN 2 THEN 'REFUND_EARN'
                                   ELSE        'REFUND_USE'
END;
            SET v_amount     = FLOOR(RAND() * 9901) + 100;

            -- 2년 이전 날짜 (730일 ~ 1825일 전)
            SET v_days_ago   = FLOOR(RAND() * 1095) + 730;
            SET v_create_at  = DATE_SUB(NOW(), INTERVAL v_days_ago DAY);

            -- INSERT IGNORE INTO payment_details (id) VALUES (v_payment_id);

INSERT INTO point_history (payment_id, phone, type, amount_point, create_at)
VALUES (v_payment_id, v_phone, v_type, v_amount, v_create_at);

SET @j = @j + 1;
END WHILE;

END LOOP;

CLOSE phones_cursor;
END$$

DELIMITER ;

CALL insert_inactive_point_history();
DROP PROCEDURE IF EXISTS insert_inactive_point_history;


-- ============================================================
-- 4. 검증 쿼리
-- ============================================================

-- 전체 회원 수 확인 (100명)
SELECT COUNT(*) AS total_members FROM member;

-- 전체 포인트 내역 수 확인 (약 10,000건)
SELECT COUNT(*) AS total_point_history FROM point_history;

-- 삭제 대상 회원 확인 (25명)
-- 배치 조건: 2년간 포인트 내역이 없는 회원
SELECT
    m.phone,
    m.create_at AS member_create_at,
    MAX(ph.create_at) AS last_point_at,
    DATEDIFF(NOW(), MAX(ph.create_at)) AS days_since_last_point
FROM member m
         LEFT JOIN point_history ph ON m.phone = ph.phone
GROUP BY m.phone, m.create_at
HAVING MAX(ph.create_at) IS NULL
    OR MAX(ph.create_at) < DATE_SUB(NOW(), INTERVAL 2 YEAR)
ORDER BY last_point_at;

-- 삭제 대상 수 카운트 (25명이어야 함)
SELECT COUNT(*) AS delete_target_count
FROM (
         SELECT m.phone
         FROM member m
                  LEFT JOIN point_history ph ON m.phone = ph.phone
         GROUP BY m.phone
         HAVING MAX(ph.create_at) IS NULL
             OR MAX(ph.create_at) < DATE_SUB(NOW(), INTERVAL 2 YEAR)
     ) t;

-- ============================================================
-- 5. 배치 실행 쿼리 (참고용)
-- ============================================================
-- DELETE FROM member
-- WHERE phone IN (
--     SELECT phone FROM (
--         SELECT m.phone
--         FROM member m
--         LEFT JOIN point_history ph ON m.phone = ph.phone
--         GROUP BY m.phone
--         HAVING MAX(ph.create_at) IS NULL
--             OR MAX(ph.create_at) < DATE_SUB(NOW(), INTERVAL 2 YEAR)
--     ) AS target
-- );


SET FOREIGN_KEY_CHECKS = 1;
