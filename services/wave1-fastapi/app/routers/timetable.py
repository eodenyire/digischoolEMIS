from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateTimetableRequest, CreateTimetableSlotRequest

router = APIRouter()


@router.post("/timetables")
def create_timetable(payload: CreateTimetableRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "academic_year_id": str(payload.academic_year_id),
            "term_id": str(payload.term_id) if payload.term_id else None,
            "name": payload.name,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/timetable-slots")
def create_timetable_slot(payload: CreateTimetableSlotRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "timetable_id": str(payload.timetable_id),
            "class_stream_id": str(payload.class_stream_id),
            "subject_id": str(payload.subject_id),
            "teacher_id": str(payload.teacher_id) if payload.teacher_id else None,
            "day_of_week": payload.day_of_week,
            "start_time": str(payload.start_time),
            "end_time": str(payload.end_time),
            "room": payload.room,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
