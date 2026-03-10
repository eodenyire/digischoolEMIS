# Kenya CBC Logical Data Model Blueprint (Grade 1-13)

This logical schema provides a full-model baseline for a world-class K-12 SMS/SIS + LMS platform aligned to Kenya CBC.

Target size: 80-120 tables.
Included here: 100 tables.

## 1. Modeling Standards

- Use UUID primary keys for all core entities.
- Include `tenant_id`, `school_id`, `created_at`, `updated_at` in tenant-scoped tables.
- Use soft delete where legal/audit requirements require record retention.
- Preserve immutable snapshots for report cards, transcripts, and billing statements.
- Store PII in encrypted columns where possible.

## 2. Domain Table Catalog (100 Tables)

## 2.1 Platform, Tenancy, and Security (12)

1. `tenants` - tenant org registry.
2. `schools` - school entities under tenant.
3. `campuses` - campus or branch records.
4. `academic_years` - year definitions.
5. `terms` - term/semester definitions.
6. `users` - all system users.
7. `roles` - role definitions.
8. `permissions` - permission keys.
9. `role_permissions` - role to permission mapping.
10. `user_roles` - user to role mapping.
11. `user_sessions` - login sessions/tokens.
12. `audit_logs` - immutable audit trail.

## 2.2 SIS and Admissions (14)

13. `admission_applications` - applicant intake.
14. `admission_documents` - submitted documents.
15. `students` - canonical student profile.
16. `student_identifiers` - admission number, national IDs.
17. `guardians` - parent/guardian records.
18. `student_guardians` - guardian relationships.
19. `student_addresses` - physical/postal address history.
20. `student_medical_profiles` - high-level medical summary.
21. `enrollments` - student class/grade enrollment periods.
22. `class_streams` - class divisions (e.g., Grade 4 East).
23. `student_class_assignments` - student to class stream mapping.
24. `promotions` - grade progression records.
25. `transfers` - transfer in/out records.
26. `student_status_history` - active, graduated, withdrawn timeline.

## 2.3 Academic Structure and Staffing (10)

27. `grade_levels` - Grade 1 through Grade 13 definitions.
28. `learning_areas` - CBC learning areas/subjects.
29. `subjects` - teachable subjects and metadata.
30. `subject_grade_offerings` - subjects offered per grade.
31. `teachers` - teacher profile records.
32. `teacher_qualifications` - certifications and training.
33. `teacher_employment_history` - contracts and service history.
34. `teaching_allocations` - teacher-class-subject assignment.
35. `lesson_plans` - lesson planning artifacts.
36. `lesson_plan_resources` - links/files for lesson plans.

## 2.4 CBC Competency Framework (12)

37. `cbc_strands` - strands per learning area.
38. `cbc_sub_strands` - sub-strands under strands.
39. `cbc_learning_outcomes` - outcomes per sub-strand.
40. `cbc_competencies` - core competencies.
41. `cbc_outcome_competencies` - mapping outcomes to competencies.
42. `cbc_assessment_criteria` - criteria for outcome evaluation.
43. `cbc_rubrics` - rubric header.
44. `cbc_rubric_levels` - mastery levels per rubric.
45. `cbc_rubric_criteria` - criterion definitions.
46. `cbc_rubric_criterion_levels` - scoring descriptors.
47. `cbc_curriculum_versions` - curriculum versioning.
48. `cbc_curriculum_publish_log` - publication audit.

## 2.5 Assessment, Evidence, and Reporting (14)

49. `assessment_plans` - term assessment plans.
50. `assessment_activities` - tasks/quizzes/projects/practicals.
51. `assessment_activity_targets` - targeted class/grade/outcomes.
52. `assessment_submissions` - learner submissions.
53. `assessment_evidence_artifacts` - files/photos/videos links.
54. `assessment_scores` - scored attempts.
55. `assessment_score_breakdowns` - criterion-level scores.
56. `assessment_comments` - teacher narrative remarks.
57. `assessment_moderations` - moderation workflows.
58. `assessment_exemptions` - exemptions/accommodations.
59. `report_cards` - report card header.
60. `report_card_sections` - academic/behavior/attendance segments.
61. `report_card_entries` - subject/competency lines.
62. `report_card_snapshots` - immutable publish snapshots.

