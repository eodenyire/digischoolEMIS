#!/usr/bin/env bash
set -euo pipefail

./scripts/validate-openapi.sh
./scripts/validate-event-schemas.sh

echo "All contract validations passed."
