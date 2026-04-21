USE `cinema_kiosk`;

-- 영화 20개와 4월달에서 랜덤 날짜 스케쥴 100개 생성


-- 1. 외래 키 체크 해제
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 원하는 삭제 작업 수행
TRUNCATE TABLE schedule;
TRUNCATE TABLE movie;

-- 3. 외래 키 체크 다시 설정 (필수)
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO movie (title, genre, rating, runtime, director, actors, description, start_at, end_at, create_at, poster_path) VALUES

-- 인터스텔라 (2014)
('인터스텔라', 'SF', '12', 169, '크리스토퍼 놀란', '매슈 매코너히, 앤 해서웨이', '우주를 넘어 인류의 미래를 향한 위대한 여정', '2026-03-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/gDN2NWtHbs8ZWEBQM8Dh5OVXdb4.jpg'),
-- 더 퍼스트 슬램덩크 (2022)
('더 퍼스트 슬램덩크', '애니메이션', 'ALL', 124, '이노우에 다케히코', '강백호, 서태웅, 채치수', '북산고의 전국 최강 팀과의 결전', '2026-04-01', '2026-06-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/coiJrdXAXuBkSGDvp9bZ7mkuU6E.jpg'),
-- 다크 나이트 라이즈 (2012)
('다크 나이트 라이즈', '액션', '12', 164, '크리스토퍼 놀란', '크리스찬 베일, 톰 하디', '고담시를 구하기 위한 배트맨의 마지막 사투', '2026-03-15', '2026-05-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/eq5P71YHwwaacEYKaQ72Wxy6BlT.jpg'),
-- 기생충 (2019)
('기생충', '드라마', '15', 132, '봉준호', '송강호, 이선균, 조여정', '두 가족의 만남이 만들어낸 예측 불가의 이야기', '2026-04-10', '2026-07-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/jjHccoFjbqlfr4VGLVLT7yek0Xn.jpg'),
-- 어벤져스: 인피니티 워 (2018)
('어벤져스: 인피니티 워', '액션', '12', 149, '앤서니 루소, 조 루소', '로버트 다우니 주니어, 크리스 에반스', '전 우주를 건 어벤져스의 최대 전투', '2026-04-01', '2026-08-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/kmP6viwzcEkZeoi1LaVcQemcvZh.jpg'),
-- 헤어질 결심 (2022)
('헤어질 결심', '멜로', '15', 138, '박찬욱', '박해일, 탕웨이', '산 위에서 발견된 시신, 그 아내를 향한 형사의 감정', '2026-03-20', '2026-05-20', CURDATE(), 'https://image.tmdb.org/t/p/w500/rXEJ28XDQsogIGqwVEgwM2oDdpl.jpg'),
-- 범죄도시 4 (2024)
('범죄도시 4', '범죄', '15', 109, '허명행', '마동석, 김무열', '마석도가 또 다시 나쁜 놈들을 쓸어버린다', '2026-04-20', '2026-06-20', CURDATE(), 'https://image.tmdb.org/t/p/w500/jucHQwnRSma1O9V2bM007e4eSd7.jpg'),
-- 신과함께: 죄와 벌 (2017)
('신과함께: 죄와 벌', '판타지', '12', 139, '김용화', '하정우, 차태현, 주지훈', '사후 세계 49일간의 7번의 재판', '2026-04-05', '2026-07-05', CURDATE(), 'https://image.tmdb.org/t/p/w500/z54zrEXAKd5u1SCPNOIAKgvGXte.jpg'),
-- 듄: 파트 2 (2024)
('듄: 파트 2', 'SF', '12', 166, '드니 빌뇌브', '티모시 샬라메, 젠데이아', '폴 아트레이데스의 운명과 아라키스의 전쟁', '2026-03-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/8AsDR2o5AC3V8Jmj6JH6cpta7dz.jpg'),
-- 부산행 (2016)
('부산행', '호러', '15', 118, '연상호', '공유, 정유미, 마동석', '좀비 바이러스가 창궐한 열차 안에서의 생존기', '2026-04-15', '2026-06-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/6XvEZVBFFjybvb1yQd1qfOC6F2S.jpg'),
-- 탑건: 매버릭 (2022)
('탑건: 매버릭', '액션', '12', 130, '조셉 코신스키', '톰 크루즈, 마일스 텔러', '30년 만에 다시 소환된 전설의 파일럿', '2026-03-10', '2026-06-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/jeqXUwNilvNqNXqAHsdwm5pEfae.jpg'),
-- 곡성 (2016)
('곡성', '미스터리', '15', 156, '나홍진', '곽도원, 황정민, 천우희', '외지인이 나타난 뒤 마을을 덮친 기이한 사건들', '2026-04-25', '2026-06-25', CURDATE(), 'https://image.tmdb.org/t/p/w500/k9AKtgRErXjz14lFHL2IJVCgwOT.jpg'),
-- 너의 이름은 (2016)
('너의 이름은', '애니메이션', 'ALL', 106, '신카이 마코토', '카미키 류노스케, 카미시라이시 모네', '서로의 몸이 뒤바뀐 두 소년소녀의 신비로운 이야기', '2026-04-01', '2026-05-30', CURDATE(), 'https://image.tmdb.org/t/p/w500/2DJCufz3Oa703PbLjNX1pM6MCG2.jpg'),
-- 한산: 용의 출현 (2022)
('한산: 용의 출현', '사극', '12', 129, '김한민', '박해일, 변요한', '이순신 장군의 한산도 대첩', '2026-03-25', '2026-05-25', CURDATE(), 'https://image.tmdb.org/t/p/w500/9zPOnQfvWJde1bLR9Y6DF1WUlnJ.jpg'),
-- 조커 (2019)
('조커', '스릴러', '15', 122, '토드 필립스', '호아킨 피닉스, 로버트 드 니로', '광기로 물들어가는 한 남자의 비극적 탄생', '2026-04-01', '2026-07-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/6OnFzi7nU6t4j1rmX9QI8EYDWb4.jpg'),
-- 인셉션 (2010)
('인셉션', 'SF', '12', 148, '크리스토퍼 놀란', '레오나르도 디카프리오, 조셉 고든-레빗', '꿈속에 침투해 생각을 훔치는 자들의 이야기', '2026-03-05', '2026-05-05', CURDATE(), 'https://image.tmdb.org/t/p/w500/atSxEGstxXRoSKDQFBgqQ5lpGSt.jpg'),
-- 매트릭스: 리저렉션 (2021)
('매트릭스: 리저렉션', 'SF', '15', 148, '라나 워쇼스키', '키아누 리브스, 캐리-앤 모스', '다시 소환된 네오, 현실과 가상의 경계에서', '2026-04-12', '2026-06-12', CURDATE(), 'https://image.tmdb.org/t/p/w500/AvWlbyLk32HdvSrrSTYpjmXHEXC.jpg'),
-- 올드보이 (2003)
('올드보이', '스릴러', '19', 120, '박찬욱', '최민식, 유지태, 강혜정', '15년 감금 후 풀려난 남자의 복수와 진실', '2026-03-15', '2026-05-15', CURDATE(), 'https://image.tmdb.org/t/p/w500/e8xZvpCS2cHIPSc5WojNaSNvJJS.jpg'),
-- 라이온 킹 (2019)
('라이온 킹', '가족', 'ALL', 118, '존 파브로', '도널드 글로버, 비욘세', '왕의 자리를 되찾기 위한 심바의 여정', '2026-04-10', '2026-07-10', CURDATE(), 'https://image.tmdb.org/t/p/w500/9Y048zYw66TWvpUtsiNK0uReiVX.jpg'),
-- 반지의 제왕: 반지 원정대 (2001)
('반지의 제왕: 반지 원정대', '판타지', '12', 178, '피터 잭슨', '일라이저 우드, 이언 매켈런', '절대반지를 파괴하기 위한 원정대의 출발', '2026-04-01', '2026-08-01', CURDATE(), 'https://image.tmdb.org/t/p/w500/r18JdjImbWDRwkbVDIzWoLQqkCo.jpg');





