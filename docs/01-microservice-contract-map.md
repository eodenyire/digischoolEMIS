# World-Class SMS: Microservice Contract Map

This document defines service boundaries, core APIs, event contracts, and integration patterns for a Kenya CBC-ready School Management System (Grade 1-13).

## 1. Platform Principles

- Domain-driven boundaries (each service owns its data).
- API-first with versioning (`/api/v1`).
- Event-driven integration for cross-domain updates.
- Tenant-aware contracts (`tenant_id` required in all write requests).
- Immutable reporting snapshots for regulatory and historical correctness.

## 2. Global API Conventions

### 2.1 Headers

- `Authorization: Bearer <token>`
- `X-Tenant-Id: <uuid>`
- `X-Request-Id: <uuid>`
- `Idempotency-Key: <uuid>` (required for payment and assessment writes)

### 2.2 Response envelope

```json
{
  "data": {},
  "meta": {
    "request_id": "uuid",
    "version": "v1"
  },
  "errors": []
}
```

### 2.3 Pagination

- Query params: `page`, `page_size`, `sort`, `filter[field]`.
- Cursor pagination for high-volume streams (`attendance`, `events`, `audit_logs`).

## 3. Service Catalog

## 3.1 Identity and Access Service (IAM)

Responsibilities:
- Users, roles, permission policies, MFA, sessions.

Core APIs:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/users`
- `GET /api/v1/users/{user_id}`
- `POST /api/v1/roles`
- `POST /api/v1/roles/{role_id}/permissions`

Published events:
- `iam.user.created.v1`
- `iam.user.role_assigned.v1`
- `iam.user.deactivated.v1`

Consumed events:
- `sis.staff.created.v1` (auto-provision staff accounts)

## 3.2 SIS Service

Responsibilities:
- Admissions, student profiles, guardians, enrollment lifecycle, promotions.

Core APIs:
- `POST /api/v1/students`
- `GET /api/v1/students/{student_id}`
- `PATCH /api/v1/students/{student_id}`
- `POST /api/v1/students/{student_id}/guardians`
- `POST /api/v1/enrollments`
- `POST /api/v1/promotions`

Published events:
- `sis.student.created.v1`
- `sis.student.enrolled.v1`
- `sis.student.promoted.v1`
- `sis.student.transferred.v1`

Consumed events:
- `finance.invoice.paid.v1` (fee-clearance gating rules)

## 3.3 Academic Management Service

Responsibilities:
- Grade levels, class streams, subjects, teacher allocation, lesson plans.

Core APIs:
- `POST /api/v1/academic-years`
- `POST /api/v1/terms`
- `POST /api/v1/subjects`
- `POST /api/v1/class-streams`
- `POST /api/v1/teaching-allocations`
- `POST /api/v1/lesson-plans`

Published events:
- `academic.subject.created.v1`
- `academic.class.assigned.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.4 CBC Competency Service

Responsibilities:
- CBC curriculum structures (strand, sub-strand, learning outcomes, competencies, rubrics).

Core APIs:
- `POST /api/v1/cbc/learning-areas`
- `POST /api/v1/cbc/strands`
- `POST /api/v1/cbc/sub-strands`
- `POST /api/v1/cbc/outcomes`
- `POST /api/v1/cbc/competencies`
- `POST /api/v1/cbc/rubrics`

Published events:
- `cbc.outcome.created.v1`
- `cbc.rubric.published.v1`

Consumed events:
- `academic.subject.created.v1`

## 3.5 Assessment Service

Responsibilities:
- Continuous assessments, rubrics, scores, teacher comments, report snapshots.

Core APIs:
- `POST /api/v1/assessments/plans`
- `POST /api/v1/assessments/activities`
- `POST /api/v1/assessments/submissions`
- `POST /api/v1/assessments/scores`
- `POST /api/v1/assessments/comments`
- `POST /api/v1/report-cards/publish`

Published events:
- `assessment.activity.created.v1`
- `assessment.score.recorded.v1`
- `assessment.comment.recorded.v1`
- `assessment.reportcard.published.v1`

