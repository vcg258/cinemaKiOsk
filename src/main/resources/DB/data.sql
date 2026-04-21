-- JPA가 생성한 movie.rating ENUM을 DB 기준값으로 변경
-- JPA는 ENUM 이름(FIFTEEN 등)으로 생성하므로 실제 서비스 값(15 등)으로 ALTER
ALTER TABLE movie MODIFY COLUMN rating ENUM('ALL', '12', '15', '19') NOT NULL;

-- 초기 데이터
-- 총관리자(admin) 비밀번호 1234
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
values ('admin', '$2a$10$b0wSz0F5vuSpu5nZFdao1.vMfsXNgS6UTMCIs.g/NNWqaChn.IIju', '관리자', '010-1234-5678', 0, null, now());

-- 직원1 비밀번호 비밀번호 1111
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
VALUES ('staff01', '$2a$10$hOcZ/CS62ll2L6LJuRDKkepyjE4ldp9l3Dwz3LSY7rBI54/MKjZ8m', '직원1', '010-1111-2222', 1, null, now());

-- 직원2 비밀번호 2222
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
VALUES ('staff02', '$2a$10$hJyoakWqW5R2Fn0VB5xC8.ksNIn7P8gi4dsCM3km8cSIckiw9Ocbe', '직원2', '010-3333-4444', 1, null, now());

-- 운영 권한 (일반 관리자 부여 가능) - 7개
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_REFUND', '환불 처리');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_MOVIE_LIST', '영화 목록 조회');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_MOVIE_REGISTER', '영화 등록');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_MOVIE_EDIT', '영화 수정');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_MOVIE_DELETE', '영화 삭제');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_THEATER_LIST', '상영관 조회');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_THEATER_EDIT', '상영관 수정');

-- 최고 관리자 전용 권한 - 5개
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_POLICY_LIST', '정책 조회');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_POLICY_EDIT', '정책 수정');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_STATISTICS', '통계 조회');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_MEMBER_MANAGEMENT', '회원 정보 관리');
INSERT IGNORE INTO admin_role (role_name, role_desc) VALUES ('ROLE_ADMIN_MANAGEMENT', '계정 및 권한 관리');

-- FK 인덱스 적용
-- coupon
CREATE INDEX IF NOT EXISTS idx_coupon_policy_id ON coupon(policy_id);

-- theater
CREATE INDEX IF NOT EXISTS idx_theater_policy_id ON theater(policy_id);

-- schedule
CREATE INDEX IF NOT EXISTS idx_schedule_no ON schedule(no);
CREATE INDEX IF NOT EXISTS idx_schedule_movie_id ON schedule(movie_id);

-- statistics
CREATE INDEX IF NOT EXISTS idx_statistics_schedule_id ON statistics(schedule_id);

-- reservation_details
CREATE INDEX IF NOT EXISTS idx_reservation_details_schedule_id ON reservation_details(schedule_id);
CREATE INDEX IF NOT EXISTS idx_reservation_details_phone ON reservation_details(phone);

-- reservation_seat
CREATE INDEX IF NOT EXISTS idx_reservation_seat_reservation_id ON reservation_seat(reservation_id);

-- payment_details
CREATE INDEX IF NOT EXISTS idx_payment_details_reservation_id ON payment_details(reservation_id);
CREATE INDEX IF NOT EXISTS idx_payment_details_bonus_policy_id ON payment_details(bonus_policy_id);
CREATE INDEX IF NOT EXISTS idx_payment_details_coupon_num ON payment_details(coupon_num);

-- point_history
CREATE INDEX IF NOT EXISTS idx_point_history_payment_id ON point_history(payment_id);
CREATE INDEX IF NOT EXISTS idx_point_history_phone ON point_history(phone);


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

-- 1. 인터스텔라
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '인터스텔라', 'SF', '12', 169, '크리스토퍼 놀란', '매슈 매코너히, 앤 해서웨이', '우주를 넘어 인류의 미래를 향한 위대한 여정', '2026-03-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/gDN2NWtHbs8ZWEBQM8Dh5OVXdb4.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '인터스텔라' AND start_at = '2026-03-01');

