"""
Integration & regression tests for all DigiSchool EMIS Wave 1 API endpoints.

These tests complement the unit tests in test_api.py and cover every endpoint
that was not exercised there, giving 100% route coverage for Wave 1 stubs.

Run with:
    cd services/wave1-fastapi
    python -m pytest tests/test_integration.py -v
"""

from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)

TENANT_ID = "7b0d4a39-9be0-4f35-909f-df73cb7f9999"
HEADERS = {"X-Tenant-Id": TENANT_ID}
SCHOOL_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0011"
STUDENT_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0022"
TEACHER_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0088"
ACADEMIC_YEAR_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0066"
TERM_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0033"
CLASS_STREAM_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0044"
SUBJECT_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0077"
LEARNING_AREA_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab01"
STRAND_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab02"
SUB_STRAND_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab03"
PLAN_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab04"
ACTIVITY_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0055"
COURSE_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab05"
ASSIGNMENT_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab06"
INCIDENT_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab07"
TIMETABLE_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab08"
BOOK_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab09"
LOAN_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab10"
ROUTE_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab11"
DORM_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab12"
MESSAGE_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab13"
USER_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7fab14"


# ─── Auth ────────────────────────────────────────────────────────────────────

def test_auth_refresh() -> None:
    response = client.post(
        "/api/v1/auth/refresh",
        json={"refresh_token": "some-refresh-token"},
    )
    body = response.json()

    assert response.status_code == 200
    assert "access_token" in body["data"]
    assert "refresh_token" in body["data"]
    assert body["data"]["expires_in"] == 3600


# ─── Users ───────────────────────────────────────────────────────────────────

