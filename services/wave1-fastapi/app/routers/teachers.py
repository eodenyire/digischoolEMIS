from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateTeacherRequest, CreateTeachingAllocationRequest

router = APIRouter()


@router.post("/teachers")
def create_teacher(payload: CreateTeacherRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "first_name": payload.first_name,
            "last_name": payload.last_name,
            "tsc_number": payload.tsc_number,
            "email": payload.email,
            "phone": payload.phone,
            "hire_date": str(payload.hire_date) if payload.hire_date else None,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/teachers/{teacher_id}")
def get_teacher(teacher_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(teacher_id),
            "school_id": str(uuid4()),
            "first_name": "John",
            "last_name": "Kamau",
            "tsc_number": "TSC-12345",
            "email": "jkamau@school.ke",
            "phone": "+254700000000",
            "hire_date": "2020-01-15",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/teaching-allocations")
def create_teaching_allocation(payload: CreateTeachingAllocationRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "academic_year_id": str(payload.academic_year_id),
            "term_id": str(payload.term_id) if payload.term_id else None,
            "teacher_id": str(payload.teacher_id),
            "class_stream_id": str(payload.class_stream_id),
            "subject_id": str(payload.subject_id),
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
