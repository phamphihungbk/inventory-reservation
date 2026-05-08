COMPOSE=docker compose

.PHONY: up down logs backend frontend db migrate clean

up:
	$(COMPOSE) up --build

down:
	$(COMPOSE) down

logs:
	$(COMPOSE) logs -f

backend:
	$(COMPOSE) up --build backend

frontend:
	$(COMPOSE) up --build frontend

db:
	$(COMPOSE) up -d postgres

migrate:
	$(COMPOSE) run --rm backend gradle bootRun --no-daemon --args='--spring.main.web-application-type=none'

clean:
	$(COMPOSE) down -v --remove-orphans
