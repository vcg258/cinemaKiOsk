-- 초기 데이터
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
values (1, 1, '관리자', '01012345678', '0', null, now());

-- JPA가 생성한 movie.rating ENUM을 DB 기준값으로 변경
-- JPA는 ENUM 이름(FIFTEEN 등)으로 생성하므로 실제 서비스 값(15 등)으로 ALTER
ALTER TABLE movie MODIFY COLUMN rating ENUM('ALL', '12', '15', '19') NOT NULL;


-- 기초 데이터 입력 테스트
insert ignore into seat_policy (policy_id, cost, name)
values (1, 5000, '일반'),
       (2, 10000, '리클라이너'),
       (3, 15000, '커플석'),
       (4, 7000, 'VIP');

insert ignore into theater (no, cleanup_time, policy_id)
VALUES (1, 10, 1),
       (2, 20, 2),
       (3, 30, 3),
       (4, 40, 4);

insert ignore into movie (movie_id, actors, create_at, description, director, end_at, genre, rating, runtime, start_at,
                          title)
VALUES (1, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 1 day), '액션', 'ALL', 200,
        date_sub(current_date, interval 6 day), '실험용 영화 제목1'),
       (2, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 2 day), '로맨스', 'ALL', 200,
        date_sub(current_date, interval 5 day), '실험용 영화 제목2'),
       (3, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 3 day), '공포', 'ALL', 200,
        date_sub(current_date, interval 4 day), '실험용 영화 제목3'),
       (4, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 4 day), '스릴러', 'ALL', 200,
        date_sub(current_date, interval 3 day), '실험용 영화 제목4'),
       (5, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 5 day), '드라마', 'ALL', 200,
        date_sub(current_date, interval 2 day), '실험용 영화 제목5'),
       (6, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 6 day), '애니', 'ALL', 200,
        date_sub(current_date, interval 1 day), '실험용 영화 제목6'),
       (7, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 14 day), '애니', 'ALL', 200,
        date_add(current_date, interval 7 day), '실험용 영화 제목7'),
       (8, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date, interval 15 day), '애니', 'ALL', 200,
        date_add(current_date, interval 8 day), '실험용 영화 제목8');


-- 스케쥴 등록. 해당 매커니즘은 타이밍을 설정 하면 해당 영화의 상영일에 맞춰서 전부 추가되는 형식.
insert ignore into schedule (id, end_at, start_at, movie_id, no)
VALUES (1, date_add(current_timestamp, interval 210 minute), date_add(current_timestamp, interval 10 minute), 1, 1),
       (2, date_add(current_timestamp, interval 440 minute), date_add(current_timestamp, interval 240 minute), 1, 1),
       (3, date_add(current_timestamp, interval 670 minute), date_add(current_timestamp, interval 470 minute), 1, 1),
       (4, date_add(current_timestamp, interval 210 minute), date_add(current_timestamp, interval 10 minute), 2, 2),
       (5, date_add(current_timestamp, interval 440 minute), date_add(current_timestamp, interval 240 minute), 2, 2),
       (6, date_add(current_timestamp, interval 670 minute), date_add(current_timestamp, interval 470 minute), 2, 2),
       (7, date_add(current_timestamp, interval 210 minute), date_add(current_timestamp, interval 10 minute), 3, 3),
       (8, date_add(current_timestamp, interval 440 minute), date_add(current_timestamp, interval 240 minute), 3, 3),
       (9, date_add(current_timestamp, interval 670 minute), date_add(current_timestamp, interval 470 minute), 3, 3),
       (10, date_add(current_timestamp, interval 210 minute), date_add(current_timestamp, interval 10 minute), 4, 4),
       (11, date_add(current_timestamp, interval 440 minute), date_add(current_timestamp, interval 240 minute), 4, 4),
       (12, date_add(current_timestamp, interval 670 minute), date_add(current_timestamp, interval 470 minute), 4, 4),
       (13, date_add(current_timestamp, interval 1000 minute), date_add(current_timestamp, interval 800 minute), 5, 1),
       (14, date_add(current_timestamp, interval 1240 minute), date_add(current_timestamp, interval 1040 minute), 5, 1),
       (15, date_add(current_timestamp, interval 1480 minute), date_add(current_timestamp, interval 1280 minute), 5, 3),
       (16, date_add(current_timestamp, interval 1000 minute), date_add(current_timestamp, interval 800 minute), 6, 4),
       (17, date_add(current_timestamp, interval 1240 minute), date_add(current_timestamp, interval 1040 minute), 6, 2),
       (18, date_add(current_timestamp, interval 1480 minute), date_add(current_timestamp, interval 1280 minute), 6, 2);

