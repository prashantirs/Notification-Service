# 📬 NotifyHub — Multi-Channel Notification System

A production-ready, event-driven notification system built with **Spring Boot 3** and **Apache Kafka**, supporting Email, SMS, and Push channels with retry mechanisms, delivery tracking, and Redis-based rate limiting.

---

## 🏗️ Architecture

```
REST API (Producer)
        │
        ▼
  Apache Kafka
  ┌─────────────────────────────┐
  │  notification.email         │──► Email Consumer → Gmail SMTP
  │  notification.sms           │──► SMS Consumer   → Mock Handler
  │  notification.push          │──► Push Consumer  → Mock Handler
  │  notification.retry         │──► Retry Handler  → DLQ after 3 attempts
  └─────────────────────────────┘
        │
        ▼
   MySQL (Audit Log)     Redis (Rate Limiting)
```

---

## ⚙️ Tech Stack

| Layer             | Technology                          |
|-------------------|-------------------------------------|
| Framework         | Spring Boot 3, Spring MVC           |
| Messaging         | Apache Kafka                        |
| Database          | MySQL 8 + Spring Data JPA/Hibernate |
| Caching           | Redis                               |
| Security          | Spring Security + JWT               |
| Containerization  | Docker + Docker Compose             |
| API Documentation | Swagger / OpenAPI 3.0               |
| Testing           | JUnit 5 + Mockito                   |
| Build Tool        | Maven                               |

---

## ✨ Features

- 🔔 **Multi-channel notifications** — Email (live), SMS & Push (mock)
- ⚡ **Kafka-powered async messaging** — decoupled producer/consumer architecture
- 🔁 **Retry with Dead Letter Queue** — up to 3 retries with exponential backoff
- 📋 **Notification audit log** — every event tracked in MySQL with status
- 🚦 **Redis rate limiting** — max 5 notifications/min per user per channel
- 🔐 **JWT Authentication** — secured endpoints with role-based access
- 📊 **Stats API** — delivery counts by channel and status
- 📄 **Swagger UI** — interactive API documentation

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Docker Desktop

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/your-username/notification-service.git
cd notification-service
```

**2. Start infrastructure (Kafka, MySQL, Redis) via Docker**
```bash
docker-compose up -d
```

Verify all 4 containers are running:
```bash
docker ps
```

You should see:
```
notification-service-kafka-1       ✅ port 9092
notification-service-mysql-1       ✅ port 3307
notification-service-redis-1       ✅ port 6379
notification-service-zookeeper-1   ✅ port 2181
```

**3. Configure Email (Optional — for live email sending)**

Open `src/main/resources/application.properties` and update:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```
> Generate Gmail App Password: Google Account → Security → 2-Step Verification → App Passwords

**4. Run the application**

Open `com.notifyhub.notification_service.NotificationServiceApplication` in IntelliJ and run the main method.

Or via terminal:
```bash
mvn spring-boot:run
```

**5. Access Swagger UI**
```
http://localhost:8081/swagger-ui/index.html
```

---

## 📡 API Endpoints

| Method | Endpoint                    | Description                          |
|--------|-----------------------------|--------------------------------------|
| POST   | `/api/notify`               | Send a single notification           |
| POST   | `/api/notify/bulk`          | Send to multiple recipients          |
| GET    | `/api/notifications`        | Get all notifications (with filters) |
| GET    | `/api/notifications/stats`  | Get delivery stats by channel/status |

### Sample Request
```json
POST /api/notify
{
  "recipient": "user@example.com",
  "channel": "EMAIL",
  "subject": "Welcome to NotifyHub!",
  "message": "Your account has been created successfully."
}
```

### Sample Response
```json
{
  "id": 1,
  "status": "SENT",
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "createdAt": "2026-05-17T17:00:00"
}
```

---

## 🗂️ Project Structure

```
src/main/java/com/notifyhub/
│
├── controller/         # REST API endpoints
├── service/            # Business logic
├── kafka/
│   ├── producer/       # Kafka message publishers
│   └── consumer/       # Kafka message consumers
├── entity/             # JPA entities (DB tables)
├── repository/         # Database queries
├── dto/                # Request/Response objects
└── config/             # Kafka, Security, Redis config
```

---

## 🐳 Docker Services

| Service     | Image                          | Port |
|-------------|--------------------------------|------|
| Kafka       | confluentinc/cp-kafka:7.4.0    | 9092 |
| Zookeeper   | confluentinc/cp-zookeeper:7.4.0| 2181 |
| MySQL       | mysql:8.0                      | 3307 |
| Redis       | redis:7.0                      | 6379 |

---

## 🛑 Stopping the Application

```bash
# Stop Spring Boot app
Ctrl + C  (in terminal)

# Stop Docker containers
docker-compose down

# Stop and remove all data (full reset)
docker-compose down -v
```

---

## 👨‍💻 Author

**Prashant Srivastava**  
Java Backend Developer  
[LinkedIn](https://linkedin.com/in/prashantirs) • [GitHub](https://github.com/prashantirs)

---
