# SQL Скрипты для заполнения базы данных

Этот каталог содержит SQL скрипты для заполнения базы данных тестовыми данными.

## Порядок выполнения скриптов

### 1. Основные данные
```bash
psql -d zaman_bank_assistant -f insert_test_data.sql
```

### 2. Дополнительные данные (опционально)
```bash
psql -d zaman_bank_assistant -f insert_additional_data.sql
```

### 3. Создание индексов для оптимизации
```bash
psql -d zaman_bank_assistant -f create_indexes.sql
```

## Описание данных

### Пользователи
- **Айдар Нурланов** (+77001234567) - основной тестовый пользователь
- **Айша Касымова** (+77007654321) - второй пользователь
- **Марат Ахметов** (+77009876543) - третий пользователь

### Финансовые цели
- Накопления на квартиру (15,000,000₸)
- Образование детей (5,000,000₸)
- Отпуск (1,000,000₸) - завершенная цель
- Покупка автомобиля (8,000,000₸)
- Инвестиции (10,000,000₸)

### Транзакции
- **6 месяцев** данных (январь-июнь 2024)
- **Зарплаты** каждый месяц
- **Расходы** по категориям: жилье, продукты, транспорт, развлечения, здоровье, образование
- **Взносы** в финансовые цели
- **Дополнительные** доходы и расходы

## Структура данных

### Категории транзакций
- **Доходы**: SALARY, BONUS, INVESTMENT_RETURN
- **Расходы**: HOUSING, FOOD, TRANSPORT, ENTERTAINMENT, HEALTHCARE, EDUCATION, CLOTHING, UTILITIES, SAVINGS

### Типы целей
- **HOUSING** - жилье
- **EDUCATION** - образование
- **TRAVEL** - путешествия
- **TRANSPORT** - транспорт
- **INVESTMENT** - инвестиции

### Статусы целей
- **ACTIVE** - активная
- **COMPLETED** - завершенная
- **PAUSED** - приостановленная

## Проверка данных

После выполнения скриптов можно проверить данные:

```sql
-- Общая статистика
SELECT 
    COUNT(DISTINCT u.id) as users,
    COUNT(DISTINCT fg.id) as goals,
    COUNT(DISTINCT t.id) as transactions
FROM users u
LEFT JOIN financial_goals fg ON u.id = fg.user_id
LEFT JOIN transactions t ON u.id = t.user_id;

-- Статистика по пользователям
SELECT 
    u.first_name,
    u.last_name,
    COUNT(t.id) as transactions,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as income,
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as expenses
FROM users u
LEFT JOIN transactions t ON u.id = t.user_id
GROUP BY u.id, u.first_name, u.last_name;
```

## API Endpoints для тестирования

После заполнения базы данных можно тестировать следующие endpoints:

### Отчеты
- `GET /api/reports/monthly` - месячный отчет
- `GET /api/reports/quarterly` - квартальный отчет
- `GET /api/reports/half-yearly` - полугодовой отчет
- `GET /api/reports/yearly` - годовой отчет

### Дашборд
- `GET /api/dashboard` - основной дашборд
- `GET /api/dashboard/analytics?months=6` - аналитика за 6 месяцев
- `GET /api/dashboard/summary` - краткая сводка

### Цели
- `GET /api/goals` - все цели пользователя
- `POST /api/goals` - создание новой цели
- `GET /api/goals/{id}` - конкретная цель
- `PUT /api/goals/{id}` - обновление цели
- `POST /api/goals/{id}/contribute` - взнос в цель

## Очистка данных

Для очистки тестовых данных:

```sql
-- Очистка в правильном порядке (с учетом foreign keys)
DELETE FROM transactions;
DELETE FROM financial_goals;
DELETE FROM users;

-- Сброс последовательностей
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE financial_goals_id_seq RESTART WITH 1;
ALTER SEQUENCE transactions_id_seq RESTART WITH 1;
```
