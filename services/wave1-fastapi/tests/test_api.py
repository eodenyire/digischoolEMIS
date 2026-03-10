from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


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
        "school_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0011",
        "first_name": "Jane",
        "last_name": "Wanjiku",
        "sex": "female",
    }
    response = client.post(
        "/api/v1/students",
        headers={"X-Tenant-Id": "7b0d4a39-9be0-4f35-909f-df73cb7f9999"},
        json=payload,
    )
    body = response.json()

    assert response.status_code == 200
    assert body["data"]["first_name"] == "Jane"
    assert body["data"]["last_name"] == "Wanjiku"
    assert body["data"]["tenant_id"] == "7b0d4a39-9be0-4f35-909f-df73cb7f9999"
