from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateUserRequest

router = APIRouter()


@router.post("/users")
def create_user(payload: CreateUserRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "first_name": payload.first_name,
            "last_name": payload.last_name,
            "email": payload.email,
            "phone": payload.phone,
            "status": "active",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/users/{user_id}")
def get_user(user_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(user_id),
            "school_id": str(uuid4()),
            "first_name": "Demo",
            "last_name": "User",
            "email": "demo@example.com",
            "phone": "+254700000000",
            "status": "active",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
