from uuid import UUID, uuid4

from fastapi import APIRouter, Header

router = APIRouter()


@router.get("/analytics/schools/{school_id}/overview")
def get_school_overview(school_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "school_id": str(school_id),
            "total_students": 842,
            "total_teachers": 54,
            "attendance_rate": 0.91,
            "average_performance_level": "ME",
            "fee_collection_rate": 0.78,
            "active_classes": 24,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/analytics/students/{student_id}/risk")
def get_student_risk(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "student_id": str(student_id),
            "risk_score": 0.12,
            "risk_level": "low",
            "factors": {
                "attendance_rate": 0.95,
                "performance_trend": "stable",
                "fee_arrears": False,
                "discipline_incidents": 0,
            },
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