-- member 등록
insert ignore into member (phone, create_at, point)
    VALUES ('01012345678', current_timestamp, 0),
           ('01088771113', current_timestamp, 1000000);

insert ignore into discount_policy(id,activation, condition_type, discount_type, discount_value, end_at, policy_name,
                                   start_at)
VALUES (1,1, 'AGE', 'WON', 2000, date_add(current_timestamp, interval 200 day), '청소년 할인', current_timestamp),
       (2,1, 'COUPON', 'WON', 2000, date_add(current_timestamp, interval 200 day), '쿠폰 할인', current_timestamp),
       (3,1, 'JOB', 'RATIO', 20, date_add(current_timestamp, interval 200 day), '직업 할인', current_timestamp),
       (4,1, 'TIME', 'RATIO', 10, date_add(current_timestamp, interval 200 day), '조조심야 할인', current_timestamp);

insert ignore into coupon(coupon_num, policy_id)
VALUES ('testCoupon01', 2),
       ('testCoupon02', 2),
       ('testCoupon03', 2),
       ('testCoupon04', 2),
       ('testCoupon05', 2),
       ('testCoupon06', 2),
       ('testCoupon07', 2),
       ('testCoupon08', 2),
       ('testCoupon09', 2),
       ('testCoupon10', 2),
       ('testCoupon11', 2),
       ('ageDiscount0', 1),
       ('jobDiscount0', 3),
       ('timeDiscount', 4);

insert ignore into bonus_policy (id, activation, end_at, give_value, policy_name, start_at)
VALUES (1, 1, date_add(current_timestamp, interval 100 day), 5, '기본', current_timestamp),
       (2, 1, date_add(current_timestamp, interval 100 day), 20, 'VIP', current_timestamp);

insert ignore into reservation_details (id, phone, schedule_id, returned)
VALUES (1,  '01012345678', 1,0),
       (2,  '01012345678', 2,0),
       (3,  '01012345678', 3,0),
       (4,  '01012345678', 4,0),
       (5,  '01012345678', 5,0),
       (6,  '01012345678', 6,0);


insert ignore into reservation_seat (id, seat_number, reservation_id)
VALUES (1, 'A1', 1),
       (2, 'A2', 1),
       (3, 'A3', 1),
       (4, 'A4', 1),
       (5, 'A1', 2),
       (6, 'A2', 2),
       (7, 'A3', 2),
       (8, 'A4', 2),
       (9, 'A1', 3),
       (10, 'A2', 3),
       (11, 'A3', 3),
       (12, 'A4', 3),
       (13, 'A1', 4),
       (14, 'A2', 4),
       (15, 'A3', 4),
       (16, 'A4', 4),
       (17, 'A1', 5),
       (18, 'A2', 5),
       (19, 'A3', 5),
       (20, 'A4', 5),
       (21, 'A1', 6),
       (22, 'A2', 6),
       (23, 'A3', 6),
       (24, 'A4', 6);

insert ignore into payment_details (id, cost, status, create_at, use_point, bonus_policy_id, coupon_num, reservation_id, payment_key)
values ('11111111-1111-1111-1111-111111111111', 50000, 'PAY', current_timestamp, 0, 1, null, 1, "card"),
       ('22222222-2222-2222-2222-222222222222', 50000, 'PAY', current_timestamp, 0, 1, null, 2, "card"),
       ('33333333-3333-3333-3333-333333333333', 50000, 'PAY', current_timestamp, 0, 1, null, 3, "card"),
       ('44444444-4444-4444-4444-444444444444', 50000, 'PAY', current_timestamp, 0, 1, null, 4, "card"),
       ('55555555-5555-5555-5555-555555555555', 50000, 'PAY', current_timestamp, 0, 1, null, 5, "card"),
       ('66666666-6666-6666-6666-666666666666', 50000, 'PAY', current_timestamp, 0, 1, null, 6, "card");

insert ignore into point_history (point_id, amount_point, create_at, type, phone, payment_id)
values (1, 200, current_timestamp, 'EARN', '01012345678', '11111111-1111-1111-1111-111111111111'),
       (2, 200, current_timestamp, 'EARN', '01012345678', '22222222-2222-2222-2222-222222222222'),
       (3, 200, current_timestamp, 'EARN', '01012345678', '33333333-3333-3333-3333-333333333333'),
       (4, 200, current_timestamp, 'EARN', '01012345678', '44444444-4444-4444-4444-444444444444'),
       (5, 200, current_timestamp, 'EARN', '01012345678', '55555555-5555-5555-5555-555555555555'),
       (6, 200, current_timestamp, 'EARN', '01012345678', '66666666-6666-6666-6666-666666666666');