## 2.6 Attendance and Calendar (7)

63. `school_calendar_days` - working/non-working days.
64. `attendance_sessions` - attendance event headers.
65. `attendance_student_marks` - student present/absent/late.
66. `attendance_teacher_marks` - teacher attendance logs.
67. `attendance_excuses` - justified absence records.
68. `attendance_alert_rules` - threshold definitions.
69. `attendance_alert_events` - generated alerts.

## 2.7 Timetable and Scheduling (6)

70. `timetables` - timetable versions.
71. `timetable_slots` - timetable periods.
72. `timetable_slot_assignments` - subject-teacher-room assignments.
73. `rooms` - room inventory.
74. `exam_timetables` - exam schedule header.
75. `exam_timetable_slots` - exam period rows.

## 2.8 LMS and Learning Content (7)

76. `courses` - LMS course shell.
77. `course_enrollments` - learners/teachers in course.
78. `course_modules` - unit/module structure.
79. `course_materials` - file/link/video materials.
80. `course_assignments` - LMS assignments.
81. `assignment_submissions` - submission entries.
82. `discussion_threads` - discussion forum topics.

## 2.9 Finance and Billing (9)

83. `fee_categories` - tuition/transport/lab/etc.
84. `fee_structures` - fee setup per grade/term.
85. `fee_structure_lines` - billable components.
86. `student_invoices` - invoice headers.
87. `student_invoice_lines` - invoice items.
88. `payments` - payment records.
89. `payment_allocations` - payment-to-invoice allocation.
90. `fee_waivers` - waivers/scholarships.
91. `finance_ledger_entries` - accounting-style ledger.

## 2.10 Communication and Engagement (4)

92. `message_templates` - SMS/email/push templates.
93. `messages` - outbound communication payloads.
94. `message_recipients` - recipient resolution table.
95. `message_delivery_logs` - provider delivery status.

## 2.11 Student Welfare and Operations (7)

96. `health_clinic_visits` - clinic visit records.
97. `health_immunizations` - vaccination status.
98. `discipline_incidents` - reported incidents.
99. `discipline_actions` - actions/sanctions.
100. `document_registry` - generated certificates/transcripts/report files.

## 3. Key Relationships

- `students` 1..* `enrollments`
- `students` *..* `guardians` via `student_guardians`
- `enrollments` *..1 `class_streams`
- `subjects` *..* `grade_levels` via `subject_grade_offerings`
- `assessment_activities` *..* `cbc_learning_outcomes` via `assessment_activity_targets`
- `assessment_scores` 1..* `assessment_score_breakdowns`
- `report_cards` 1..* `report_card_entries`
- `student_invoices` 1..* `student_invoice_lines`
- `payments` *..* `student_invoices` via `payment_allocations`

## 4. Indexing Guidance

- Composite indexes:
  - (`tenant_id`, `school_id`, `created_at`) for high-traffic operational tables.
  - (`student_id`, `term_id`) for `assessment_scores`, `attendance_student_marks`, `student_invoices`.
  - (`class_stream_id`, `date`) for attendance roll calls.
- Unique constraints:
  - (`tenant_id`, `school_id`, `admission_no`) in `student_identifiers`.
  - (`tenant_id`, `school_id`, `invoice_no`) in `student_invoices`.
- Partitioning candidates:
  - `audit_logs`, `attendance_student_marks`, `assessment_scores`, `finance_ledger_entries`.

## 5. Snapshot and Audit Requirements

- On report publication, copy computed data into `report_card_snapshots`.
- On transcript generation, write hash and signature to `document_registry`.
- All grade edits and invoice reversals must emit auditable events and `audit_logs` records.

## 6. Suggested Next Technical Step

- Convert this logical model into physical PostgreSQL DDL in migrations.
- Start with Wave 1 tables:
  - Platform/Security, SIS, Academic, Attendance, Communication.
- Add Wave 2:
  - CBC framework + Assessment + Reporting snapshots.
