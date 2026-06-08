# Car Sales Platform

Микросервисный backend для платформы продажи автомобилей. Проект покрывает управление каталогом автомобилей, заказами из наличия и индивидуальными заказами, заявками на тест-драйв, складским учетом, сборочными заказами, аутентификацией и взаимодействием между сервисами.

## Функциональность

- каталог автомобилей, моделей и доступных конфигураций
- фильтрация автомобилей
- оформление заказов на автомобили из наличия
- оформление индивидуальных заказов с выбором комплектации
- управление заявками на тест-драйв
- складской учет автомобилей и комплектующих
- создание и обработка сборочных заказов
- подтверждение или отклонение заказов складским сервисом
- ролевая модель доступа для клиентов, менеджеров, складских администраторов и администраторов

## Технологии

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Liquibase
- Apache Kafka
- gRPC
- Keycloak
- Testcontainers
- JUnit 5
- Mockito
- MapStruct
- Lombok
- Springdoc OpenAPI
- Docker Compose
- Gradle

## Реализация

- `order-service` отвечает за каталог, пользователей, тест-драйвы, stock orders и custom orders.
- `storage-service` отвечает за складской учет, доступные автомобили и assembly orders.
- У каждого доменного сервиса своя PostgreSQL база и свои Liquibase-миграции.
- REST API используется для внешних операций, Swagger UI доступен после запуска сервисов.
- Keycloak выдает JWT-токены, а Spring Security проверяет роли и доступ к ресурсам.
- Kafka используется для асинхронной обработки заказов между `order-service` и `storage-service`.
- Outbox pattern используется для надежной публикации событий в Kafka после изменения данных.
- Таблица `processed_messages` защищает consumers от повторной обработки одного события.
- gRPC используется для синхронного получения доступных автомобилей из складского сервиса.
- Статусы stock/custom заказов реализованы через state pattern.
- MapStruct используется для преобразования DTO, entity и доменных моделей.
- Testcontainers используется в integration-тестах с PostgreSQL.

## Сервисы

- `order-service` - автомобили, пользователи, тест-драйвы, заказы из наличия и индивидуальные заказы
- `storage-service` - складской учет, доступные автомобили и сборочные заказы
- `grpc-contracts` - общие gRPC-контракты для взаимодействия сервисов
- `common-security` - общая конфигурация Spring Security

## Архитектура

Система разделена на независимые Spring Boot сервисы. Каждый сервис владеет своей схемой базы данных. Межсервисное взаимодействие построено на Kafka для асинхронных бизнес-событий и gRPC для синхронных запросов.

### Сценарий Обработки Заказа

1. Пользователь создает заказ на автомобиль из наличия или индивидуальный заказ с конфигурацией.
2. `order-service` сохраняет заказ и переводит его в нужный доменный статус.
3. Для заказов, которые требуют подтверждения склада, сервис сохраняет событие в outbox-таблицу.
4. Outbox publisher публикует событие в Kafka topic `order-events`.
5. `storage-service` получает событие, проверяет наличие автомобиля или компонентов и создает сборочный заказ.
6. Складской сервис публикует результат обработки в Kafka topic `storage-events`.
7. `order-service` получает результат, защищается от повторной обработки по `eventId` и обновляет статус заказа.

## Тестирование

Покрыты ключевые части проекта: бизнес-логика заказов и склада, переходы статусов заказов, security-проверки, Kafka consumers, outbox publishers, gRPC client/server, Liquibase migrations и JPA repositories с PostgreSQL через Testcontainers.

## Быстрый Старт

### Требования

- Java 21
- Docker и Docker Compose

### Переменные Окружения

Создайте локальный `.env` файл на основе примера:

```bash
cp .env.example .env
```

При необходимости измените значения переменных.

### Запуск Через Docker Compose

```bash
docker compose up --build
```

Основные сервисы будут доступны по адресам:

- Сервис заказов: `http://localhost:8080`
- Складской сервис: `http://localhost:8082`
- Keycloak: `http://localhost:8081`
- внешний listener Kafka: `localhost:29092`
- Order PostgreSQL: `localhost:5433`
- Storage PostgreSQL: `localhost:5434`

Swagger UI:

- Order service: `http://localhost:8080/swagger-ui.html`
- Storage service: `http://localhost:8082/swagger-ui.html`

## Сборка И Тесты

Собрать все модули:

```bash
./gradlew build
```

Запустить тесты:

```bash
./gradlew test
```

Запустить integration-тесты:

```bash
./gradlew integrationTest
```

## Структура Репозитория

```text
.
├── common-security
├── grpc-contracts
├── order-service
├── storage-service
├── docker-compose.yml
└── realm-config.json
```
