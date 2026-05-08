# Inventory Reservation

Clean monorepo for a backend-focused inventory reservation. It contains one Kotlin Spring Boot API, one Vue 3 frontend, and one root Docker Compose file for local development.

## Backend Engineering Highlights

This project is designed to show practical backend engineering, not just CRUD.

- **Transactions**: reservation creation, cancellation, and expiration run inside service-layer `@Transactional` boundaries.
- **Consistency**: stock changes and reservation state changes commit together, so inventory does not drift from reservation records.
- **Concurrency awareness**: concurrent reservation requests are handled through JPA version checks instead of unsafe read-modify-write assumptions.
- **Inventory logic**: creating a reservation reduces stock; canceling or expiring an active reservation restores stock.
- **Optimistic locking**: `Product` uses `@Version`, so stale concurrent updates fail with `409 Conflict` instead of overselling.
- **Scheduling**: expired active reservations are processed by a scheduled Spring job that releases stock automatically.

Core flow:

```text
Create reservation
-> load product in transaction
-> validate available stock
-> decrement stock
-> create ACTIVE reservation with expiration time
-> commit product + reservation together
```

Concurrent safety example:

```text
Request A reads product stock=10 version=0
Request B reads product stock=10 version=0

A reserves 8 -> writes stock=2 version=1 -> success
B reserves 8 -> tries stale version=0 write -> optimistic lock failure -> 409 Conflict
```

## Monorepo Structure

```text
inventory-reservation-system/
├── backend/
│   ├── src/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── Dockerfile
│   └── .env.example
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   ├── Dockerfile
│   └── .env.example
├── docker-compose.yml
├── Makefile
├── README.md
└── .gitignore
```

## Applications

Backend:

- Kotlin
- Spring Boot 3
- PostgreSQL
- Flyway
- Gradle Kotlin DSL
- Runs on `http://localhost:8080`

Frontend:

- Vue 3
- TypeScript
- Vite
- Pinia
- TailwindCSS
- Runs on `http://localhost:5173`

PostgreSQL:

- Runs on `localhost:5432`
- Database: `inventory`
- User: `inventory`
- Password: `inventory`

## Local Development

Copy env examples when running outside Docker:

```bash
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

Start everything with Docker:

```bash
make up
```

Open:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api/products`

Stop services:

```bash
make down
```

## Makefile Usage

```bash
make up        # Build and start frontend, backend, postgres
make down      # Stop containers
make logs      # Follow all logs
make backend   # Start backend and postgres
make frontend  # Start frontend, backend, postgres dependency chain
make db        # Start postgres only
make migrate   # Run Spring Boot once in non-web mode so Flyway migrations apply
make clean     # Stop containers and remove volumes
```

All targets use `docker compose`.

## Docker Compose

Root `docker-compose.yml` defines:

- `postgres`: PostgreSQL database with persistent named volume
- `backend`: Spring Boot dev container with mounted `./backend`
- `frontend`: Vite dev container with mounted `./frontend`

Service order:

```text
frontend -> backend -> postgres
```

Hot reload:

- Backend source is mounted into `/workspace`
- Frontend source is mounted into `/app`
- Gradle and npm dependencies use Docker named volumes

## API Integration

Frontend uses:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

Backend uses:

```text
DB_HOST=postgres
DB_PORT=5432
DB_NAME=inventory
DB_USER=inventory
DB_PASSWORD=inventory
```

Main endpoints:

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `GET /api/reservations`
- `POST /api/reservations`
- `DELETE /api/reservations/{id}`

## Run Without Docker

Backend:

```bash
cd backend
gradle bootRun
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

## Troubleshooting

Port already in use:

```bash
lsof -i :8080
lsof -i :5173
```

Database connection fails:

- Run `make db`
- Check `docker compose logs postgres`
- Verify backend env vars match `docker-compose.yml`

Frontend cannot reach API:

- Confirm backend is running at `http://localhost:8080`
- Confirm `frontend/.env` has `VITE_API_BASE_URL=http://localhost:8080/api`
- Restart Vite after changing env values

Stale containers or volumes:

```bash
make clean
make up
```
