from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateDisciplineActionRequest, CreateDisciplineIncidentRequest

router = APIRouter()


@router.post("/discipline/incidents")
def create_incident(payload: CreateDisciplineIncidentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "student_id": str(payload.student_id),
            "incident_date": str(payload.incident_date),
            "incident_type": payload.incident_type,
            "severity": payload.severity,
            "description": payload.description,
            "status": "open",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/discipline/actions")
def create_action(payload: CreateDisciplineActionRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "incident_id": str(payload.incident_id),
            "action_type": payload.action_type,
            "action_details": payload.action_details,
            "action_date": str(payload.action_date),
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/discipline/students/{student_id}")
def get_student_discipline(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "student_id": str(student_id),
                "incident_date": "2024-02-10",
                "incident_type": "misconduct",
                "severity": "low",
                "description": "Disrupted class",
                "status": "resolved",
                "actions": [
                    {
                        "id": str(uuid4()),
                        "action_type": "verbal_warning",
                        "action_date": "2024-02-10",
                    }
                ],
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
