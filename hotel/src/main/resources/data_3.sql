
-- Insert для User (пользователи)
INSERT INTO users (nick_name, first_name, fathers_name, last_name, email, phone, password, created_at, updated_at, role_id, verification_token, verify_status) VALUES
('admin_ivan', 'Иван', 'Петрович', 'Сидоров', 'anivlg@ydex.ru', '79161234567', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 1,
 'f47ac10b-58cc-4372-a567-0e02b2c3d479', true),
('manager_olga', 'Ольга', 'Сергеевна', 'Иванова', 'managerOOOOOOO@hoOOOOOtel.ru', '79162345678', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 2,
 '6ba7b810-9dad-11d1-80b4-00c04fd430c8', true),
('cook_andrey', 'Андрей', 'Михайлович', 'Петров', 'cookOOOOOOO@hoOOOOOOtel.ru', '79163456789', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 3,
 '7c9e6679-7425-40de-944b-e07fc1f90ae7', true),
('cleaner_maria', 'Мария', 'Александровна', 'Смирнова', 'tihonicovsergej@gmail.com', '79164567890', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 4,
 'a1b2c3d4-e5f6-7890-1234-567890abcdef', true),
('kitchen_alex', 'Алексей', 'Дмитриевич', 'Кузнецов', 'kitchen@hotel.ru', '79165678901', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 5,
 'b2c3d4e5-f6a7-8901-2345-67890abcdef1', true),
('reception_anna', 'Анна', 'Владимировна', 'Попова', 'reception@hotel.ru', '79166789012', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 6,
 'c3d4e5f6-a7b8-9012-3456-7890abcdef12', true),
('accountant_dmitry', 'Дмитрий', 'Игоревич', 'Лебедев', 'accountant@hotel.ru', '79167890123', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 7,
 'd4e5f6a7-b8c9-0123-4567-890abcdef123', true),
('spa_elena', 'Елена', 'Николаевна', 'Волкова', 'spa@hotel.ru', '79168901234', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 8,
 'e5f6a7b8-c9d0-1234-5678-90abcdef1234', true),
('tech_sergey', 'Сергей', 'Андреевич', 'Морозов', 'tech@hotel.ru', '79169012345', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 9,
 'f6a7b8c9-d0e1-2345-6789-0abcdef12345', true),
('guide_alexandra', 'Александра', 'Павловна', 'Новикова', 'guide@hotel.ru', '79160123456', '$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi', NOW(), NOW(), 10,
 'a7b8c9d0-e1f2-3456-7890-abcdef123456', true);

-- Insert для PromoCode (промокоды)
INSERT INTO promo_codes (code, discount_type, description, discount_value, valid_from, valid_until, is_active) VALUES
('SUMMER2024', 'PERCENT', 'скидка в виде процента от стоимости', 15.00, '2024-06-01', '2024-08-31', true),
('WELCOME1000', 'FIXED', 'фиксированая скидка', 1000.00, '2024-01-01', '2024-12-31', true),
('WINTER500', 'FIXED', 'фиксированая скидка', 500.00, '2024-12-01', '2025-02-28', false),
('LOYALTY10', 'PERCENT', 'скидка в виде процента от стоимости', 10.00, '2024-03-01', '2025-03-01', true),
('NEWYEAR25', 'PERCENT', 'скидка в виде процента от стоимости', 25.00, '2024-12-20', '2025-01-10', true),
('SPRING20', 'PERCENT', 'весенняя скидка', 20.00, '2025-03-01', '2025-05-31', true),
('FAMILY500', 'FIXED', 'скидка для семей', 500.00, '2024-01-01', '2025-12-31', true),
('VIP30', 'PERCENT', 'эксклюзивная скидка для VIP', 30.00, '2024-01-01', '2025-12-31', false),
('EARLY10', 'PERCENT', 'скидка за раннее бронирование', 10.00, '2024-01-01', '2025-12-31', true),
('LASTMINUTE', 'FIXED', 'скидка за бронирование в последний момент', 300.00, '2024-01-01', '2025-12-31', true);
