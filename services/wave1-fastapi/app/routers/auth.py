from uuid import uuid4

from fastapi import APIRouter

from app.schemas import LoginRequest, RefreshRequest

router = APIRouter()


@router.post("/login")
def login(payload: LoginRequest) -> dict:
    return {
        "data": {
            "access_token": str(uuid4()),
            "refresh_token": str(uuid4()),
            "expires_in": 3600,
            "user": {
                "id": str(uuid4()),
                "first_name": "Demo",
                "last_name": "User",
                "email": payload.email,
                "status": "active",
            },
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/refresh")
def refresh(_: RefreshRequest) -> dict:
    return {
        "data": {
            "access_token": str(uuid4()),
            "refresh_token": str(uuid4()),
            "expires_in": 3600,
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
