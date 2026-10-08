# ⚡ ActivePulse AI - Smart Fitness Tracker & AI Coach

ActivePulse AI is a modern, responsive full-stack **Microservices** application that transforms standard fitness logs into intelligent, actionable health analytics. Instead of just storing raw data, this platform utilizes advanced AI engineering pipelines to deliver customized coaching plans, performance analysis, and safety precautions.

## 🤖 AI Engineering & Architecture Features
* **Structured AI Response Pipelines:** Integrates with the **Google Gemini API** using advanced prompt engineering to return deterministic, validated JSON structures.
* **Asynchronous Event-Driven AI Processing:** Uses **RabbitMQ** message queues to decouple the core activity tracking services from heavy AI processing tasks.
* **Microservices Orchestration:** Built with a distributed architecture managed by a **Netflix Eureka Discovery Server** for seamless service registry.
* **Dribbble-Inspired UI Framework:** Designed with responsive, modern **Material UI (MUI)** layout card configurations matching modern dashboard aesthetics.

## 🛠️ The Tech Stack
* **Frontend:** React.js, Material UI (MUI), Framer Motion, Redux (Auth State)
* **Backend:** Java, Spring Boot, Spring Cloud Eureka, Spring AMQP (RabbitMQ)
* **Databases & Infrastructure:** PostgreSQL / MongoDB, RabbitMQ Broker
* **Security:** Keycloak OAuth2 / OpenID Connect

## ⚙️ How the AI Pipeline Works
1. **Log Activity:** The user submits a workout (Running, Swimming, Cycling) via the React frontend.
2. **Event Dispatched:** The `ActivityService` saves the raw data and fires a message payload into the `activity.queue` on **RabbitMQ**.
3. **AI Processing:** The `Aiservice` consumes the message asynchronously, triggers a case-normalized context prompt builder, and streams it to **Gemini**.
4. **Structured Mapping:** The system sanitizes the raw markdown response text, converts it into an internal Java data tree using Jackson `ObjectMapper`, and saves the generated recommendation back to the database.
