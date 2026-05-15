# 🧤 Smart Glove Core Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF.svg)](https://github.com/features/actions)

This is the core backend service for the **Smart Glove** project — an innovative IoT device for sign language recognition (1st place winner in the IoT competition evaluated by Infineon Technologies). 

The server is responsible for processing telemetry from the glove's sensors (18 data axes), managing users' machine learning models, handling authentication, and orchestrating the neural network training processes.

## 🔗 Live Documentation (API)
The project is deployed on a DigitalOcean cloud server. You can test all endpoints directly in your browser:
👉 **[Open Swagger UI](https://smartglove.duckdns.org/swagger-ui/index.html)**

## 🏗 Architecture & Technologies
The backend is built using a microservices approach with a modern technology stack:

* **Core:** Java 21, Spring Boot 4, Spring Security (JWT)
* **Database:** MongoDB (storing user profiles, gesture datasets, and model metadata)
* **Message Broker:** RabbitMQ (asynchronous communication with the Python ML server for model training)
* **Object Storage:** MinIO (storing compiled `.keras` models and neural network weights)
* **DevOps:** Docker, Docker Compose, GitHub Actions (CI/CD)
* **Security/Proxy:** Caddy Server (automatic HTTPS SSL certificates)

## ⚙️ Local Setup (For Developers)

**1. Clone the repository:**
```bash
git clone [https://github.com/Rozzzchepiy/smartglove-core-backend.git](https://github.com/Rozzzchepiy/smartglove-core-backend.git)
cd smartglove-core-backend