# Инструкция по запуску Zaman AI Assistant

## Предварительные требования

- Java 17 или выше
- PostgreSQL 12 или выше
- Gradle 8.0 или выше
- Docker и Docker Compose (опционально)

## Быстрый запуск с Docker

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd Zaman-Aruaq
```

2. Запустите с Docker Compose:
```bash
docker-compose up -d
```

3. Приложение будет доступно по адресу: http://localhost:8080

## Локальная разработка

### 1. Настройка базы данных

Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE zaman_ai_assistant;
CREATE USER zaman_user WITH PASSWORD 'zaman_password';
GRANT ALL PRIVILEGES ON DATABASE zaman_ai_assistant TO zaman_user;
```

### 2. Настройка переменных окружения

Создайте файл `.env` в корне проекта:
```bash
# Database
DB_USERNAME=zaman_user
DB_PASSWORD=zaman_password

# AI API
OPENAI_API_KEY=sk-roG3OusRr0TLCHAADks6lw
OPENAI_BASE_URL=https://openai-hub.neuraldeep.tech

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Admin
ADMIN_PASSWORD=admin123
```

### 3. Запуск приложения

```bash
# Сборка проекта
./gradlew build

# Запуск в режиме разработки
./gradlew bootRun --args='--spring.profiles.active=dev'

# Или запуск JAR файла
java -jar build/libs/zaman-ai-assistant-0.0.1-SNAPSHOT.jar
```

## API Документация

После запуска приложения документация API доступна по адресам:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Тестирование

### Запуск тестов
```bash
# Все тесты
./gradlew test

# Только unit тесты
./gradlew test --tests "*UnitTest"

# Только integration тесты
./gradlew test --tests "*IntegrationTest"
```

### Тестирование API

1. **Регистрация пользователя:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+77001234567",
    "firstName": "Айдар",
    "lastName": "Нурланов",
    "email": "aidar@example.com",
    "monthlyIncome": 500000,
    "monthlyExpenses": 300000,
    "currentSavings": 100000
  }'
```

2. **Вход в систему:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+77001234567",
    "password": "password"
  }'
```

3. **Отправка сообщения AI-ассистенту:**
```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "message": "Хочу накопить на квартиру за 5 лет"
  }'
```

4. **Создание финансовой цели:**
```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Квартира в Алматы",
    "description": "2-комнатная квартира в центре города",
    "type": "APARTMENT",
    "priority": "HIGH",
    "targetAmount": 50000000,
    "targetDate": "2029-12-31",
    "monthlyContribution": 500000
  }'
```

## Мониторинг

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Метрики
```bash
curl http://localhost:8080/actuator/metrics
```

## Структура проекта

```
src/
├── main/
│   ├── java/com/zamanbank/aiassistant/
│   │   ├── config/          # Конфигурация Spring
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # JPA сущности
│   │   ├── repository/      # JPA репозитории
│   │   ├── security/       # JWT и безопасность
│   │   └── service/        # Бизнес-логика
│   └── resources/
│       ├── application.yml # Основная конфигурация
│       └── application-dev.yml # Конфигурация разработки
└── test/                   # Тесты
```

## Возможные проблемы

### 1. Ошибка подключения к базе данных
- Проверьте, что PostgreSQL запущен
- Убедитесь в правильности настроек подключения
- Проверьте права пользователя базы данных

### 2. Ошибки AI API
- Проверьте правильность API ключа
- Убедитесь в доступности API endpoint
- Проверьте лимиты запросов

### 3. Ошибки JWT
- Проверьте правильность JWT секрета
- Убедитесь в корректности заголовка Authorization
- Проверьте срок действия токена

## Производственное развертывание

### 1. Настройка для продакшена

Создайте файл `application-prod.yml`:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    base-url: ${OPENAI_BASE_URL}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}

logging:
  level:
    com.zamanbank.aiassistant: INFO
    org.springframework.ai: WARN
```

### 2. Docker для продакшена

```bash
# Сборка образа
docker build -t zaman-ai-assistant:latest .

# Запуск с переменными окружения
docker run -d \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://db:5432/zaman_ai_assistant \
  -e DB_USERNAME=zaman_user \
  -e DB_PASSWORD=zaman_password \
  -e OPENAI_API_KEY=your-api-key \
  -e JWT_SECRET=your-jwt-secret \
  zaman-ai-assistant:latest
```

### 3. Kubernetes

Создайте файл `k8s-deployment.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zaman-ai-assistant
spec:
  replicas: 3
  selector:
    matchLabels:
      app: zaman-ai-assistant
  template:
    metadata:
      labels:
        app: zaman-ai-assistant
    spec:
      containers:
      - name: zaman-ai-assistant
        image: zaman-ai-assistant:latest
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: zaman-secrets
              key: database-url
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: zaman-secrets
              key: openai-api-key
---
apiVersion: v1
kind: Service
metadata:
  name: zaman-ai-assistant-service
spec:
  selector:
    app: zaman-ai-assistant
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## Поддержка

При возникновении проблем:
1. Проверьте логи приложения
2. Убедитесь в правильности конфигурации
3. Проверьте доступность внешних сервисов
4. Обратитесь к команде разработки
