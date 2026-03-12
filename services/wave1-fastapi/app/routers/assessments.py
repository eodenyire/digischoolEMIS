from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import (
    CreateAssessmentActivityRequest,
    CreateAssessmentPlanRequest,
    CreateAssessmentScoreRequest,
    PublishReportCardRequest,
)

router = APIRouter()


@router.post("/assessments/plans")
def create_assessment_plan(payload: CreateAssessmentPlanRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "term_id": str(payload.term_id),
            "class_stream_id": str(payload.class_stream_id),
            "name": payload.name,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/assessments/activities")
def create_assessment_activity(payload: CreateAssessmentActivityRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "assessment_plan_id": str(payload.assessment_plan_id),
            "subject_id": str(payload.subject_id),
            "title": payload.title,
            "activity_type": payload.activity_type,
            "max_score": payload.max_score,
            "due_at": str(payload.due_at) if payload.due_at else None,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/assessments/scores")
def create_assessment_score(payload: CreateAssessmentScoreRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "activity_id": str(payload.activity_id),
            "student_id": str(payload.student_id),
            "score": payload.score,
            "performance_level": payload.performance_level,
            "teacher_comment": payload.teacher_comment,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/report-cards/publish")
def publish_report_card(payload: PublishReportCardRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "job_id": str(uuid4()),
            "school_id": str(payload.school_id),
            "class_stream_id": str(payload.class_stream_id),
            "term_id": str(payload.term_id),
            "status": "queued",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/report-cards/students/{student_id}")
def get_student_report_cards(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "student_id": str(student_id),
                "term_id": str(uuid4()),
                "class_stream_id": str(uuid4()),
                "published_at": "2024-04-05T10:00:00Z",
                "overall_performance": "ME",
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
