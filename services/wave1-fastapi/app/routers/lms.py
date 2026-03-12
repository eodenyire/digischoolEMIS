from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateAssignmentRequest, CreateCourseRequest, CreateSubmissionRequest

router = APIRouter()


@router.post("/courses")
def create_course(payload: CreateCourseRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "class_stream_id": str(payload.class_stream_id),
            "subject_id": str(payload.subject_id),
            "teacher_id": str(payload.teacher_id),
            "title": payload.title,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/courses/{course_id}/assignments")
def create_assignment(course_id: UUID, payload: CreateAssignmentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "course_id": str(course_id),
            "title": payload.title,
            "instructions": payload.instructions,
            "due_at": payload.due_at.isoformat() if payload.due_at else None,
            "max_score": payload.max_score,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/assignments/{assignment_id}/submissions")
def create_submission(assignment_id: UUID, payload: CreateSubmissionRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "assignment_id": str(assignment_id),
            "student_id": str(payload.student_id),
            "content": payload.content,
            "file_url": payload.file_url,
            "submitted_at": "2024-04-01T08:00:00Z",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
