from uuid import UUID, uuid4

from fastapi import APIRouter, Header

router = APIRouter()


@router.get("/parent/students")
def list_parent_students(x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "first_name": "Jane",
                "last_name": "Wanjiku",
                "grade_code": "Grade 4",
                "class_stream": "4 Blue",
                "school_id": str(uuid4()),
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/parent/students/{student_id}/dashboard")
def get_parent_student_dashboard(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "student_id": str(student_id),
            "attendance_rate": 0.93,
            "current_performance": "ME",
            "outstanding_fees": 5000.00,
            "recent_assessments": [
                {
                    "subject": "Mathematics",
                    "performance_level": "ME",
                    "date": "2024-03-20",
                }
            ],
            "recent_incidents": [],
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/parent/students/{student_id}/report-cards")
def get_parent_student_report_cards(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "student_id": str(student_id),
                "term": "Term 1 2024",
                "overall_performance": "ME",
                "published_at": "2024-04-05T10:00:00Z",
                "subjects": [
                    {"subject": "Mathematics", "performance_level": "ME", "teacher_comment": "Good effort."},
                    {"subject": "English", "performance_level": "AE", "teacher_comment": "Needs improvement."},
                ],
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