def test_create_user() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "first_name": "Brian",
        "last_name": "Otieno",
        "email": "brian.otieno@school.ke",
        "password": "SecurePass1!",
    }
    response = client.post("/api/v1/users", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["first_name"] == "Brian"
    assert body["data"]["email"] == "brian.otieno@school.ke"
    assert body["data"]["status"] == "active"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_user() -> None:
    response = client.get(f"/api/v1/users/{USER_UUID}", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == USER_UUID
    assert "first_name" in body["data"]
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Students ────────────────────────────────────────────────────────────────

def test_get_student() -> None:
    response = client.get(f"/api/v1/students/{STUDENT_UUID}", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == STUDENT_UUID
    assert body["data"]["tenant_id"] == TENANT_ID


def test_update_student() -> None:
    payload = {"first_name": "Janet", "last_name": "Wanjiku"}
    response = client.patch(f"/api/v1/students/{STUDENT_UUID}", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == STUDENT_UUID
    assert body["data"]["first_name"] == "Janet"
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Enrollments ─────────────────────────────────────────────────────────────

def test_create_enrollment() -> None:
    payload = {
        "student_id": STUDENT_UUID,
        "school_id": SCHOOL_UUID,
        "academic_year_id": ACADEMIC_YEAR_UUID,
        "term_id": TERM_UUID,
        "enrollment_status": "active",
        "enrolled_at": "2024-01-15",
    }
    response = client.post("/api/v1/enrollments", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["enrollment_status"] == "active"
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Academic ────────────────────────────────────────────────────────────────

def test_create_subject() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "code": "ENG-001",
        "name": "English",
        "is_elective": False,
    }
    response = client.post("/api/v1/subjects", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["code"] == "ENG-001"
    assert body["data"]["name"] == "English"
    assert body["data"]["is_elective"] is False
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_class_stream() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "academic_year_id": ACADEMIC_YEAR_UUID,
        "name": "Grade 5 Blue",
        "grade_code": "Grade 5",
        "capacity": 35,
    }
    response = client.post("/api/v1/class-streams", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["name"] == "Grade 5 Blue"
    assert body["data"]["grade_code"] == "Grade 5"
    assert body["data"]["capacity"] == 35
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Attendance ───────────────────────────────────────────────────────────────

def test_create_attendance_session() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "class_stream_id": CLASS_STREAM_UUID,
        "session_date": "2024-03-15",
        "session_type": "morning",
    }
    response = client.post("/api/v1/attendance/sessions", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["class_stream_id"] == CLASS_STREAM_UUID
    assert body["data"]["session_type"] == "morning"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_attendance_mark() -> None:
    session_id = "7b0d4a39-9be0-4f35-909f-df73cb7faa01"
    payload = {
        "attendance_session_id": session_id,
        "student_id": STUDENT_UUID,
        "status": "present",
    }
    response = client.post("/api/v1/attendance/marks", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["attendance_session_id"] == session_id
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["status"] == "present"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_student_attendance() -> None:
    response = client.get(
        f"/api/v1/attendance/students/{STUDENT_UUID}",
        headers=HEADERS,
        params={"from": "2024-01-01", "to": "2024-03-31"},
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert len(body["data"]) >= 1
    assert body["data"][0]["student_id"] == STUDENT_UUID


# ─── Communications ───────────────────────────────────────────────────────────

def test_create_message() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "channel": "sms",
        "body": "Reminder: School fees are due on 31 March.",
        "recipients": [
            {
                "recipient_type": "guardian",
                "destination": "+254711000001",
            }
        ],
    }
    response = client.post("/api/v1/communications/messages", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 202
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["channel"] == "sms"
    assert body["data"]["status"] == "queued"
    assert body["data"]["recipient_count"] == 1
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_delivery_status() -> None:
    response = client.get(
        f"/api/v1/communications/delivery-status/{MESSAGE_UUID}",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == MESSAGE_UUID
    assert body["data"]["status"] in ("sent", "queued", "failed")
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── CBC ──────────────────────────────────────────────────────────────────────

def test_create_cbc_strand() -> None:
    payload = {
        "learning_area_id": LEARNING_AREA_UUID,
        "code": "STR-001",
        "name": "Numbers",
    }
    response = client.post("/api/v1/cbc/strands", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["learning_area_id"] == LEARNING_AREA_UUID
    assert body["data"]["code"] == "STR-001"
    assert body["data"]["name"] == "Numbers"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_cbc_sub_strand() -> None:
    payload = {
        "strand_id": STRAND_UUID,
        "code": "SS-001",
        "name": "Whole Numbers",
    }
    response = client.post("/api/v1/cbc/sub-strands", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["strand_id"] == STRAND_UUID
    assert body["data"]["code"] == "SS-001"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_cbc_learning_outcome() -> None:
    payload = {
        "sub_strand_id": SUB_STRAND_UUID,
        "code": "LO-001",
        "description": "Count objects up to 100",
    }
    response = client.post("/api/v1/cbc/outcomes", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["sub_strand_id"] == SUB_STRAND_UUID
    assert body["data"]["code"] == "LO-001"
    assert body["data"]["description"] == "Count objects up to 100"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_cbc_competency() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "name": "Critical Thinking",
        "code": "COMP-CT",
        "description": "Ability to analyse and evaluate information",
    }
    response = client.post("/api/v1/cbc/competencies", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["name"] == "Critical Thinking"
    assert body["data"]["code"] == "COMP-CT"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_cbc_rubric() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "name": "Grade 4 Math Rubric",
        "description": "Performance rubric for Grade 4 Mathematics",
    }
    response = client.post("/api/v1/cbc/rubrics", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["name"] == "Grade 4 Math Rubric"
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Assessments ──────────────────────────────────────────────────────────────

def test_create_assessment_activity() -> None:
    payload = {
        "assessment_plan_id": PLAN_UUID,
        "subject_id": SUBJECT_UUID,
        "title": "Term 1 Written Test",
        "activity_type": "written",
        "max_score": 30.0,
        "due_at": "2024-03-20",
    }
    response = client.post("/api/v1/assessments/activities", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["assessment_plan_id"] == PLAN_UUID
    assert body["data"]["title"] == "Term 1 Written Test"
    assert body["data"]["activity_type"] == "written"
    assert body["data"]["max_score"] == 30.0
    assert body["data"]["tenant_id"] == TENANT_ID


def test_publish_report_card() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "class_stream_id": CLASS_STREAM_UUID,
        "term_id": TERM_UUID,
    }
    response = client.post("/api/v1/report-cards/publish", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["class_stream_id"] == CLASS_STREAM_UUID
    assert body["data"]["status"] == "queued"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_student_report_cards() -> None:
    response = client.get(
        f"/api/v1/report-cards/students/{STUDENT_UUID}",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert body["data"][0]["student_id"] == STUDENT_UUID
    assert body["data"][0]["tenant_id"] == TENANT_ID


# ─── Finance ──────────────────────────────────────────────────────────────────

def test_create_finance_invoice() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "student_id": STUDENT_UUID,
        "term_id": TERM_UUID,
        "invoice_date": "2024-01-10",
        "due_date": "2024-03-31",
        "line_items": [
            {"description": "Tuition", "amount": 15000.00},
            {"description": "Activity fee", "amount": 2000.00},
        ],
    }
    response = client.post("/api/v1/finance/invoices", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["status"] == "unpaid"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_student_finance_ledger() -> None:
    response = client.get(
        f"/api/v1/finance/ledger/students/{STUDENT_UUID}",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert "balance" in body["data"]
    assert isinstance(body["data"]["invoices"], list)
    assert isinstance(body["data"]["payments"], list)
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Timetable ────────────────────────────────────────────────────────────────

def test_create_timetable_slot() -> None:
    payload = {
        "timetable_id": TIMETABLE_UUID,
        "class_stream_id": CLASS_STREAM_UUID,
        "subject_id": SUBJECT_UUID,
        "teacher_id": TEACHER_UUID,
        "day_of_week": 1,
        "start_time": "08:00:00",
        "end_time": "09:00:00",
        "room": "Lab 1",
    }
    response = client.post("/api/v1/timetable-slots", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["timetable_id"] == TIMETABLE_UUID
    assert body["data"]["class_stream_id"] == CLASS_STREAM_UUID
    assert body["data"]["day_of_week"] == 1
    assert body["data"]["room"] == "Lab 1"
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Teachers ─────────────────────────────────────────────────────────────────

def test_get_teacher() -> None:
    response = client.get(f"/api/v1/teachers/{TEACHER_UUID}", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == TEACHER_UUID
    assert "first_name" in body["data"]
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_teaching_allocation() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "academic_year_id": ACADEMIC_YEAR_UUID,
        "term_id": TERM_UUID,
        "teacher_id": TEACHER_UUID,
        "class_stream_id": CLASS_STREAM_UUID,
        "subject_id": SUBJECT_UUID,
    }
    response = client.post("/api/v1/teaching-allocations", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["teacher_id"] == TEACHER_UUID
    assert body["data"]["class_stream_id"] == CLASS_STREAM_UUID
    assert body["data"]["subject_id"] == SUBJECT_UUID
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── LMS ──────────────────────────────────────────────────────────────────────

def test_create_lms_assignment() -> None:
    payload = {
        "course_id": COURSE_UUID,
        "title": "Chapter 3 Exercise",
        "instructions": "Answer all questions in chapter 3.",
        "due_at": "2024-03-25T23:59:00",
        "max_score": 20.0,
    }
    response = client.post(
        f"/api/v1/courses/{COURSE_UUID}/assignments",
        headers=HEADERS,
        json=payload,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["course_id"] == COURSE_UUID
    assert body["data"]["title"] == "Chapter 3 Exercise"
    assert body["data"]["max_score"] == 20.0
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_lms_submission() -> None:
    payload = {
        "assignment_id": ASSIGNMENT_UUID,
        "student_id": STUDENT_UUID,
        "content": "My answers to chapter 3 exercise.",
    }
    response = client.post(
        f"/api/v1/assignments/{ASSIGNMENT_UUID}/submissions",
        headers=HEADERS,
        json=payload,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["assignment_id"] == ASSIGNMENT_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Discipline ───────────────────────────────────────────────────────────────

def test_create_discipline_action() -> None:
    payload = {
        "incident_id": INCIDENT_UUID,
        "action_type": "verbal_warning",
        "action_details": "Student warned about classroom conduct.",
        "action_date": "2024-03-12",
    }
    response = client.post("/api/v1/discipline/actions", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["incident_id"] == INCIDENT_UUID
    assert body["data"]["action_type"] == "verbal_warning"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_student_discipline() -> None:
    response = client.get(
        f"/api/v1/discipline/students/{STUDENT_UUID}",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert body["data"][0]["student_id"] == STUDENT_UUID
    assert "incident_type" in body["data"][0]


# ─── Health ───────────────────────────────────────────────────────────────────

def test_create_health_record() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "student_id": STUDENT_UUID,
        "blood_group": "A+",
        "allergies": "None",
        "emergency_contact_name": "Mary Wanjiku",
        "emergency_contact_phone": "+254711000000",
    }
    response = client.post(
        f"/api/v1/health/students/{STUDENT_UUID}/records",
        headers=HEADERS,
        json=payload,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["blood_group"] == "A+"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_student_health() -> None:
    response = client.get(
        f"/api/v1/health/students/{STUDENT_UUID}",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert "health_record" in body["data"]
    assert "clinic_visits" in body["data"]
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Library ──────────────────────────────────────────────────────────────────

def test_create_library_loan() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "book_id": BOOK_UUID,
        "student_id": STUDENT_UUID,
        "due_date": "2024-04-15",
    }
    response = client.post("/api/v1/library/loans", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["book_id"] == BOOK_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["status"] == "active"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_return_library_book() -> None:
    response = client.post(f"/api/v1/library/returns/{LOAN_UUID}", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["id"] == LOAN_UUID
    assert body["data"]["status"] == "returned"
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Transport ────────────────────────────────────────────────────────────────

def test_create_transport_assignment() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "route_id": ROUTE_UUID,
        "student_id": STUDENT_UUID,
        "pickup_point": "Westlands Stage",
    }
    response = client.post("/api/v1/transport/assignments", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["route_id"] == ROUTE_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["pickup_point"] == "Westlands Stage"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_route_students() -> None:
    response = client.get(
        f"/api/v1/transport/routes/{ROUTE_UUID}/students",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert body["data"][0]["route_id"] == ROUTE_UUID
    assert "student_id" in body["data"][0]


# ─── Boarding ─────────────────────────────────────────────────────────────────

def test_create_boarding_allocation() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "dormitory_id": DORM_UUID,
        "student_id": STUDENT_UUID,
        "academic_year_id": ACADEMIC_YEAR_UUID,
        "bed_number": "A-12",
    }
    response = client.post("/api/v1/boarding/allocations", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["dormitory_id"] == DORM_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["bed_number"] == "A-12"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_get_dorm_occupants() -> None:
    response = client.get(
        f"/api/v1/boarding/dormitories/{DORM_UUID}/occupants",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert body["data"][0]["dormitory_id"] == DORM_UUID
    assert "student_id" in body["data"][0]


# ─── Analytics ────────────────────────────────────────────────────────────────

def test_analytics_student_risk() -> None:
    response = client.get(
        f"/api/v1/analytics/students/{STUDENT_UUID}/risk",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert "risk_score" in body["data"]
    assert "risk_level" in body["data"]
    assert "factors" in body["data"]
    assert body["data"]["tenant_id"] == TENANT_ID


# ─── Parent Portal ────────────────────────────────────────────────────────────

def test_parent_list_students() -> None:
    response = client.get("/api/v1/parent/students", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert len(body["data"]) >= 1
    assert "first_name" in body["data"][0]
    assert body["data"][0]["tenant_id"] == TENANT_ID


def test_parent_student_report_cards() -> None:
    response = client.get(
        f"/api/v1/parent/students/{STUDENT_UUID}/report-cards",
        headers=HEADERS,
    )
    body = response.json()

    assert response.status_code == 200
    assert isinstance(body["data"], list)
    assert body["data"][0]["student_id"] == STUDENT_UUID
    assert "subjects" in body["data"][0]
    assert body["data"][0]["tenant_id"] == TENANT_ID
