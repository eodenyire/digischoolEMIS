from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateClinicVisitRequest, CreateHealthRecordRequest

router = APIRouter()


@router.post("/health/students/{student_id}/records")
def create_health_record(
    student_id: UUID, payload: CreateHealthRecordRequest, x_tenant_id: UUID = Header(...)
) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "student_id": str(student_id),
            "blood_group": payload.blood_group,
            "allergies": payload.allergies,
            "chronic_conditions": payload.chronic_conditions,
            "emergency_contact_name": payload.emergency_contact_name,
            "emergency_contact_phone": payload.emergency_contact_phone,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/health/clinic-visits")
def create_clinic_visit(payload: CreateClinicVisitRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "student_id": str(payload.student_id),
            "visit_at": payload.visit_at.isoformat(),
            "complaint": payload.complaint,
            "diagnosis": payload.diagnosis,
            "treatment": payload.treatment,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/health/students/{student_id}")
def get_student_health(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "student_id": str(student_id),
            "health_record": {
                "id": str(uuid4()),
                "blood_group": "O+",
                "allergies": None,
                "chronic_conditions": None,
                "emergency_contact_name": "Mary Wanjiku",
                "emergency_contact_phone": "+254711000000",
            },
            "clinic_visits": [
                {
                    "id": str(uuid4()),
                    "visit_at": "2024-03-12T10:30:00Z",
                    "complaint": "headache",
                    "diagnosis": "tension headache",
                    "treatment": "paracetamol",
                }
            ],
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
