
"""Apply wave migration SQL files

Revision ID: 20260310_0001
Revises: 
Create Date: 2026-03-10 10:00:00
"""

from __future__ import annotations

from pathlib import Path

from alembic import op

revision = "20260310_0001"
down_revision = None
branch_labels = None
depends_on = None

WAVE_FILES = [
    "001_wave1_core.sql",
    "002_wave2_cbc_assessment.sql",
    "003_wave3_operations.sql",
    "004_wave4_welfare_extensions.sql",
]


def _sql_path(file_name: str) -> Path:
    base = Path(__file__).resolve().parents[3]
    return base / "migrations" / "waves" / file_name


def _run_sql_file(file_name: str) -> None:
    content = _sql_path(file_name).read_text(encoding="utf-8")
    lines = []
    for line in content.splitlines():
        normalized = line.strip().rstrip(";").upper()
        if normalized in {"BEGIN", "COMMIT"}:
            continue
        lines.append(line)
    op.execute("\n".join(lines))


def upgrade() -> None:
    for file_name in WAVE_FILES:
        _run_sql_file(file_name)


def downgrade() -> None:
    raise NotImplementedError(
        "Downgrade is intentionally not supported for wave-based baseline migrations."
    )
