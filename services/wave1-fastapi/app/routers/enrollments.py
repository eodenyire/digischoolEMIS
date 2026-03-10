from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateEnrollmentRequest

router = APIRouter()


@router.post("/enrollments")
def create_enrollment(payload: CreateEnrollmentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "student_id": str(payload.student_id),
            "school_id": str(payload.school_id),
            "academic_year_id": str(payload.academic_year_id),
            "term_id": str(payload.term_id) if payload.term_id else None,
            "enrollment_status": payload.enrollment_status,
            "enrolled_at": payload.enrolled_at,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
