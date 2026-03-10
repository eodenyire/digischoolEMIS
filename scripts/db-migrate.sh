#!/usr/bin/env bash
set -euo pipefail

# Uses DATABASE_URL if provided, otherwise falls back to alembic.ini.
alembic -c database/alembic.ini upgrade head

echo "Database migration completed."
