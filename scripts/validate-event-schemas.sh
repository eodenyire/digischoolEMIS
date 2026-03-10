#!/usr/bin/env bash
set -euo pipefail

# Validate each schema compiles under JSON Schema draft 2020-12.
for schema in events/schemas/*.json; do
  npx --yes ajv-cli compile --spec=draft2020 --strict=false --validate-formats=false -s "$schema" >/dev/null
  echo "Validated schema: $schema"
done

echo "All event schemas validated."
