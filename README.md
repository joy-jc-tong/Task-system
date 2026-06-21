# AI Requirement Assistant

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)
![Status](https://img.shields.io/badge/status-active%20development-brightgreen)

> An enterprise-style requirement management and knowledge intelligence platform — built to solve the information fragmentation problem that engineers and PMs face every day.

---

## Table of Contents

- [Overview](#overview)
- [Business Problem](#business-problem)
- [Solution](#solution)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Current Progress](#current-progress)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
- [Roadmap](#roadmap)
- [Future Enhancements](#future-enhancements)

---

## Overview

**AI Requirement Assistant** is a centralized platform for managing requirements, tracking workflow states, organizing organizational documents, and enabling AI-powered knowledge retrieval.

The system is designed around a real operational pain point: knowledge fragmentation across engineering teams. By combining a structured requirement management backend with a RAG (Retrieval-Augmented Generation) AI layer, it allows engineers and PMs to ask natural language questions and receive contextually grounded answers backed by real source documents — not hallucinations.

This project is built with production-grade patterns: layered architecture, typed enums for workflow state machines, input validation at the boundary, read-only transaction optimization, and a planned evolution toward multi-service AI infrastructure.

---

## Business Problem

In most organizations, the following assets live in disconnected systems:

| Asset | Typical Location |
|---|---|
| Requirement documents | Confluence, Notion, email threads |
| Design documents | Google Drive, SharePoint, local disks |
| SOPs and processes | Internal wikis, PDFs, printed binders |
| Meeting notes | Slack, Zoom recordings, notebooks |
| Historical requirements | Jira, Linear, spreadsheets |

The cost of this fragmentation is real:

- Engineers spend hours searching for prior context before starting a feature
- PMs write requirements that already exist in a slightly different form
- New team members cannot quickly understand the rationale behind past decisions
- Institutional knowledge leaves the organization when people do

No existing tool combines structured requirement lifecycle management with semantic knowledge retrieval in a developer-friendly, self-hostable form that an engineering team can own end-to-end.

---

## Solution

AI Requirement Assistant addresses this problem in two layers:

**Layer 1 — Structured Requirement Management**

A REST API backend that manages the full lifecycle of a requirement: from `DRAFT` through `REVIEW`, and into `APPROVED` or `REJECTED`. Every requirement carries a typed priority level (`LOW` / `MEDIUM` / `HIGH` / `URGENT`), automatic lifecycle timestamps, and status transitions enforced at the service layer. The data model is designed for auditability and workflow correctness from the start.

**Layer 2 — AI Knowledge Intelligence** *(planned — Phase 5)*

A FastAPI sidecar service that ingests uploaded documents into a pgvector embedding store and exposes three capabilities:

- **Semantic search** — find the most relevant document chunks for any natural language query
- **RAG-based Q&A** — ask a question, receive an answer grounded in your organization's own documents with citations
- **Duplicate detection** — surface similar historical requirements automatically before a new one is submitted

---

## Architecture

### Current Architecture (Phase 1)

```
┌──────────────────────────────────────────────────────────┐
│                   Client / REST Consumer                  │
└──────────────────────────┬───────────────────────────────┘
                           │  HTTP
┌──────────────────────────▼───────────────────────────────┐
│            Spring Boot Application  (:8080)              │
│                                                           │
│  ┌────────────────────┐   ┌────────────────────────────┐ │
│  │  RequirementController│  │     RequirementService     │ │
│  │  /api/requirements  │──▶│  (Business Logic,          │ │
│  └────────────────────┘   │   @Transactional)          │ │
│                            └──────────────┬─────────────┘ │
│                                           │               │
│                            ┌──────────────▼─────────────┐ │
│                            │   RequirementRepository     │ │
│                            │   (Spring Data JPA)         │ │
│                            └──────────────┬─────────────┘ │
└───────────────────────────────────────────┼───────────────┘
                                            │
                             ┌──────────────▼─────────────┐
                             │         PostgreSQL           │
                             │    (requirements table)      │
                             └─────────────────────────────┘
```

### Target Architecture (Phase 5+)

```
┌───────────────────────────────────────────────────────────────┐
│                      Client / Frontend                         │
└───────────────────────────────┬───────────────────────────────┘
                                │  HTTP
┌───────────────────────────────▼───────────────────────────────┐
│              Spring Boot API  (:8080)                          │
│                                                                │
│  Controller → Service → Repository                             │
│  + Spring Security (JWT / RBAC)                                │
│  + Audit Log (AOP / JPA EntityListeners)                       │
└──────────────┬─────────────────────────────┬──────────────────┘
               │                             │
┌──────────────▼──────────────┐  ┌───────────▼───────────────────┐
│        PostgreSQL            │  │    FastAPI AI Service (:8001)  │
│  (requirements, users,       │  │                                │
│   audit_logs, documents)     │  │  POST /embed  — ingest docs    │
└─────────────────────────────┘  │  GET  /search — semantic query  │
                                  │  POST /ask    — RAG Q&A         │
                                  └───────────────┬───────────────┘
                                                  │
                                  ┌───────────────▼───────────────┐
                                  │    PostgreSQL + pgvector        │
                                  │    (document chunk embeddings)  │
                                  └───────────────┬───────────────┘
                                                  │
                                  ┌───────────────▼───────────────┐
                                  │          OpenAI API             │
                                  │  text-embedding-3-small         │
                                  │  gpt-4o                         │
                                  └───────────────────────────────┘
```

---

## Technology Stack

### Current

| Layer | Technology | Notes |
|---|---|---|
| Language | Java 17 | LTS, records, sealed classes available |
| Framework | Spring Boot 3.2 | Auto-configuration, embedded Tomcat |
| ORM | Spring Data JPA + Hibernate | Entity mapping, derived queries |
| Database | PostgreSQL | Primary operational store |
| Validation | Jakarta Bean Validation | Constraint enforcement at the API boundary |
| Utilities | Lombok | Eliminates boilerplate builders and accessors |
| Build | Maven | Dependency management, lifecycle |
| Testing | JUnit 5 + Spring Boot Test | Integration tests against real PostgreSQL |

### Planned

| Layer | Technology | Purpose |
|---|---|---|
| Security | Spring Security + JWT | Stateless authentication, method-level authorization |
| AI Service | FastAPI (Python 3.11) | Owns the entire ML pipeline, separate from JVM |
| Embeddings | OpenAI `text-embedding-3-small` | Efficient, high-quality document embeddings |
| LLM | OpenAI `gpt-4o` | RAG answer generation with citation prompting |
| Vector Store | pgvector extension | Cosine similarity search inside existing PostgreSQL |
| Containers | Docker Compose | Multi-service local orchestration |
| File Storage | PostgreSQL / S3-compatible | Document binary storage |

---

## Current Progress

### Phase 1 — Core Requirement Management ✅ Complete

- [x] `Requirement` JPA entity with `@PrePersist` / `@PreUpdate` lifecycle hooks
- [x] `RequirementStatus` workflow enum: `DRAFT` → `REVIEW` → `APPROVED` / `REJECTED`
- [x] `RequirementPriority` typed enum: `LOW` / `MEDIUM` / `HIGH` / `URGENT`
- [x] Full CRUD REST API (`POST`, `GET`, `PUT`, `DELETE`)
- [x] Status transition endpoint (`PATCH /{id}/status`)
- [x] Filter by status endpoint (`GET /status/{status}`)
- [x] Input validation via Jakarta Bean Validation (`@NotBlank`, `@NotNull`)
- [x] Clean layered architecture: Controller → Service Interface → ServiceImpl → Repository
- [x] Read-only transaction optimization for all query methods
- [x] CORS configuration for cross-origin API consumers
- [x] Integration tests: create, list all, and status transition flows

---

## API Reference

**Base URL:** `http://localhost:8080/api`

### Requirement Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/requirements` | Create a new requirement | ✅ |
| `GET` | `/requirements` | List all requirements | ✅ |
| `GET` | `/requirements/{id}` | Get requirement by ID | ✅ |
| `PUT` | `/requirements/{id}` | Update title / description / priority | ✅ |
| `DELETE` | `/requirements/{id}` | Delete requirement | ✅ |
| `GET` | `/requirements/status/{status}` | Filter requirements by status | ✅ |
| `PATCH` | `/requirements/{id}/status` | Transition requirement status | ✅ |

### Request / Response Schemas

**POST `/requirements` — Request body**
```json
{
  "title": "Implement SSO with Azure AD",
  "description": "Support SAML 2.0 login for enterprise customers on the B2B plan",
  "priority": "HIGH"
}
```

**RequirementDTO — Response**
```json
{
  "id": 1,
  "title": "Implement SSO with Azure AD",
  "description": "Support SAML 2.0 login for enterprise customers on the B2B plan",
  "status": "DRAFT",
  "priority": "HIGH",
  "createdAt": "2026-06-21T10:30:00",
  "updatedAt": "2026-06-21T10:30:00"
}
```

**Valid `RequirementStatus` values:** `DRAFT` `REVIEW` `APPROVED` `REJECTED`

**Valid `RequirementPriority` values:** `LOW` `MEDIUM` `HIGH` `URGENT`

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Create the database

```sql
CREATE DATABASE requirementdb;
```

### 2. Configure the datasource

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/requirementdb
    username: postgres
    password: your_password
```

Hibernate will auto-create the `requirements` table on first startup (`ddl-auto: update`).

### 3. Run the application

```bash
mvn spring-boot:run
```

The API is now available at `http://localhost:8080/api/requirements`.

### 4. Run the tests

```bash
mvn test
```

---

## Roadmap

### Phase 1 — Core Requirement Management ✅ Complete

Layered Spring Boot REST API with typed status and priority enums, full CRUD, Jakarta validation, and integration tests backed by PostgreSQL.

---

### Phase 2 — Security & Access Control

- Spring Security with stateless JWT authentication
- Four roles: `ADMIN`, `PM`, `ENGINEER`, `VIEWER`
- Method-level authorization with `@PreAuthorize`
- User registration, login, and token refresh endpoints
- Password hashing with BCrypt

---

### Phase 3 — Audit Log

- `AuditLog` entity capturing actor, action, target entity, before/after values, and timestamp
- Non-invasive instrumentation via AOP or JPA `@EntityListeners`
- Query endpoint: full change history for any requirement
- Immutable records — audit logs are append-only by design

---

### Phase 4 — Document Management

- File upload endpoint accepting PDF, DOCX, and Markdown
- Document metadata (name, type, size, uploader, uploadedAt) stored in PostgreSQL
- Binary content stored in PostgreSQL BYTEA or an S3-compatible bucket
- Document-to-requirement association (many-to-many)
- Text extraction via Apache Tika for downstream embedding

---

### Phase 5 — AI Knowledge Base (RAG Pipeline)

- FastAPI sidecar service, independently deployable alongside the Spring Boot API
- **Ingestion pipeline:** extract text → chunk by paragraph → embed via `text-embedding-3-small` → store vectors in pgvector
- **Semantic search:** `GET /search?q=...` returns ranked document chunks with similarity scores
- **RAG Q&A:** `POST /ask` retrieves top-K chunks, constructs a citation-grounded prompt, and sends to `gpt-4o`
- Response format includes both the answer and the source documents it was derived from

---

### Phase 6 — Intelligent Requirement Features

- **Duplicate detection:** on `POST /requirements`, automatically surface semantically similar existing requirements before saving
- **Requirement quality scoring:** LLM-based completeness and ambiguity analysis with actionable suggestions
- **AI-assisted draft:** generate a full requirement from a one-line brief

---

## Future Enhancements

| Enhancement | Motivation |
|---|---|
| Docker Compose for full stack | One command brings up PostgreSQL, pgvector, Spring Boot, and FastAPI |
| CI/CD via GitHub Actions | Automated build, test, and Docker image publishing on every push |
| Swagger / OpenAPI docs | Interactive API explorer, auto-generated from Spring annotations |
| Event-driven audit via Kafka | Decouples audit writes from the request path; enables async pipelines |
| Approval workflow with notifications | Closes the loop on the REVIEW → APPROVED transition with email or webhook alerts |
| Frontend (React / Next.js) | End-to-end demo surface for portfolio presentation |
| Kubernetes manifests | Demonstrates production deployment beyond local Docker Compose |
| Observability stack (Prometheus + Grafana) | Latency, throughput, and error rate dashboards for the API |
| Multi-tenant isolation | Row-level security so multiple organizations can share one deployment safely |

---

## Design Decisions

**Why a separate FastAPI service for AI, not just a Java library?**

The Python ML ecosystem (LangChain, sentence-transformers, OpenAI SDK) is significantly more mature than the JVM equivalent. Keeping the AI pipeline in Python means access to the best tooling, while the Spring Boot backend stays focused on what Java does well: transactional business logic, security, and structured data management. The boundary between them is a clean HTTP API.

**Why typed enums for status and priority instead of integers or free strings?**

Integer codes require a lookup table in every reader's head. Free strings break at the first typo. Typed enums make the compiler a collaborator — invalid states cannot be constructed, the API rejects unrecognized values automatically, and the database stores human-readable strings that survive schema migrations without a mapping table.

**Why read-only transactions on query methods?**

`@Transactional(readOnly = true)` gives the persistence provider a hint to skip dirty-checking, allows the database to route queries to read replicas, and prevents accidental writes in methods that should only read. It's a cheap annotation with measurable benefits at scale.

---

## License

MIT License — free to use, modify, and distribute.
