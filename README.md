# DigiSchool EMIS

**World-Class School Management System for Kenya CBC (Grade 1 to Grade 13)**

DigiSchool EMIS is a comprehensive, production-ready School Management System designed to fully support the **Kenya Competency-Based Curriculum (CBC)** from Grade 1 through Grade 13.

## Overview

DigiSchool EMIS is modeled after global platforms like **PowerSchool**, **Infinite Campus**, and **Skyward** and provides a complete education ERP ecosystem.

## Core Platform Documentation

- [Microservice Contract Map](docs/01-microservice-contract-map.md)
- [CBC Logical Data Model (100 Tables)](docs/02-cbc-logical-data-model.md)
- [Infrastructure Sizing Blueprint](docs/03-infrastructure-sizing.md)

## Implementation Assets

- [Initial PostgreSQL Migration (100-table baseline)](database/migrations/001_initial_cbc_schema.sql)
- [Staged Migration Waves](database/migrations/waves/README.md)
- [Wave 1 OpenAPI Specification](openapi/wave1-services.openapi.yaml)
- [Event Topics Catalog](events/topics.md)
- [Event JSON Schemas](events/schemas)
- [Wave 1 FastAPI Service Stubs](services/wave1-fastapi/README.md)

## Runtime and Tooling

- [Docker Compose Stack](docker-compose.yml)
- [Alembic Configuration](database/alembic.ini)
- [Wave Baseline Alembic Revision](database/alembic/versions/20260310_0001_apply_wave_migrations.py)
- [Database Migration Script](scripts/db-migrate.sh)
- [Contract Validation Workflow](.github/workflows/contract-validation.yml)

## Validation

```bash
./scripts/validate-contracts.sh
```

## Local Startup

```bash
docker compose up --build
```
