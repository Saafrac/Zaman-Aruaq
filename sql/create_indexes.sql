-- SQL скрипт для создания индексов для оптимизации запросов
-- Выполните после создания таблиц и вставки данных

-- Индексы для таблицы users
CREATE INDEX IF NOT EXISTS idx_users_phone_number ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Индексы для таблицы financial_goals
CREATE INDEX IF NOT EXISTS idx_financial_goals_user_id ON financial_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_financial_goals_status ON financial_goals(status);
CREATE INDEX IF NOT EXISTS idx_financial_goals_type ON financial_goals(type);
CREATE INDEX IF NOT EXISTS idx_financial_goals_priority ON financial_goals(priority);
CREATE INDEX IF NOT EXISTS idx_financial_goals_target_date ON financial_goals(target_date);

-- Индексы для таблицы transactions
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON transactions(category);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX IF NOT EXISTS idx_transactions_user_type ON transactions(user_id, type);
CREATE INDEX IF NOT EXISTS idx_transactions_user_category ON transactions(user_id, category);

-- Составные индексы для оптимизации отчетов
CREATE INDEX IF NOT EXISTS idx_transactions_user_date_type ON transactions(user_id, transaction_date, type);
CREATE INDEX IF NOT EXISTS idx_transactions_user_date_category ON transactions(user_id, transaction_date, category);

-- Индексы для таблицы conversations (если используется)
-- CREATE INDEX IF NOT EXISTS idx_conversations_user_id ON conversations(user_id);
-- CREATE INDEX IF NOT EXISTS idx_conversations_status ON conversations(status);
-- CREATE INDEX IF NOT EXISTS idx_conversations_type ON conversations(type);

-- Индексы для таблицы messages (если используется)
-- CREATE INDEX IF NOT EXISTS idx_messages_conversation_id ON messages(conversation_id);
-- CREATE INDEX IF NOT EXISTS idx_messages_role ON messages(role);
-- CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);

-- Анализ производительности индексов
ANALYZE users;
ANALYZE financial_goals;
ANALYZE transactions;

-- Проверка использования индексов
-- EXPLAIN (ANALYZE, BUFFERS) 
-- SELECT * FROM transactions 
-- WHERE user_id = 1 AND transaction_date BETWEEN '2024-01-01' AND '2024-12-31';

-- Статистика по индексам
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes 
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
