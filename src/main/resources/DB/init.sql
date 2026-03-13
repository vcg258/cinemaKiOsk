-- 스키마 생성
CREATE DATABASE IF NOT EXISTS `cinema_kiosk` CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

-- 사용자 생성, 권한 부여
CREATE USER IF NOT EXISTS 'task_master'@'%' IDENTIFIED BY '4444';
GRANT ALL PRIVILEGES ON `cinema_kiosk`.* TO 'task_master'@'%';

-- 권한 다시 로드, 즉시 적용하기 위해 넣음
FLUSH PRIVILEGES;

USE `cinema_kiosk`;

CREATE TABLE IF NOT EXISTS `admin` # 외래키 없음
(
    `admin_id`    BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '관리자 인덱스',
    `login_id`    VARCHAR(30) NOT NULL UNIQUE COMMENT '관리자 아이디',
    `password`    VARCHAR(60) NOT NULL COMMENT '관리자 비밀번호',
    `name`        VARCHAR(50) NOT NULL COMMENT '관리자 이름',
    `phone_admin` VARCHAR(20) NOT NULL COMMENT '전화번호',
    `level`       boolean     NOT NULL COMMENT '관리자 권한 레벨 : 마스터 : 0, 알바 : 1',
    `UUID`        CHAR(36)    NULL COMMENT '자동 로그인 토큰',
    `create_at`   DATETIME    NOT NULL COMMENT '계정 생성 일자'
) COMMENT '관리자';

insert into admin (login_id, password, name, phone_admin, level, UUID, create_at)
values (1, 1, '관리자', '010-1234-5678', '0', null, now());

CREATE TABLE IF NOT EXISTS `members` # 외래키 없음
(
    `phone`      VARCHAR(20) PRIMARY KEY COMMENT '회원 번호',
    `point`      BIGINT UNSIGNED NULL DEFAULT 0 COMMENT '포인트',
    `created_at` DATETIME        NULL COMMENT '생성일'
) COMMENT '회원(포인트)';


CREATE TABLE IF NOT EXISTS `movie` # 외래키 없음
(
    `movie_id`    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '영화 인덱스',
    `title`       VARCHAR(100)                   NOT NULL COMMENT '영화 제목',
    `genre`       VARCHAR(50)                    NOT NULL COMMENT '장르',
    `rating`      ENUM ('ALL', '12', '15', '19') NOT NULL COMMENT '관람 등급',
    `runtime`     BIGINT UNSIGNED                NOT NULL COMMENT '상영 시간(분?, 초?)',
    `director`    VARCHAR(50)                    NOT NULL COMMENT '감독이름',
    `actors`      VARCHAR(255)                   NULL COMMENT '배우(쉼표로 구분(?))',
    `description` TEXT                           NULL COMMENT '줄거리',
    `start_at`    DATE                           NOT NULL COMMENT '상영 시작일',
    `end_at`      DATE                           NULL COMMENT '상영 종료일',
    `create_at`   DATE                           NULL COMMENT '영화 등록일'
) COMMENT '영화';

CREATE TABLE IF NOT EXISTS `seat_policy` #외래키 없음
(
    `policy_id` CHAR(36) PRIMARY KEY COMMENT '좌석 아이디',
    `name`      VARCHAR(20)     NULL COMMENT '좌석 이름',
    `cost`      BIGINT UNSIGNED NULL DEFAULT 0 COMMENT '좌석 비용'
) COMMENT '좌석 정책';

CREATE TABLE IF NOT EXISTS `bonus_policy` # 외래키 없음
(
    `id`          BIGINT UNSIGNED PRIMARY KEY COMMENT '적립 정책 인덱스',
    `policy_name` VARCHAR(20)     NULL COMMENT '정책 이름',
    `give_value`  BIGINT UNSIGNED NULL COMMENT '적립 비율',
    `create_at`   DATETIME        NULL COMMENT '시작일',
    `finished_at` DATETIME        NULL COMMENT '만료일',
    `activation`  BOOLEAN         NULL COMMENT '활성화 여부'
) COMMENT '적립 행사 정책(자체 이벤트)';

