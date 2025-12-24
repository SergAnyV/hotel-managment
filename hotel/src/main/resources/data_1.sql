-- Сначала очищаем таблицы с внешними ключами (дочерние)
DELETE FROM booking_service;
DELETE FROM report_attachments;
DELETE FROM notifications;
DELETE FROM reports;
DELETE FROM bookings;
DELETE FROM user_type_job_type;

-- Затем очищаем родительские таблицы
DELETE FROM services;
DELETE FROM job_types;
DELETE FROM rooms;
DELETE FROM promo_codes;
DELETE FROM users;
DELETE FROM user_types;




