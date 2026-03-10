# Staged Migration Plan

This folder contains wave-based migration scripts that split the monolithic baseline migration into phased delivery units.

Execution order:
1. `001_wave1_core.sql`
2. `002_wave2_cbc_assessment.sql`
3. `003_wave3_operations.sql`
4. `004_wave4_welfare_extensions.sql`

Notes:
- The original `database/migrations/001_initial_cbc_schema.sql` is retained as a full reference baseline.
- Use either the monolithic migration or staged wave migrations for a new environment, not both.
