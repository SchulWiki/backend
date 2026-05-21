# Contributing Guidelines

This document defines the standards, architecture, and workflows for our Java Spring Boot application.

---

# 1. Architecture & Project Structure

We use a **Package-by-Feature** approach. Each business domain (e.g. `user`) is isolated in its own package.

## Layer responsibilities:

### Controller (/controller)
- Entry point for REST requests
- No business logic allowed
- Handles validation (@Valid)
- Calls service layer
- Returns HTTP responses

### Service (/service)
- Core business logic
- Must NOT contain HTTP logic

### Repository (/repository)
- Data access layer (Spring Data JPA)
- Interfaces only

### Entity (/entity)
- JPA entities
- Must NOT leave service layer
- Only DTOs go outside

### DTO (/dto)
- Data Transfer Objects
- Separate DTO per operation

### Mapper (/mapper)
- Entity ↔ DTO mapping
- Uses MapStruct

---

# 2. Code Style

- Code language: English only
- Naming:
    - Classes: PascalCase
    - Methods/variables: camelCase
    - Constants: UPPER_SNAKE_CASE

## Lombok:
- Use: @Getter, @Setter, @Builder, @RequiredArgsConstructor
- Avoid @Data in entities

---

# 3. REST API Design

- Use nouns:
    - GET /api/users
    - NOT /api/getUsers

## Status codes:
- GET → 200
- POST → 201
- PUT/PATCH → 200
- DELETE → 204

---

# 4. Error Handling

We use @ControllerAdvice for centralized error handling.

Services throw custom exceptions:
- NotFoundException
- BadRequestException
- UnauthorizedException
- ConflictException

GlobalExceptionHandler converts them into clean JSON responses without exposing stack traces.

---

# 5. Git Workflow

- master = production (no direct pushes)
- feature branches:
    - feat/auth
    - fix/token-bug

## Commits:
- feat:
- fix:
- refactor:

## PR:
- at least 1 approval required

---