Consumed events:
- `cbc.rubric.published.v1`
- `sis.student.enrolled.v1`

## 3.6 LMS Service

Responsibilities:
- Course materials, assignments, class posts, submissions, discussion boards.

Core APIs:
- `POST /api/v1/courses`
- `POST /api/v1/courses/{course_id}/materials`
- `POST /api/v1/courses/{course_id}/assignments`
- `POST /api/v1/assignments/{assignment_id}/submissions`

Published events:
- `lms.assignment.created.v1`
- `lms.submission.received.v1`

Consumed events:
- `academic.class.assigned.v1`

## 3.7 Attendance Service

Responsibilities:
- Student and teacher attendance, lateness, parent alerts.

Core APIs:
- `POST /api/v1/attendance/sessions`
- `POST /api/v1/attendance/marks`
- `GET /api/v1/attendance/students/{student_id}`
- `POST /api/v1/attendance/alerts/run`

Published events:
- `attendance.mark.recorded.v1`
- `attendance.student.absent.v1`
- `attendance.student.late.v1`

Consumed events:
- `academic.class.assigned.v1`

## 3.8 Timetable Service

Responsibilities:
- Class and teacher timetables, room allocation, exam schedules.

Core APIs:
- `POST /api/v1/timetables`
- `POST /api/v1/timetables/{timetable_id}/slots`
- `POST /api/v1/exam-timetables`

Published events:
- `timetable.published.v1`
- `timetable.slot.changed.v1`

Consumed events:
- `academic.class.assigned.v1`

## 3.9 Finance Service

Responsibilities:
- Fee structures, invoices, receipts, waivers, balances, reconciliation.

Core APIs:
- `POST /api/v1/finance/fee-structures`
- `POST /api/v1/finance/invoices`
- `POST /api/v1/finance/payments`
- `POST /api/v1/finance/waivers`
- `GET /api/v1/finance/ledger/students/{student_id}`

Published events:
- `finance.invoice.issued.v1`
- `finance.payment.received.v1`
- `finance.invoice.paid.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.10 Teacher and HR Service

Responsibilities:
- Teacher records, qualifications, workload, appraisals, payroll integration.

Core APIs:
- `POST /api/v1/teachers`
- `POST /api/v1/teachers/{teacher_id}/qualifications`
- `GET /api/v1/teachers/{teacher_id}/workload`

Published events:
- `teacher.created.v1`
- `teacher.workload.updated.v1`

Consumed events:
- `academic.class.assigned.v1`

## 3.11 Parent Portal Service

Responsibilities:
- Parent-specific view aggregation for grades, attendance, finance, notices.

Core APIs:
- `GET /api/v1/parent/students`
- `GET /api/v1/parent/students/{student_id}/dashboard`
- `GET /api/v1/parent/students/{student_id}/report-cards`

Published events:
- `parent.dashboard.viewed.v1` (analytics)

Consumed events:
- `assessment.reportcard.published.v1`
- `attendance.student.absent.v1`
- `finance.invoice.issued.v1`

## 3.12 Communication Service

Responsibilities:
- Notifications and campaigns via SMS, email, push.

Core APIs:
- `POST /api/v1/communications/messages`
- `POST /api/v1/communications/campaigns`
- `GET /api/v1/communications/delivery-status/{message_id}`

Published events:
- `communication.message.sent.v1`
- `communication.delivery.failed.v1`

Consumed events:
- `attendance.student.absent.v1`
- `assessment.reportcard.published.v1`
- `finance.invoice.issued.v1`

## 3.13 Document Service

Responsibilities:
- Document templates, generated report cards, certificates, transcripts.

Core APIs:
- `POST /api/v1/documents/templates`
- `POST /api/v1/documents/generate`
- `GET /api/v1/documents/{document_id}`

Published events:
- `document.generated.v1`
- `document.signed.v1`

Consumed events:
- `assessment.reportcard.published.v1`

## 3.14 Discipline Service

Responsibilities:
- Incidents, actions, follow-up, behavior analytics.

Core APIs:
- `POST /api/v1/discipline/incidents`
- `POST /api/v1/discipline/actions`
- `GET /api/v1/discipline/students/{student_id}`

Published events:
- `discipline.incident.reported.v1`
- `discipline.action.applied.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.15 Health Service

