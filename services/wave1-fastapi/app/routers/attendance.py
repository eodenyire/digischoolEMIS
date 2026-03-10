from datetime import date
from uuid import UUID, uuid4

from fastapi import APIRouter, Header, Query

from app.schemas import CreateAttendanceMarkRequest, CreateAttendanceSessionRequest

router = APIRouter()


@router.post("/attendance/sessions")
def create_attendance_session(payload: CreateAttendanceSessionRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "class_stream_id": str(payload.class_stream_id),
            "session_date": payload.session_date,
            "session_type": payload.session_type,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/attendance/marks")
def create_attendance_mark(payload: CreateAttendanceMarkRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "attendance_session_id": str(payload.attendance_session_id),
            "student_id": str(payload.student_id),
            "status": payload.status,
            "arrived_at": payload.arrived_at,
            "reason": payload.reason,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/attendance/students/{student_id}")
def get_student_attendance(
    student_id: UUID,
    x_tenant_id: UUID = Header(...),
    from_date: date | None = Query(default=None, alias="from"),
    to_date: date | None = Query(default=None, alias="to"),
) -> dict:
    _ = (from_date, to_date)
    return {
        "data": [
            {
                "id": str(uuid4()),
                "attendance_session_id": str(uuid4()),
                "student_id": str(student_id),
                "status": "present",
                "arrived_at": None,
                "reason": None,
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
