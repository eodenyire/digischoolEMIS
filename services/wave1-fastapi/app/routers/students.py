from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateStudentRequest, UpdateStudentRequest

router = APIRouter()


@router.post("/students")
def create_student(payload: CreateStudentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "campus_id": str(payload.campus_id) if payload.campus_id else None,
            "first_name": payload.first_name,
            "middle_name": payload.middle_name,
            "last_name": payload.last_name,
            "sex": payload.sex,
            "date_of_birth": payload.date_of_birth,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/students/{student_id}")
def get_student(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(student_id),
            "school_id": str(uuid4()),
            "campus_id": None,
            "first_name": "Jane",
            "middle_name": "N",
            "last_name": "Doe",
            "sex": "female",
            "date_of_birth": "2014-01-12",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.patch("/students/{student_id}")
def update_student(student_id: UUID, payload: UpdateStudentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(student_id),
            "school_id": str(uuid4()),
            "campus_id": None,
            "first_name": payload.first_name or "Jane",
            "middle_name": payload.middle_name,
            "last_name": payload.last_name or "Doe",
            "sex": payload.sex,
            "date_of_birth": payload.date_of_birth,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