Responsibilities:
- Allergies, medical conditions, clinic visits, immunization records.

Core APIs:
- `POST /api/v1/health/students/{student_id}/records`
- `POST /api/v1/health/clinic-visits`
- `GET /api/v1/health/students/{student_id}`

Published events:
- `health.clinic_visit.logged.v1`

Consumed events:
- `sis.student.created.v1`

## 3.16 Library Service

Responsibilities:
- Catalog, lending, returns, fines.

Core APIs:
- `POST /api/v1/library/books`
- `POST /api/v1/library/loans`
- `POST /api/v1/library/returns`

Published events:
- `library.book.issued.v1`
- `library.book.overdue.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.17 Transport Service

Responsibilities:
- Route setup, student assignments, trip logs.

Core APIs:
- `POST /api/v1/transport/routes`
- `POST /api/v1/transport/assignments`
- `POST /api/v1/transport/trips`

Published events:
- `transport.route.published.v1`
- `transport.student.assigned.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.18 Boarding Service

Responsibilities:
- Dorm assignment, check-in/out, meal plans.

Core APIs:
- `POST /api/v1/boarding/dormitories`
- `POST /api/v1/boarding/allocations`
- `POST /api/v1/boarding/checkins`

Published events:
- `boarding.student.allocated.v1`

Consumed events:
- `sis.student.enrolled.v1`

## 3.19 Analytics Service

Responsibilities:
- Read model materialization, predictive risk scoring, dashboards.

Core APIs:
- `GET /api/v1/analytics/schools/{school_id}/overview`
- `GET /api/v1/analytics/students/{student_id}/risk`
- `POST /api/v1/analytics/models/retrain`

Published events:
- `analytics.risk.raised.v1`
- `analytics.kpi.snapshot.created.v1`

Consumed events:
- All core domain events (stream consumption).

## 3.20 Administration and Audit Service

Responsibilities:
- Tenancy, audit logs, system config, feature flags, release controls.

Core APIs:
- `POST /api/v1/admin/tenants`
- `GET /api/v1/admin/audit-logs`
- `POST /api/v1/admin/feature-flags`

Published events:
- `admin.feature_flag.changed.v1`

Consumed events:
- Writes all auditable events into immutable logs.

## 4. Canonical Event Schema

```json
{
  "event_id": "uuid",
  "event_type": "assessment.score.recorded.v1",
  "occurred_at": "2026-03-10T08:30:00Z",
  "tenant_id": "uuid",
  "school_id": "uuid",
  "actor": {
    "user_id": "uuid",
    "role": "teacher"
  },
  "entity": {
    "type": "assessment_score",
    "id": "uuid"
  },
  "payload": {},
  "trace": {
    "request_id": "uuid",
    "correlation_id": "uuid"
  }
}
```

## 5. Integration Contracts

External integrations:
- NEMIS and ministry reporting connectors.
- M-Pesa, bank, and card payment gateways.
- SMS providers and email providers.
- Optional LMS interoperability (LTI/xAPI where needed).

Integration patterns:
- Outbound webhooks for key lifecycle events.
- Scheduled pull for legacy systems.
- Dead-letter queues for failed event processing.

## 6. Non-Functional Contract Requirements

- p95 API latency targets:
  - Read APIs: < 300 ms
  - Write APIs: < 500 ms
- Availability:
  - Core SIS/Attendance/Assessment: 99.9%
- Recovery targets:
  - RPO: <= 15 minutes
  - RTO: <= 60 minutes
- Security:
  - MFA for privileged roles
  - Row-level tenant isolation
  - Full auditability for grade and finance changes

## 7. Delivery Sequence

- Wave 1: IAM, SIS, Academic, Attendance, Communication.
- Wave 2: CBC Competency, Assessment, Parent Portal, Documents.
- Wave 3: Finance, Timetable, Teacher, Analytics.
- Wave 4: Library, Transport, Boarding, Alumni, advanced AI.
