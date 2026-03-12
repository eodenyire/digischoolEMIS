.PHONY: help local-api local-test docker-up docker-down docker-logs k8s-deploy k8s-delete k8s-status screenshots lint

PYTHON   := python3
UVICORN  := uvicorn
PYTEST   := python3 -m pytest
API_DIR  := services/wave1-fastapi
KUBECTL  := kubectl
K8S_DIR  := k8s

help: ## Show this help message
	@echo "DigiSchool EMIS — available targets"
	@echo ""
	@awk 'BEGIN {FS = ":.*##"} /^[a-zA-Z_-]+:.*##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

# ─── Local development ───────────────────────────────────────────────────────

local-install: ## Install Python dependencies
	pip install -r $(API_DIR)/requirements.txt

local-api: ## Start the Wave 1 FastAPI service locally (port 8000)
	cd $(API_DIR) && $(UVICORN) app.main:app --host 0.0.0.0 --port 8000 --reload

local-test: ## Run all unit / integration tests
	cd $(API_DIR) && $(PYTEST) tests/ -v

local-test-fast: ## Run tests (quiet mode)
	cd $(API_DIR) && $(PYTEST) tests/ -q

# ─── Docker ──────────────────────────────────────────────────────────────────

docker-build: ## Build Docker images
	docker compose build

docker-up: ## Start all services with Docker Compose (detached)
	docker compose up -d --build

docker-down: ## Stop and remove Docker Compose services
	docker compose down

docker-logs: ## Tail logs from all Docker Compose services
	docker compose logs -f

docker-ps: ## Show running Docker Compose containers
	docker compose ps

docker-test: ## Run tests against the Docker Compose stack
	docker compose run --rm wave1-api python -m pytest tests/ -v

# ─── Kubernetes ──────────────────────────────────────────────────────────────

k8s-build: ## Build and tag the image for Kubernetes (minikube)
	eval $$(minikube docker-env) && docker build -t digischool-wave1-api:latest $(API_DIR)

k8s-deploy: ## Deploy the full stack to Kubernetes (namespace: digischool)
	$(KUBECTL) apply -f $(K8S_DIR)/namespace.yaml
	$(KUBECTL) apply -f $(K8S_DIR)/postgres.yaml
	$(KUBECTL) apply -f $(K8S_DIR)/wave1-api.yaml
	@echo "Waiting for postgres to be ready..."
	$(KUBECTL) rollout status deployment/postgres -n digischool --timeout=120s
	@echo "Waiting for wave1-api to be ready..."
	$(KUBECTL) rollout status deployment/wave1-api -n digischool --timeout=120s

k8s-delete: ## Remove all DigiSchool resources from Kubernetes
	$(KUBECTL) delete namespace digischool --ignore-not-found

k8s-status: ## Show status of all pods in the digischool namespace
	$(KUBECTL) get all -n digischool

k8s-logs-api: ## Tail wave1-api pod logs
	$(KUBECTL) logs -n digischool -l app=wave1-api -f

k8s-logs-db: ## Tail postgres pod logs
	$(KUBECTL) logs -n digischool -l app=postgres -f

k8s-port-forward: ## Forward wave1-api service to localhost:8000
	$(KUBECTL) port-forward -n digischool svc/wave1-api 8000:8000

# ─── Database ────────────────────────────────────────────────────────────────

db-migrate: ## Run Alembic database migrations
	./scripts/db-migrate.sh

# ─── Quality assurance ───────────────────────────────────────────────────────

validate-contracts: ## Validate all API contracts and OpenAPI specs
	./scripts/validate-contracts.sh
	./scripts/validate-openapi.sh
	./scripts/validate-event-schemas.sh

screenshots: ## Capture screenshots of all API modules (requires running API)
	python3 scripts/capture_screenshots.py
