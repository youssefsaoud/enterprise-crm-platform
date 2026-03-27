# Spring Boot Angular CRM

A Month 1 CRM foundation built as a modular monolith REST API with a separate Angular single-page application.

## Stack

- Java 17 target on Spring Boot 3.x
- Maven project structure
- PostgreSQL with Flyway migrations
- Angular 16 with TypeScript
- Angular Router and HttpClient

## Repository Layout

```text
backend/   Spring Boot API foundation
frontend/  Angular SPA foundation
docs/      Product, architecture, timeline, and release documentation
```

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- PostgreSQL 14 or newer
- Node.js 16.14 through 20.x for Angular 16 compatibility
- npm or pnpm

## Environment

Copy `.env.example` into your local shell or environment manager and provide local values only. Do not commit secrets.
