# MoneyFlow: A Microservices-Based Financial Ecosystem

MoneyFlow is a modern, secure, and scalable microservices-based financial platform designed to facilitate instant account-to-account money transfers.

The system follows a distributed architecture with isolated services responsible for authentication, user management, and financial transfers. This ensures security, maintainability, and horizontal scalability.

---

## Live Demo

https://moneyflow-frontend-o59b.onrender.com/login

---

## Sample Credentials

Use the following accounts to test login and transfer functionality.

| User | Email | Password |
|-----|------|------|
| Alice | Alice@moneyflow.com | Password123* |
| Bob | Bob@moneyflow.com | Password123* |

Example test flow:
1. Login as Alice
2. Transfer funds to Bob
3. Login as Bob to verify the updated balance

---

## Technologies Used

### Frontend
- Angular 17
- RxJS
- SCSS
- Responsive UI

### Backend
- Java 17
- Spring Boot 3
- Spring Security
- Spring Cloud OpenFeign
- RESTful APIs

### Database
- PostgreSQL (Supabase Cloud)

### Security
- JWT Authentication (Stateless)
- BCrypt Password Hashing
- Internal Service Secret Validation

### DevOps / Deployment
- Docker
- Docker Hub
- Render Cloud Platform

---

## Architecture Overview

The application consists of four main components into a monorepo structure:

- **auth-service** - Handles authentication, login attempt protection, JWT generation, and token validation.
- **user-service** - Manages user profiles, account balances, and information retrieval.
- **transfer-service** - Handles secure funds transfer, transaction validation, and history.
- **frontend** - Angular web client for user interactions and feedback.

---

## Option 1: Run via Docker (Recommended)

### 1. Pull the Images

```bash
docker pull darweeshy/moneyflow-frontend:latest
docker pull darweeshy/user-service:latest
docker pull darweeshy/auth-service:latest
docker pull darweeshy/transfer-service:latest
```

### 2. Run the Containers

#### User Service
```bash
docker run -d --name user-service \
-p 8081:8081 \
-e DB_PASSWORD="HD*h4r/zmpnPz-t" \
-e DB_URL="jdbc:postgresql://aws-1-eu-central-1.pooler.supabase.com:5432/postgres?sslmode=require" \
-e DB_USERNAME="postgres.qwemlolbjjeqgmzvhaee" \
-e INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7" \
-e JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba" \
darweeshy/user-service:latest
```

#### Auth Service
```bash
docker run -d --name auth-service \
-p 8082:8082 \
-e PORT=8082 \
-e AUTH_MAX_FAILED_ATTEMPTS=5 \
-e DB_PASSWORD="HD*h4r/zmpnPz-t" \
-e DB_URL="jdbc:postgresql://aws-1-eu-central-1.pooler.supabase.com:5432/postgres?sslmode=require" \
-e DB_USERNAME="postgres.qwemlolbjjeqgmzvhaee" \
-e INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7" \
-e JWT_ACCESS_EXPIRY=900000 \
-e JWT_REFRESH_EXPIRY=604800000 \
-e JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba" \
-e USER_SERVICE_URL="http://localhost:8081" \
darweeshy/auth-service:latest
```

#### Transfer Service
```bash
docker run -d --name transfer-service \
-p 8083:8083 \
-e DB_PASSWORD="HD*h4r/zmpnPz-t" \
-e DB_URL="jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require" \
-e DB_USERNAME="postgres.qassknbiieojlwadswzy" \
-e INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7" \
-e JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba" \
-e USER_SERVICE_URL="http://localhost:8081" \
darweeshy/transfer-service:latest
```

#### Frontend
```bash
docker run -d --name moneyflow-frontend \
-p 80:80 \
darweeshy/moneyflow-frontend:latest
```

---

## Option 2: Build and Run Locally

### 1. Prerequisites
- Java 17+
- Node.js 18+
- Maven (included as ./mvnw)

### 2. Environment Setup

Run the following commands from the project root to create the necessary .env files for local development:

#### Set up User Service
```bash
cat <<EOF > user-service/.env
DB_PASSWORD="HD*h4r/zmpnPz-t"
DB_URL="jdbc:postgresql://aws-1-eu-central-1.pooler.supabase.com:5432/postgres?sslmode=require"
DB_USERNAME="postgres.qwemlolbjjeqgmzvhaee"
INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7"
JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba"
EOF
```

#### Set up Auth Service
```bash
cat <<EOF > auth-service/.env
PORT=8082
AUTH_MAX_FAILED_ATTEMPTS=5
DB_PASSWORD="HD*h4r/zmpnPz-t"
DB_URL="jdbc:postgresql://aws-1-eu-central-1.pooler.supabase.com:5432/postgres?sslmode=require"
DB_USERNAME="postgres.qwemlolbjjeqgmzvhaee"
INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7"
JWT_ACCESS_EXPIRY=900000
JWT_REFRESH_EXPIRY=604800000
JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba"
USER_SERVICE_URL="http://localhost:8081"
EOF
```

#### Set up Transfer Service
```bash
cat <<EOF > transfer-service/.env
DB_PASSWORD="HD*h4r/zmpnPz-t"
DB_URL="jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require"
DB_USERNAME="postgres.qassknbiieojlwadswzy"
INTERNAL_SECRET="b1682ff9a1254907c92a96bf17723ba7"
JWT_SECRET="aff1962e0fb1a399236d5b4b1385c57eb16e55df5e0020d6df8a70e1862bb81073aca9a9bef0f85d841c47d873fdfeb398b586e083098f5266fef786b50f0eba"
USER_SERVICE_URL="http://localhost:8081"
EOF
```

### 3. Running Backend
For each service folder:
```bash
./mvnw spring-boot:run
```

### 4. Running Frontend
In the frontend folder:
```bash
npm install
npm start
```

---

## API Endpoints

### Auth Service
- POST /auth/login - Authenticate and return tokens
- POST /auth/refresh - Refresh access token
- GET /auth/validate - Validate token
- POST /auth/logout - Invalidate session

### User Service
- GET /users/me - Current user profile
- GET /users/{userId} - Specific user info
- GET /users - List all users

### Transfer Service
- POST /transfers - Initiate money transfer
- GET /transfers - Transaction history
