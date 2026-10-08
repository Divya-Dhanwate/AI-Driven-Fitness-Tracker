# AI-Driven-Fitness-Tracker

A full-stack fitness application built using **Spring Boot Microservices**, **React**, **MySQL**, **Keycloak**, **RabbitMQ**, **Eureka Service Discovery**, **Spring Cloud Gateway**, and an **AI-powered recommendation service**.

The application allows users to manage their fitness activities such as **Running, Cycling, and Swimming** and receive personalized AI-generated recommendations based on their activity data.

---

## 🚀 Features

- 🔐 User authentication and authorization using Keycloak
- 👤 User management
- 🏃 Fitness activity tracking
- 🏊 Support for Running, Cycling, and Swimming
- 🤖 AI-powered fitness recommendations
- 📨 Asynchronous communication using RabbitMQ
- 🔍 Service discovery using Eureka
- 🌐 API Gateway for routing requests
- 🗄️ MySQL database for persistent storage
- 🔒 JWT-based API security
- ⚡ REST APIs using Spring Boot
- 🧩 Microservices architecture

---

# 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │     React Frontend  │
                         │     localhost:5173  │
                         └──────────┬──────────┘
                                    │
                                    │ HTTP / JWT
                                    ▼
                         ┌─────────────────────┐
                         │   Spring Cloud      │
                         │      Gateway        │
                         │     Port: 8083      │
                         └──────────┬──────────┘
                                    │
                  ┌─────────────────┼─────────────────┐
                  │                 │                 │
                  ▼                 ▼                 ▼
          ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
          │ User Service │  │   Activity   │  │  AI Service  │
          │              │  │   Service    │  │              │
          │ Port: 8080   │  │ Port: 8081   │  │ Port: 8082   │
          └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
                 │                 │                  │
                 ▼                 ▼                  ▼
          ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
          │    MySQL     │  │    MySQL     │  │    MySQL     │
          │   user_db    │  │  activity_db │  │recommendations│
          │              │  │              │  │     _db      │
          └──────────────┘  └──────────────┘  └──────────────┘
                                    │
                                    │ RabbitMQ
                                    ▼
                           ┌─────────────────┐
                           │    RabbitMQ     │
                           │ activity.queue  │
                           └────────┬────────┘
                                    │
                                    ▼
                           ┌─────────────────┐
                           │    AI Service   │
                           │ Gemini AI / AI  │
                           │ Recommendation  │
                           └─────────────────┘


              ┌──────────────────────────────┐
              │       Eureka Server         │
              │      Service Discovery      │
              │        Port: 8761           │
              └──────────────────────────────┘

              ┌──────────────────────────────┐
              │          Keycloak            │
              │ Authentication & JWT         │
              │        Port: 8084            │
              └──────────────────────────────┘
