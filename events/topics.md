# Event Topic Definitions

This catalog defines message topics and payload schema references for asynchronous integration.

## Conventions

- Topic format: `digischool.<domain>.<entity>.<action>.v1`
- Key: entity UUID string
- Value: JSON matching schema in `events/schemas`
- Delivery: at-least-once
- Compatibility: backward compatible changes only within major version

## Topics

1. `digischool.iam.user.created.v1`
- Producer: IAM Service
- Consumers: Administration, Audit, Analytics
- Schema: `events/schemas/iam.user.created.v1.json`

2. `digischool.sis.student.created.v1`
- Producer: SIS Service
- Consumers: Health, Finance, Analytics, Communication
- Schema: `events/schemas/sis.student.created.v1.json`

3. `digischool.sis.student.enrolled.v1`
- Producer: SIS Service
- Consumers: Attendance, Academic, Finance, Parent Portal
- Schema: `events/schemas/sis.student.enrolled.v1.json`

4. `digischool.academic.subject.created.v1`
- Producer: Academic Service
- Consumers: CBC Competency, Timetable, Analytics
- Schema: `events/schemas/academic.subject.created.v1.json`

5. `digischool.attendance.mark.recorded.v1`
- Producer: Attendance Service
- Consumers: Parent Portal, Communication, Analytics
- Schema: `events/schemas/attendance.mark.recorded.v1.json`

6. `digischool.assessment.score.recorded.v1`
- Producer: Assessment Service
- Consumers: Analytics, Parent Portal, Reporting
- Schema: `events/schemas/assessment.score.recorded.v1.json`

7. `digischool.assessment.reportcard.published.v1`
- Producer: Assessment Service
- Consumers: Document Service, Communication, Parent Portal
- Schema: `events/schemas/assessment.reportcard.published.v1.json`

8. `digischool.finance.invoice.issued.v1`
- Producer: Finance Service
- Consumers: Parent Portal, Communication, Analytics
- Schema: `events/schemas/finance.invoice.issued.v1.json`

9. `digischool.finance.payment.received.v1`
- Producer: Finance Service
- Consumers: SIS, Parent Portal, Analytics
- Schema: `events/schemas/finance.payment.received.v1.json`

10. `digischool.communication.message.sent.v1`
- Producer: Communication Service
- Consumers: Audit, Analytics
- Schema: `events/schemas/communication.message.sent.v1.json`

## Envelope Requirements (All events)

Each event payload includes:
- `event_id` (uuid)
- `event_type` (string)
- `occurred_at` (date-time)
- `tenant_id` (uuid)
- `school_id` (uuid)
- `actor` object
- `trace` object
- domain-specific payload fields