-- 1. 제약 조건 잠시 해제 및 기존 데이터 정리
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `schedule`;
TRUNCATE TABLE `reservation_details`;
TRUNCATE TABLE `payment_details`;
SET FOREIGN_KEY_CHECKS = 1;

DELIMITER $$

DROP PROCEDURE IF EXISTS mega_dummy_gen$$

CREATE PROCEDURE mega_dummy_gen()
BEGIN
    DECLARE i INT DEFAULT 1; -- 영화 루프
    DECLARE j INT;           -- 스케줄 루프
    DECLARE k INT;           -- 예매 루프
    DECLARE res_id VARCHAR(36);
    DECLARE pay_id VARCHAR(36);
    DECLARE rand_seats INT;
    DECLARE current_sch_id BIGINT;

    WHILE i <= 20 DO
        IF (SELECT EXISTS(SELECT 1 FROM movie WHERE movie_id = i)) THEN
            SET j = 1;
            WHILE j <= 50 DO -- 영화당 스케줄 50개씩 (총 1000개)
                -- 1. 스케줄 생성
                SET @start = STR_TO_DATE(
                        CONCAT('2026-04-', FLOOR(1 + RAND() * 30), ' ', FLOOR(8 + RAND() * 15), ':',
                               LPAD(FLOOR(RAND() * 6), 1, '0'), '0:00'), -- 정시 혹은 10분단위
                        '%Y-%m-%d %H:%i:%s'
                    );

INSERT INTO `schedule` (`no`, `movie_id`, `start_at`, `end_at`, `activation`)
VALUES (FLOOR(1 + RAND() * 3), i, @start, DATE_ADD(@start, INTERVAL 150 MINUTE), TRUE);

SET current_sch_id = LAST_INSERT_ID();

                -- 2. 해당 스케줄에 랜덤 예매 생성 (스케줄당 5~15번의 결제 발생)
                SET k = 1;
                SET rand_seats = FLOOR(5 + RAND() * 11);
                WHILE k <= rand_seats DO
                    SET res_id = UUID();
                    SET pay_id = UUID();

                    -- 예매 상세
INSERT INTO `reservation_details` (`id`, `schedule_id`, `phone`, `create_at`)
VALUES (res_id, current_sch_id, NULL, DATE_SUB(@start, INTERVAL FLOOR(10 + RAND() * 50) MINUTE));

-- 예매 좌석 (결제 1건당 1~2개 좌석 랜덤)
SET @s_count = FLOOR(1 + RAND() * 2);
INSERT INTO `reservation_seat` (`reservation_id`, `seat_number`) VALUES (res_id, CONCAT('A', k));
IF @s_count = 2 THEN
                        INSERT INTO `reservation_seat` (`reservation_id`, `seat_number`) VALUES (res_id, CONCAT('B', k));
END IF;

                    -- 결제 상세 (실제 통계에 잡힐 데이터)
INSERT INTO `payment_details` (`id`, `reservation_id`, `cost`, `create_at`, `status`)
VALUES (pay_id, res_id, (@s_count * 12000), DATE_SUB(@start, INTERVAL 5 MINUTE), 'PAY');

SET k = k + 1;
END WHILE;

                SET j = j + 1;
END WHILE;
END IF;
        SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

-- 실행
CALL mega_dummy_gen();
















