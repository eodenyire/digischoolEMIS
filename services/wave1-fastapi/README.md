# Wave 1 FastAPI Service Stubs

This service provides implementation stubs aligned to `openapi/wave1-services.openapi.yaml`.

## Run locally

```bash
cd services/wave1-fastapi
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

OpenAPI docs:
- `http://127.0.0.1:8000/docs`
- `http://127.0.0.1:8000/openapi.json`

## Run tests

```bash
cd services/wave1-fastapi
pytest -q
```

## Run migrations (from repo root)

```bash
./scripts/db-migrate.sh
```

## Scope

Implemented Wave 1 endpoint stubs:
- Auth
- Users
- Students
- Enrollments
- Academic (`subjects`, `class-streams`)
- Attendance
- Communications

These are stub handlers returning deterministic mock responses for rapid frontend and integration bootstrapping.
