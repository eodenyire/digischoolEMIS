from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)

TENANT_ID = "7b0d4a39-9be0-4f35-909f-df73cb7f9999"
HEADERS = {"X-Tenant-Id": TENANT_ID}
SCHOOL_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0011"
STUDENT_UUID = "7b0d4a39-9be0-4f35-909f-df73cb7f0022"


def test_health_endpoint() -> None:
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_auth_login_endpoint() -> None:
    payload = {"email": "teacher@example.com", "password": "strongpass123"}
    response = client.post("/api/v1/auth/login", json=payload)
    body = response.json()

    assert response.status_code == 200
    assert "access_token" in body["data"]
    assert "refresh_token" in body["data"]
    assert body["data"]["user"]["email"] == payload["email"]


def test_create_student_endpoint() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "first_name": "Jane",
        "last_name": "Wanjiku",
        "sex": "female",
    }
    response = client.post("/api/v1/students", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["first_name"] == "Jane"
    assert body["data"]["last_name"] == "Wanjiku"
    assert body["data"]["tenant_id"] == TENANT_ID


def test_create_cbc_learning_area() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "code": "LA-001",
        "name": "Mathematics",
        "grade_band": "LOWER_PRIMARY",
    }
    response = client.post("/api/v1/cbc/learning-areas", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["code"] == "LA-001"
    assert body["data"]["name"] == "Mathematics"
    assert body["data"]["grade_band"] == "LOWER_PRIMARY"


def test_create_assessment_plan() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "term_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0033",
        "class_stream_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0044",
        "name": "Term 1 Assessment Plan",
    }
    response = client.post("/api/v1/assessments/plans", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["name"] == "Term 1 Assessment Plan"


def test_create_assessment_score() -> None:
    payload = {
        "activity_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0055",
        "student_id": STUDENT_UUID,
        "performance_level": "ME",
    }
    response = client.post("/api/v1/assessments/scores", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["performance_level"] == "ME"


def test_create_fee_structure() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "academic_year_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0066",
        "grade_code": "Grade 4",
        "fee_category": "tuition",
        "amount": 15000.00,
    }
    response = client.post("/api/v1/finance/fee-structures", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["grade_code"] == "Grade 4"
    assert body["data"]["fee_category"] == "tuition"


def test_create_finance_payment() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "student_id": STUDENT_UUID,
        "amount": 5000.00,
        "payment_method": "mpesa",
    }
    response = client.post("/api/v1/finance/payments", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["payment_method"] == "mpesa"


def test_create_teacher() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "first_name": "Alice",
        "last_name": "Njeri",
        "tsc_number": "TSC-99001",
    }
    response = client.post("/api/v1/teachers", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["first_name"] == "Alice"
    assert body["data"]["last_name"] == "Njeri"
    assert body["data"]["tsc_number"] == "TSC-99001"


def test_create_timetable() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "academic_year_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0066",
        "name": "Term 1 2024 Timetable",
    }
    response = client.post("/api/v1/timetables", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["name"] == "Term 1 2024 Timetable"


def test_create_lms_course() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "class_stream_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0044",
        "subject_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0077",
        "teacher_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0088",
        "title": "Mathematics Grade 4",
    }
    response = client.post("/api/v1/courses", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["title"] == "Mathematics Grade 4"


def test_create_discipline_incident() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "student_id": STUDENT_UUID,
        "incident_date": "2024-03-10",
        "incident_type": "misconduct",
    }
    response = client.post("/api/v1/discipline/incidents", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["incident_type"] == "misconduct"


def test_create_clinic_visit() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "student_id": STUDENT_UUID,
        "visit_at": "2024-03-12T10:30:00",
        "complaint": "headache",
    }
    response = client.post("/api/v1/health/clinic-visits", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["student_id"] == STUDENT_UUID
    assert body["data"]["complaint"] == "headache"


def test_create_library_book() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "title": "Mathematics Textbook Grade 4",
        "author": "Kenya Institute of Curriculum Development",
    }
    response = client.post("/api/v1/library/books", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["title"] == "Mathematics Textbook Grade 4"
    assert body["data"]["author"] == "Kenya Institute of Curriculum Development"


def test_create_transport_route() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "route_code": "RT-001",
        "route_name": "Westlands Express",
    }
    response = client.post("/api/v1/transport/routes", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["route_code"] == "RT-001"
    assert body["data"]["route_name"] == "Westlands Express"


def test_create_dormitory() -> None:
    payload = {
        "school_id": SCHOOL_UUID,
        "dorm_code": "DORM-A",
        "dorm_name": "Block A",
        "capacity": 30,
    }
    response = client.post("/api/v1/boarding/dormitories", headers=HEADERS, json=payload)
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["school_id"] == SCHOOL_UUID
    assert body["data"]["dorm_code"] == "DORM-A"
    assert body["data"]["dorm_name"] == "Block A"
    assert body["data"]["capacity"] == 30


def test_analytics_school_overview() -> None:
    response = client.get(f"/api/v1/analytics/schools/{SCHOOL_UUID}/overview", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert "data" in body
    assert body["data"]["school_id"] == SCHOOL_UUID


def test_parent_student_dashboard() -> None:
    response = client.get(f"/api/v1/parent/students/{STUDENT_UUID}/dashboard", headers=HEADERS)
    body = response.json()

    assert response.status_code == 200
    assert "data" in body
    assert body["data"]["student_id"] == STUDENT_UUID
