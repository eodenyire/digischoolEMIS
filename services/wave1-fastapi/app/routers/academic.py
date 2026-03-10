from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateClassStreamRequest, CreateSubjectRequest

router = APIRouter()


@router.post("/subjects")
def create_subject(payload: CreateSubjectRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "learning_area_id": str(payload.learning_area_id) if payload.learning_area_id else None,
            "code": payload.code,
            "name": payload.name,
            "is_elective": payload.is_elective,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/class-streams")
def create_class_stream(payload: CreateClassStreamRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "academic_year_id": str(payload.academic_year_id),
            "name": payload.name,
            "grade_code": payload.grade_code,
            "capacity": payload.capacity,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