CREATE TABLE IF NOT EXISTS `discount_policy` # 외래키 없음
(
    `id`             BIGINT UNSIGNED PRIMARY KEY COMMENT '할인 정책 인덱스',
    `policy_name`    VARCHAR(20)                                 NULL COMMENT '정책 이름',
    `discount_type`  ENUM ('RATIO', 'WON')                       NULL COMMENT '할인 방식',
    `discount_value` BIGINT UNSIGNED                             NULL COMMENT '할인 값',
    `condition_type` ENUM ('TIME', 'AGE', 'GOVERNMENT_EMPLOYEE') NULL COMMENT '할인 유형',
    `create_at`      DATETIME                                    NULL COMMENT '시작일',
    `finished_at`    DATETIME                                    NULL COMMENT '만료일',
    `activation`     BOOLEAN                                     NULL COMMENT '활성화 여부'
) COMMENT '할인 행사 정책(자체 이벤트)';
-- --------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `theater`
(
    `no`           BIGINT UNSIGNED PRIMARY KEY COMMENT '상영관 번호',
    `policy_id`    char(36)        NOT NULL COMMENT '좌석 정책(외래)',
    `cleanup_time` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT '정리시간',
    CONSTRAINT `fk_theater_policy_id` FOREIGN KEY (`policy_id`) REFERENCES seat_policy (`policy_id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '상영관';

CREATE TABLE IF NOT EXISTS `schedule`
(
    `id`         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '스케줄 인덱스',
    `no`         BIGINT UNSIGNED NOT NULL COMMENT '상영관 번호(외래)',
    `movie_id`   BIGINT UNSIGNED NOT NULL COMMENT '영화 번호(외래)',
    `start_time` DATETIME        NULL COMMENT '상영 시작 시간',
    `end_time`   DATETIME        NULL COMMENT '상영 종료 시간',
    CONSTRAINT `fk_schedule_theater_no` FOREIGN KEY (`no`) REFERENCES theater (`no`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_schedule_movie_id` FOREIGN KEY (`movie_id`) REFERENCES movie (`movie_id`)
        ON DELETE CASCADE ON UPDATE CASCADE

) COMMENT '상영 스케줄';


CREATE TABLE IF NOT EXISTS statistics
(
    `id`             BIGINT UNSIGNED PRIMARY KEY COMMENT '통계 고유 번호',
    `schedule_id`    BIGINT UNSIGNED                                       NOT NULL COMMENT '스케쥴 아이디(외래)',
    `day`            ENUM ('SUN','MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT') NOT NULL COMMENT '요일',
    `revenue`        BIGINT UNSIGNED                                       NOT NULL COMMENT '수익',
    `customer_count` BIGINT UNSIGNED                                       NOT NULL COMMENT '관람객 수',
    CONSTRAINT `fk_statistics_schedule_id` FOREIGN KEY (`schedule_id`) REFERENCES schedule (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '통계';

CREATE TABLE IF NOT EXISTS `reservation_details`
(
    `id`               varchar(36) PRIMARY KEY COMMENT '예매 고유번호, uuid',
    `schedule_id`      BIGINT UNSIGNED NOT NULL COMMENT '스케쥴 아이디(외래)',
    `phone`            VARCHAR(20) NOT NULL COMMENT '회원 번호(외래)',
    `reservation_time` DATETIME    NULL COMMENT '예매 기준시',
    CONSTRAINT `fk_reservation_details_schedule_id` FOREIGN KEY (`schedule_id`) REFERENCES schedule (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_reservation_details_members_phone` FOREIGN KEY (`phone`) REFERENCES members (`phone`)
        ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '예매 내역';


CREATE TABLE IF NOT EXISTS `reservation_seat`
(
    `id`             VARCHAR(255) PRIMARY KEY COMMENT '고유번호',
    `reservation_id` varchar(36) NOT NULL COMMENT '예매 내역(외래)',
    `seat_number`    VARCHAR(10) NULL COMMENT '좌석 번호',
    CONSTRAINT `fk_reservation_seat_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES reservation_details (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE

) COMMENT '예매 좌석';



CREATE TABLE IF NOT EXISTS `payment_details`
(
    `id`                 CHAR(36) PRIMARY KEY COMMENT '결제 고유번호',
    `reservation_id`     varchar(36)                  NOT NULL COMMENT '예매 내역(외래)',
    `bonus_policy_id`    BIGINT UNSIGNED              NOT NULL COMMENT '적립 정책(외래)',
    `discount_policy_id` BIGINT UNSIGNED              NULL COMMENT '할인 정책(외래), 할인 없는 경우 있음. 혹은 할인 0원도 정책으로?',
    `cost`               BIGINT UNSIGNED              NOT NULL COMMENT '결제 금액',
    `time`               DATETIME                     NOT NULL COMMENT '결제 시간',
    `use_point`          BIGINT UNSIGNED              NULL DEFAULT 0 COMMENT '사용 포인트',
    `status`             ENUM ('PAY','RETURN','FAIL') NULL COMMENT '결제 내용',
    CONSTRAINT `fk_payment_details_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES reservation_details (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_payment_details_bonus_policy_id` FOREIGN KEY (`bonus_policy_id`) REFERENCES bonus_policy (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_payment_details_discount_policy_id` FOREIGN KEY (`discount_policy_id`) REFERENCES discount_policy (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '결제 내역';


CREATE TABLE IF NOT EXISTS `point_history`
(
    `point_id`     BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '포인트 인덱스',
    `payment_id`   char(36)             NOT NULL COMMENT '결제 고유번호(외래)',
    `phone`        VARCHAR(20)          NOT NULL COMMENT '회원 번호(외래)',
    `type`         ENUM ('EARN', 'USE') NOT NULL COMMENT '적립 / 사용',
    `amount_point` BIGINT UNSIGNED      NOT NULL COMMENT '사용할 포인트(CHECK 제약 조건 음수 X)',
    `create_at`    DATETIME             NOT NULL COMMENT '포인트 변경일',
    CONSTRAINT `fk_point_history_payment_id` FOREIGN KEY (`payment_id`) REFERENCES payment_details (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_point_history_phone` FOREIGN KEY (`phone`) REFERENCES members (`phone`)
        ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT '포인트 내역';