-- 2. 더 퍼스트 슬램덩크
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '더 퍼스트 슬램덩크', '애니메이션', 'ALL', 124, '이노우에 다케히코', '강백호, 서태웅, 채치수', '북산고의 전국 최강 팀과의 결전', '2026-04-01', '2026-06-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/coiJrdXAXuBkSGDvp9bZ7mkuU6E.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '더 퍼스트 슬램덩크' AND start_at = '2026-04-01');

-- 3. 다크 나이트 라이즈
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '다크 나이트 라이즈', '액션', '12', 164, '크리스토퍼 놀란', '크리스찬 베일, 톰 하디', '고담시를 구하기 위한 배트맨의 마지막 사투', '2026-03-15', '2026-05-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/eq5P71YHwwaacEYKaQ72Wxy6BlT.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '다크 나이트 라이즈' AND start_at = '2026-03-15');

-- 4. 기생충
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '기생충', '드라마', '15', 132, '봉준호', '송강호, 이선균, 조여정', '두 가족의 만남이 만들어낸 예측 불가의 이야기', '2026-04-10', '2026-07-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/jjHccoFjbqlfr4VGLVLT7yek0Xn.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '기생충' AND start_at = '2026-04-10');

-- 5. 어벤져스: 인피니티 워
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '어벤져스: 인피니티 워', '액션', '12', 149, '앤서니 루소, 조 루소', '로버트 다우니 주니어, 크리스 에반스', '전 우주를 건 어벤져스의 최대 전투', '2026-04-01', '2026-08-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/kmP6viwzcEkZeoi1LaVcQemcvZh.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '어벤져스: 인피니티 워' AND start_at = '2026-04-01');

-- 6. 헤어질 결심
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '헤어질 결심', '멜로', '15', 138, '박찬욱', '박해일, 탕웨이', '산 위에서 발견된 시신, 그 아내를 향한 형사의 감정', '2026-03-20', '2026-05-20', CURDATE(), 'https://image.tmdb.org/t/p/w500/rXEJ28XDQsogIGqwVEgwM2oDdpl.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '헤어질 결심' AND start_at = '2026-03-20');

-- 7. 범죄도시 4
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '범죄도시 4', '범죄', '15', 109, '허명행', '마동석, 김무열', '마석도가 또 다시 나쁜 놈들을 쓸어버린다', '2026-04-20', '2026-06-20', CURDATE(), 'https://image.tmdb.org/t/p/w500/jucHQwnRSma1O9V2bM007e4eSd7.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '범죄도시 4' AND start_at = '2026-04-20');

-- 8. 신과함께: 죄와 벌
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '신과함께: 죄와 벌', '판타지', '12', 139, '김용화', '하정우, 차태현, 주지훈', '사후 세계 49일간의 7번의 재판', '2026-04-05', '2026-07-05', CURDATE(), 'https://image.tmdb.org/t/p/w500/z54zrEXAKd5u1SCPNOIAKgvGXte.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '신과함께: 죄와 벌' AND start_at = '2026-04-05');

-- 9. 듄: 파트 2
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '듄: 파트 2', 'SF', '12', 166, '드니 빌뇌브', '티모시 샬라메, 젠데이아', '폴 아트레이데스의 운명과 아라키스의 전쟁', '2026-03-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/8AsDR2o5AC3V8Jmj6JH6cpta7dz.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '듄: 파트 2' AND start_at = '2026-03-01');

-- 10. 부산행
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '부산행', '호러', '15', 118, '연상호', '공유, 정유미, 마동석', '좀비 바이러스가 창궐한 열차 안에서의 생존기', '2026-04-15', '2026-06-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/6XvEZVBFFjybvb1yQd1qfOC6F2S.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '부산행' AND start_at = '2026-04-15');

-- 11. 탑건: 매버릭
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '탑건: 매버릭', '액션', '12', 130, '조셉 코신스키', '톰 크루즈, 마일스 텔러', '30년 만에 다시 소환된 전설의 파일럿', '2026-03-10', '2026-06-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/jeqXUwNilvNqNXqAHsdwm5pEfae.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '탑건: 매버릭' AND start_at = '2026-03-10');

-- 12. 곡성
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '곡성', '미스터리', '15', 156, '나홍진', '곽도원, 황정민, 천우희', '외지인이 나타난 뒤 마을을 덮친 기이한 사건들', '2026-04-25', '2026-06-25', CURDATE(), 'https://image.tmdb.org/t/p/w500/k9AKtgRErXjz14lFHL2IJVCgwOT.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '곡성' AND start_at = '2026-04-25');

