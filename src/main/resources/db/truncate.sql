SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE admin_role_map;
TRUNCATE TABLE admin_role;
TRUNCATE TABLE admin;
TRUNCATE TABLE point_history;
TRUNCATE TABLE payment_details;
TRUNCATE TABLE reservation_seat;
TRUNCATE TABLE reservation_details;
TRUNCATE TABLE statistics;
TRUNCATE TABLE schedule;
TRUNCATE TABLE movie;
TRUNCATE TABLE theater;
TRUNCATE TABLE seat_policy;
TRUNCATE TABLE coupon;
TRUNCATE TABLE discount_policy;
TRUNCATE TABLE bonus_policy;
TRUNCATE TABLE member;

SET FOREIGN_KEY_CHECKS = 1;
