# Infrastructure Sizing Blueprint (10 Schools, 100 Schools, National Scale)

This document provides pragmatic infrastructure sizing guidance for a cloud-native, multi-tenant School Management System aligned to Kenya CBC.

Assumptions:
- Peak usage around attendance windows, fee deadlines, and report release periods.
- Mobile + web clients.
- API-first microservice architecture.
- PostgreSQL as primary OLTP store.

## 1. Capacity Assumptions by Scale

## 1.1 Tier A: 10 Schools (Pilot)

- Students: 12,000 to 25,000
- Staff users: 1,000 to 2,500
- Parent users: 18,000 to 40,000
- Peak concurrent users: 1,500 to 3,000
- API requests per second (RPS): 80 to 200 peak

## 1.2 Tier B: 100 Schools (Regional)

- Students: 120,000 to 300,000
- Staff users: 10,000 to 25,000
- Parent users: 180,000 to 500,000
- Peak concurrent users: 12,000 to 35,000
- API RPS: 800 to 2,500 peak

## 1.3 Tier C: National Rollout

- Students: 3M to 12M+
- Staff users: 200,000+
- Parent users: 4M+
- Peak concurrent users: 150,000 to 600,000
- API RPS: 8,000 to 60,000 peak

## 2. Baseline Cloud Architecture

- Kubernetes cluster(s) for microservices.
- Managed PostgreSQL for OLTP.
- Redis for caching and queue support.
- Message bus (Kafka or RabbitMQ).
- Object storage for files and portfolio evidence.
- API Gateway + WAF + CDN.
- Warehouse for analytics and BI.

## 3. Resource Sizing by Tier

## 3.1 Compute (Application Layer)

Tier A (10 schools):
- 12 to 20 microservice pods total.
- Pod size baseline: 0.5 to 1 vCPU, 512MB to 1.5GB RAM.
- Total app compute: 12 to 24 vCPU, 24 to 48GB RAM.

Tier B (100 schools):
- 60 to 120 microservice pods total.
- Pod size baseline: 1 to 2 vCPU, 1GB to 3GB RAM.
- Total app compute: 120 to 300 vCPU, 180 to 500GB RAM.

Tier C (national):
- 300 to 1200 service pods across multiple clusters/regions.
- Pod size baseline: 1 to 4 vCPU, 2GB to 8GB RAM.
- Total app compute: 1,500 to 6,000 vCPU, 3TB to 12TB RAM.

## 3.2 Database (PostgreSQL OLTP)

Tier A:
- Primary: 8 to 16 vCPU, 32 to 64GB RAM.
- Storage: 1TB SSD starting point.
- Read replicas: 1.

Tier B:
- Primary: 32 to 64 vCPU, 128 to 256GB RAM.
- Storage: 4TB to 12TB SSD.
- Read replicas: 2 to 4.
- Consider sharding by tenant group if hot spots emerge.

Tier C:
- Multiple Postgres clusters by domain and geography.
- Per primary: 64 to 128 vCPU, 256 to 1024GB RAM.
- Storage: 30TB+ aggregate OLTP footprint.
- Read replicas: 6+ total, with workload segregation.

## 3.3 Messaging and Events

Tier A:
- 3-node small Kafka/Rabbit cluster.
- Throughput target: 1,000 to 5,000 events/sec burst.

Tier B:
- 5 to 7 broker nodes.
- Throughput target: 10,000 to 50,000 events/sec burst.

Tier C:
- Multi-cluster event backbone with replication.
- Throughput target: 100,000+ events/sec burst.

## 3.4 Storage

Tier A:
- Object storage: 2TB to 10TB.

Tier B:
- Object storage: 20TB to 150TB.

Tier C:
- Object storage: petabyte roadmap required (portfolio media + documents).

## 3.5 Analytics and BI

Tier A:
- Nightly ETL + daily dashboards.
- Small warehouse footprint (0.5TB to 2TB).

Tier B:
- Near real-time ingestion for risk alerts.
- Warehouse footprint (5TB to 30TB).

Tier C:
- Streaming analytics + feature store + ML pipeline.
- Warehouse/lake footprint 100TB+.

## 4. Availability, DR, and SRE Targets

Core targets:
- Availability: 99.9% (Tier A/B), 99.95% (Tier C core services).
- p95 API latency:
  - Reads < 300ms
  - Writes < 500ms
- RPO <= 15 minutes
- RTO <= 60 minutes

DR patterns:
- Tier A: single region with cross-zone HA + backups.
- Tier B: warm standby region for critical services.
- Tier C: active-active or active-warm across at least two regions.

## 5. Security and Compliance Baseline

- Central IAM with RBAC and tenant scoping.
- MFA for admin, finance, and superuser roles.
- Encryption at rest and in transit.
- PII minimization and masking in logs.
- Immutable audit logging for grades, attendance overrides, and billing adjustments.

## 6. Cost-Control Strategies

- Autoscale stateless services by queue depth/RPS.
- Use read replicas and caching before DB vertical scaling.
- Archive cold records (old audit/attendance) to cheaper storage.
- Tiered storage lifecycle policies for media artifacts.

## 7. Scaling Triggers (When to Upgrade)

Move Tier A -> B when:
- sustained p95 > target for 2+ weeks,
- DB CPU > 70% during school hours,
- queue lag > 60 seconds at peak.

Move Tier B -> C architecture when:
- total tenants > 250 schools,
- concurrent users > 50,000 regularly,
- single-cluster blast radius becomes unacceptable.

## 8. Rollout Recommendation

- Phase 1: Launch Tier A architecture for 10-school pilot.
- Phase 2: Add event backbone hardening and read model optimizations.
- Phase 3: Transition to multi-cluster regional topology before national onboarding.

## 9. Practical Tooling Stack

- Kubernetes (managed), Helm, ArgoCD.
- PostgreSQL + PgBouncer.
- Redis, Kafka.
- Prometheus, Grafana, Loki, Tempo/OpenTelemetry.
- Object storage (S3-compatible).
- Warehouse (BigQuery/Snowflake/ClickHouse/Postgres analytics).
