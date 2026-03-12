# Kubernetes Manifests

This directory contains Kubernetes manifests for deploying the DigiSchool EMIS Wave 1 stack.

## Components

| File | Resource | Description |
|------|----------|-------------|
| `namespace.yaml` | Namespace | Dedicated `digischool` namespace |
| `postgres.yaml` | Secret, PVC, Deployment, Service | PostgreSQL 16 database |
| `wave1-api.yaml` | ConfigMap, Deployment, Service, Ingress | Wave 1 FastAPI service (2 replicas) |

## Quick Start (minikube)

```bash
# 1. Start minikube
minikube start

# 2. Build the Wave 1 API image inside minikube's Docker daemon
eval $(minikube docker-env)
docker build -t digischool-wave1-api:latest services/wave1-fastapi

# 3. Deploy all resources
make k8s-deploy

# 4. Check status
make k8s-status

# 5. Forward the API to localhost:8000
make k8s-port-forward

# 6. Test the health endpoint
curl http://localhost:8000/health
```

Or use Makefile shortcuts:

```bash
make k8s-build    # build image inside minikube
make k8s-deploy   # apply all manifests
make k8s-status   # kubectl get all -n digischool
make k8s-logs-api # tail wave1-api logs
make k8s-delete   # teardown
```

## Resource Summary

| Pod | CPU Request | CPU Limit | Memory Request | Memory Limit |
|-----|-------------|-----------|----------------|--------------|
| postgres | 250m | 1000m | 256Mi | 1Gi |
| wave1-api (×2) | 100m | 500m | 128Mi | 512Mi |

## Health Probes

Both deployments use HTTP/exec probes:

- **postgres**: `pg_isready` exec probe (readiness + liveness)
- **wave1-api**: `GET /health` HTTP probe (readiness + liveness)

## Ingress

The wave1-api is exposed via an NGINX Ingress at `api.digischool.local`.
Add the following to `/etc/hosts` after running `minikube tunnel`:

```
127.0.0.1   api.digischool.local
```

Then access the API at `http://api.digischool.local/health`.
