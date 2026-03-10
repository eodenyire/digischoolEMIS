#!/usr/bin/env bash
set -euo pipefail

npx --yes @apidevtools/swagger-cli validate openapi/wave1-services.openapi.yaml

echo "OpenAPI validation passed."
