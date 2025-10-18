-- SQL скрипт для заполнения базы данных тестовыми данными
-- Выполните этот скрипт в вашей PostgreSQL базе данных

-- Очистка существующих данных (опционально)
-- DELETE FROM transactions;
-- DELETE FROM financial_goals;
-- DELETE FROM users;

-- Вставка тестовых пользователей
INSERT INTO users (id, phone_number, first_name, last_name, email, role, status, monthly_income, monthly_expenses, current_savings, created_at, updated_at) VALUES
(1, '+77001234567', 'Айдар', 'Нурланов', 'aidar@example.com', 'CLIENT', 'ACTIVE', 500000.00, 350000.00, 2500000.00, NOW(), NOW()),
(2, '+77007654321', 'Айша', 'Касымова', 'aisha@example.com', 'CLIENT', 'ACTIVE', 450000.00, 300000.00, 1800000.00, NOW(), NOW()),
(3, '+77009876543', 'Марат', 'Ахметов', 'marat@example.com', 'CLIENT', 'ACTIVE', 600000.00, 400000.00, 3200000.00, NOW(), NOW());

-- Вставка финансовых целей
INSERT INTO financial_goals (id, user_id, title, description, type, priority, status, target_amount, current_amount, target_date, monthly_contribution, created_at, updated_at) VALUES
(1, 1, 'Накопления на квартиру', 'Накопления на покупку квартиры в Алматы', 'HOUSING', 'HIGH', 'ACTIVE', 15000000.00, 3000000.00, '2027-12-31', 200000.00, NOW(), NOW()),
(2, 1, 'Образование детей', 'Накопления на образование детей', 'EDUCATION', 'MEDIUM', 'ACTIVE', 5000000.00, 1500000.00, '2026-06-30', 100000.00, NOW(), NOW()),
(3, 1, 'Отпуск', 'Накопления на семейный отпуск', 'TRAVEL', 'LOW', 'COMPLETED', 1000000.00, 1000000.00, '2024-08-31', 50000.00, NOW(), NOW()),
(4, 2, 'Покупка автомобиля', 'Накопления на покупку автомобиля', 'TRANSPORT', 'HIGH', 'ACTIVE', 8000000.00, 2000000.00, '2025-12-31', 150000.00, NOW(), NOW()),
(5, 3, 'Инвестиции', 'Инвестиционный портфель', 'INVESTMENT', 'HIGH', 'ACTIVE', 10000000.00, 2500000.00, '2026-12-31', 200000.00, NOW(), NOW());

-- Вставка транзакций за последние 6 месяцев
-- Январь 2024
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
(1, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за январь', '2024-01-31', NOW(), NOW()),
(2, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-01-01', NOW(), NOW()),
(3, 1, 80000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-01-15', NOW(), NOW()),
(4, 1, 45000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-01-20', NOW(), NOW()),
(5, 1, 30000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-01-25', NOW(), NOW()),

-- Февраль 2024
(6, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за февраль', '2024-02-29', NOW(), NOW()),
(7, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-02-01', NOW(), NOW()),
(8, 1, 75000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-02-15', NOW(), NOW()),
(9, 1, 40000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-02-20', NOW(), NOW()),
(10, 1, 25000.00, 'EXPENSE', 'HEALTHCARE', 'Медицинские услуги', '2024-02-10', NOW(), NOW()),

-- Март 2024
(11, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за март', '2024-03-31', NOW(), NOW()),
(12, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-03-01', NOW(), NOW()),
(13, 1, 90000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-03-15', NOW(), NOW()),
(14, 1, 50000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-03-20', NOW(), NOW()),
(15, 1, 35000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-03-25', NOW(), NOW()),

-- Апрель 2024
(16, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за апрель', '2024-04-30', NOW(), NOW()),
(17, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-04-01', NOW(), NOW()),
(18, 1, 85000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-04-15', NOW(), NOW()),
(19, 1, 42000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-04-20', NOW(), NOW()),
(20, 1, 28000.00, 'EXPENSE', 'EDUCATION', 'Образовательные курсы', '2024-04-10', NOW(), NOW()),

-- Май 2024
(21, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за май', '2024-05-31', NOW(), NOW()),
(22, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-05-01', NOW(), NOW()),
(23, 1, 78000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-05-15', NOW(), NOW()),
(24, 1, 38000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-05-20', NOW(), NOW()),
(25, 1, 32000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-05-25', NOW(), NOW()),

-- Июнь 2024 (текущий месяц)
(26, 1, 500000.00, 'INCOME', 'SALARY', 'Зарплата за июнь', '2024-06-30', NOW(), NOW()),
(27, 1, 120000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-06-01', NOW(), NOW()),
(28, 1, 82000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-06-15', NOW(), NOW()),
(29, 1, 46000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-06-20', NOW(), NOW()),
(30, 1, 27000.00, 'EXPENSE', 'HEALTHCARE', 'Медицинские услуги', '2024-06-10', NOW(), NOW());

-- Транзакции для второго пользователя
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
(31, 2, 450000.00, 'INCOME', 'SALARY', 'Зарплата за июнь', '2024-06-30', NOW(), NOW()),
(32, 2, 100000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-06-01', NOW(), NOW()),
(33, 2, 70000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-06-15', NOW(), NOW()),
(34, 2, 40000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-06-20', NOW(), NOW()),
(35, 2, 25000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-06-25', NOW(), NOW());

-- Транзакции для третьего пользователя
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
(36, 3, 600000.00, 'INCOME', 'SALARY', 'Зарплата за июнь', '2024-06-30', NOW(), NOW()),
(37, 3, 150000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-06-01', NOW(), NOW()),
(38, 3, 90000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-06-15', NOW(), NOW()),
(39, 3, 55000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-06-20', NOW(), NOW()),
(40, 3, 40000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-06-25', NOW(), NOW());

-- Обновление последовательностей (если используется auto-increment)
-- SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
-- SELECT setval('financial_goals_id_seq', (SELECT MAX(id) FROM financial_goals));
-- SELECT setval('transactions_id_seq', (SELECT MAX(id) FROM transactions));

-- Проверка вставленных данных
SELECT 'Users count: ' || COUNT(*) FROM users;
SELECT 'Financial goals count: ' || COUNT(*) FROM financial_goals;
SELECT 'Transactions count: ' || COUNT(*) FROM transactions;

-- Пример запроса для проверки данных пользователя
SELECT 
    u.first_name,
    u.last_name,
    u.monthly_income,
    u.monthly_expenses,
    u.current_savings,
    COUNT(fg.id) as goals_count,
    COUNT(t.id) as transactions_count
FROM users u
LEFT JOIN financial_goals fg ON u.id = fg.user_id
LEFT JOIN transactions t ON u.id = t.user_id
GROUP BY u.id, u.first_name, u.last_name, u.monthly_income, u.monthly_expenses, u.current_savings;