-- 13. 너의 이름은
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '너의 이름은', '애니메이션', 'ALL', 106, '신카이 마코토', '카미키 류노스케, 카미시라이시 모네', '서로의 몸이 뒤바뀐 두 소년소녀의 신비로운 이야기', '2026-04-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/2DJCufz3Oa703PbLjNX1pM6MCG2.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '너의 이름은' AND start_at = '2026-04-01');

-- 14. 한산: 용의 출현
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '한산: 용의 출현', '사극', '12', 129, '김한민', '박해일, 변요한', '이순신 장군의 한산도 대첩', '2026-03-25', '2026-05-25', CURDATE(), 'https://image.tmdb.org/t/p/w500/9zPOnQfvWJde1bLR9Y6DF1WUlnJ.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '한산: 용의 출현' AND start_at = '2026-03-25');

-- 15. 조커
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '조커', '스릴러', '15', 122, '토드 필립스', '호아킨 피닉스, 로버트 드 니로', '광기로 물들어가는 한 남자의 비극적 탄생', '2026-04-01', '2026-07-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/6OnFzi7nU6t4j1rmX9QI8EYDWb4.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '조커' AND start_at = '2026-04-01');

-- 16. 인셉션
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '인셉션', 'SF', '12', 148, '크리스토퍼 놀란', '레오나르도 디카프리오, 조셉 고든-레빗', '꿈속에 침투해 생각을 훔치는 자들의 이야기', '2026-03-05', '2026-05-05', CURDATE(), 'https://image.tmdb.org/t/p/w500/atSxEGstxXRoSKDQFBgqQ5lpGSt.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '인셉션' AND start_at = '2026-03-05');

-- 17. 매트릭스: 리저렉션
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '매트릭스: 리저렉션', 'SF', '15', 148, '라나 워쇼스키', '키아누 리브스, 캐리-앤 모스', '다시 소환된 네오, 현실과 가상의 경계에서', '2026-04-12', '2026-06-12', CURDATE(), 'https://image.tmdb.org/t/p/w500/AvWlbyLk32HdvSrrSTYpjmXHEXC.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '매트릭스: 리저렉션' AND start_at = '2026-04-12');

-- 18. 올드보이
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '올드보이', '스릴러', '19', 120, '박찬욱', '최민식, 유지태, 강혜정', '15년 감금 후 풀려난 남자의 복수와 진실', '2026-03-15', '2026-05-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/e8xZvpCS2cHIPSc5WojNaSNvJJS.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '올드보이' AND start_at = '2026-03-15');

-- 19. 라이온 킹
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '라이온 킹', '가족', 'ALL', 118, '존 파브로', '도널드 글로버, 비욘세', '왕의 자리를 되찾기 위한 심바의 여정', '2026-04-10', '2026-07-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/9Y048zYw66TWvpUtsiNK0uReiVX.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '라이온 킹' AND start_at = '2026-04-10');

-- 20. 반지의 제왕: 반지 원정대
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path)
SELECT '반지의 제왕: 반지 원정대', '판타지', '12', 178, '피터 잭슨', '일라이저 우드, 이언 매켈런', '절대반지를 파괴하기 위한 원정대의 출발', '2026-04-01', '2026-08-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/r18JdjImbWDRwkbVDIzWoLQqkCo.jpg'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM movie WHERE title = '반지의 제왕: 반지 원정대' AND start_at = '2026-04-01');


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

insert ignore into coupon(coupon_num, policy_id, status)
VALUES ('testCoupon01', 2, true),
       ('testCoupon02', 2, true),
       ('testCoupon03', 2, true),
       ('testCoupon04', 2, true),
       ('testCoupon05', 2, true),
       ('testCoupon06', 2, true),
       ('testCoupon07', 2, true),
       ('testCoupon08', 2, true),
       ('testCoupon09', 2, true),
       ('testCoupon10', 2, true),
       ('testCoupon11', 2, true),
       ('ageDiscount0', 1, true),
       ('jobDiscount0', 3, true),
       ('timeDiscount', 4, false);

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