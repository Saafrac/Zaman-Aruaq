-- Дополнительные SQL скрипты для более детального тестирования
-- Выполните после основного скрипта insert_test_data.sql

-- Дополнительные транзакции для более реалистичных данных
-- Транзакции за предыдущие месяцы (январь-май 2024)
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
-- Январь 2024 - дополнительные транзакции
(41, 1, 15000.00, 'EXPENSE', 'UTILITIES', 'Коммунальные услуги', '2024-01-05', NOW(), NOW()),
(42, 1, 20000.00, 'EXPENSE', 'HEALTHCARE', 'Стоматология', '2024-01-12', NOW(), NOW()),
(43, 1, 5000.00, 'INCOME', 'BONUS', 'Премия', '2024-01-25', NOW(), NOW()),

-- Февраль 2024 - дополнительные транзакции
(44, 1, 15000.00, 'EXPENSE', 'UTILITIES', 'Коммунальные услуги', '2024-02-05', NOW(), NOW()),
(45, 1, 10000.00, 'EXPENSE', 'CLOTHING', 'Одежда', '2024-02-14', NOW(), NOW()),
(46, 1, 25000.00, 'EXPENSE', 'ENTERTAINMENT', 'Ресторан', '2024-02-20', NOW(), NOW()),

-- Март 2024 - дополнительные транзакции
(47, 1, 15000.00, 'EXPENSE', 'UTILITIES', 'Коммунальные услуги', '2024-03-05', NOW(), NOW()),
(48, 1, 30000.00, 'EXPENSE', 'EDUCATION', 'Онлайн курсы', '2024-03-10', NOW(), NOW()),
(49, 1, 15000.00, 'EXPENSE', 'HEALTHCARE', 'Спортзал', '2024-03-15', NOW(), NOW()),

-- Апрель 2024 - дополнительные транзакции
(50, 1, 15000.00, 'EXPENSE', 'UTILITIES', 'Коммунальные услуги', '2024-04-05', NOW(), NOW()),
(51, 1, 20000.00, 'EXPENSE', 'CLOTHING', 'Одежда', '2024-04-20', NOW(), NOW()),
(52, 1, 10000.00, 'INCOME', 'INVESTMENT_RETURN', 'Доходы от инвестиций', '2024-04-25', NOW(), NOW()),

-- Май 2024 - дополнительные транзакции
(53, 1, 15000.00, 'EXPENSE', 'UTILITIES', 'Коммунальные услуги', '2024-05-05', NOW(), NOW()),
(54, 1, 35000.00, 'EXPENSE', 'ENTERTAINMENT', 'Путешествие', '2024-05-15', NOW(), NOW()),
(55, 1, 20000.00, 'EXPENSE', 'HEALTHCARE', 'Медицинское обследование', '2024-05-20', NOW(), NOW());

