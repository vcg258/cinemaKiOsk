-- 초기 데이터
INSERT IGNORE INTO admin (login_id, password, name, admin_phone, level, UUID, create_at)
values (1, 1, '관리자', '010-1234-5678', '0', null, now());

-- JPA가 생성한 movie.rating ENUM을 DB 기준값으로 변경
-- JPA는 ENUM 이름(FIFTEEN 등)으로 생성하므로 실제 서비스 값(15 등)으로 ALTER
ALTER TABLE movie MODIFY COLUMN rating ENUM('ALL', '12', '15', '19') NOT NULL;


-- 기초 데이터 입력 테스트
insert ignore into seat_policy (cost, name)
values (5000, '일반'),
       (10000, '리클라이너'),
       (15000, '커플석'),
       (7000, 'VIP');

insert ignore into theater (no, cleanup_time, policy_id)
VALUES (1, 10, 1),
       (2, 20, 2),
       (3, 30, 3),
       (4, 40, 4);

insert ignore into movie (movie_id, actors, create_at, description, director, end_at, genre, rating, runtime, start_at,
                          title)
VALUES (1, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 1 day), '액션', 'ALL', 200, date_sub(current_date,interval 6 day), '실험용 영화 제목1'),
       (2, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 2 day), '로맨스', 'ALL', 200, date_sub(current_date,interval 5 day), '실험용 영화 제목2'),
       (3, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 3 day), '공포', 'ALL', 200, date_sub(current_date,interval 4 day), '실험용 영화 제목3'),
       (4, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 4 day), '스릴러', 'ALL', 200, date_sub(current_date,interval 3 day), '실험용 영화 제목4'),
       (5, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 5 day), '드라마', 'ALL', 200, date_sub(current_date,interval 2 day), '실험용 영화 제목5'),
       (6, '연기자 목록', current_timestamp, '설명임', '감독', date_add(current_date,interval 6 day), '애니', 'ALL', 200, date_sub(current_date,interval 1 day), '실험용 영화 제목6');


-- 스케쥴 등록. 해당 매커니즘은 타이밍을 설정 하면 해당 영화의 상영일에 맞춰서 전부 추가되는 형식.
insert ignore into schedule (id, end_at, start_at, movie_id, no)
VALUES (1,CURRENT_TIMESTAMP, date_add(current_timestamp,interval 200 minute), 1,1),
       (2,CURRENT_TIMESTAMP, date_add(current_timestamp,interval 200 minute), 1,2),
       (3,CURRENT_TIMESTAMP, date_add(current_timestamp,interval 200 minute), 1,3);

-- member 등록
insert ignore  into member (phone, create_at, point)
    VALUE ('01088771113', current_timestamp, 0)