-- Взносы в финансовые цели
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
-- Взносы в цель "Накопления на квартиру"
(56, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-01-31', NOW(), NOW()),
(57, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-02-29', NOW(), NOW()),
(58, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-03-31', NOW(), NOW()),
(59, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-04-30', NOW(), NOW()),
(60, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-05-31', NOW(), NOW()),
(61, 1, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Накопления на квартиру', '2024-06-30', NOW(), NOW()),

-- Взносы в цель "Образование детей"
(62, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-01-31', NOW(), NOW()),
(63, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-02-29', NOW(), NOW()),
(64, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-03-31', NOW(), NOW()),
(65, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-04-30', NOW(), NOW()),
(66, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-05-31', NOW(), NOW()),
(67, 1, 100000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Образование детей', '2024-06-30', NOW(), NOW()),

-- Взносы в цель "Отпуск" (завершенная цель)
(68, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-01-31', NOW(), NOW()),
(69, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-02-29', NOW(), NOW()),
(70, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-03-31', NOW(), NOW()),
(71, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-04-30', NOW(), NOW()),
(72, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-05-31', NOW(), NOW()),
(73, 1, 50000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Отпуск', '2024-06-30', NOW(), NOW());

-- Транзакции для второго пользователя (Айша)
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
-- Основные транзакции за последние месяцы
(74, 2, 450000.00, 'INCOME', 'SALARY', 'Зарплата за май', '2024-05-31', NOW(), NOW()),
(75, 2, 100000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-05-01', NOW(), NOW()),
(76, 2, 65000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-05-15', NOW(), NOW()),
(77, 2, 35000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-05-20', NOW(), NOW()),
(78, 2, 20000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-05-25', NOW(), NOW()),

-- Взносы в цель "Покупка автомобиля"
(79, 2, 150000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Покупка автомобиля', '2024-05-31', NOW(), NOW()),
(80, 2, 150000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Покупка автомобиля', '2024-06-30', NOW(), NOW());

-- Транзакции для третьего пользователя (Марат)
INSERT INTO transactions (id, user_id, amount, type, category, description, transaction_date, created_at, updated_at) VALUES
-- Основные транзакции за последние месяцы
(81, 3, 600000.00, 'INCOME', 'SALARY', 'Зарплата за май', '2024-05-31', NOW(), NOW()),
(82, 3, 150000.00, 'EXPENSE', 'HOUSING', 'Аренда квартиры', '2024-05-01', NOW(), NOW()),
(83, 3, 85000.00, 'EXPENSE', 'FOOD', 'Продукты питания', '2024-05-15', NOW(), NOW()),
(84, 3, 50000.00, 'EXPENSE', 'TRANSPORT', 'Транспортные расходы', '2024-05-20', NOW(), NOW()),
(85, 3, 35000.00, 'EXPENSE', 'ENTERTAINMENT', 'Развлечения', '2024-05-25', NOW(), NOW()),

-- Взносы в цель "Инвестиции"
(86, 3, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Инвестиции', '2024-05-31', NOW(), NOW()),
(87, 3, 200000.00, 'EXPENSE', 'SAVINGS', 'Взнос в цель: Инвестиции', '2024-06-30', NOW(), NOW());

-- Обновление текущих сумм в финансовых целях на основе взносов
UPDATE financial_goals 
SET current_amount = 3000000.00 + (200000.00 * 6)  -- 6 месяцев взносов
WHERE id = 1;

UPDATE financial_goals 
SET current_amount = 1500000.00 + (100000.00 * 6)  -- 6 месяцев взносов
WHERE id = 2;

UPDATE financial_goals 
SET current_amount = 1000000.00  -- Цель уже выполнена
WHERE id = 3;

UPDATE financial_goals 
SET current_amount = 2000000.00 + (150000.00 * 2)  -- 2 месяца взносов
WHERE id = 4;

UPDATE financial_goals 
SET current_amount = 2500000.00 + (200000.00 * 2)  -- 2 месяца взносов
WHERE id = 5;

-- Проверочные запросы для анализа данных
-- Общая статистика по пользователям
SELECT 
    'Общая статистика' as info,
    COUNT(DISTINCT u.id) as users_count,
    COUNT(DISTINCT fg.id) as goals_count,
    COUNT(DISTINCT t.id) as transactions_count,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as total_income,
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as total_expenses
FROM users u
LEFT JOIN financial_goals fg ON u.id = fg.user_id
LEFT JOIN transactions t ON u.id = t.user_id;

-- Статистика по пользователям
SELECT 
    u.first_name,
    u.last_name,
    u.monthly_income,
    u.monthly_expenses,
    u.current_savings,
    COUNT(DISTINCT fg.id) as active_goals,
    SUM(CASE WHEN fg.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_goals,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as total_income,
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as total_expenses,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - 
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as net_income
FROM users u
LEFT JOIN financial_goals fg ON u.id = fg.user_id
LEFT JOIN transactions t ON u.id = t.user_id
GROUP BY u.id, u.first_name, u.last_name, u.monthly_income, u.monthly_expenses, u.current_savings;

-- Расходы по категориям для первого пользователя
SELECT 
    t.category,
    COUNT(*) as transaction_count,
    SUM(t.amount) as total_amount,
    AVG(t.amount) as avg_amount
FROM transactions t
WHERE t.user_id = 1 AND t.type = 'EXPENSE'
GROUP BY t.category
ORDER BY total_amount DESC;
