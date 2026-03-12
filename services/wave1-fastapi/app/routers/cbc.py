from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import (
    CreateCompetencyRequest,
    CreateLearningAreaRequest,
    CreateLearningOutcomeRequest,
    CreateRubricRequest,
    CreateStrandRequest,
    CreateSubStrandRequest,
)

router = APIRouter()


@router.post("/cbc/learning-areas")
def create_learning_area(payload: CreateLearningAreaRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "code": payload.code,
            "name": payload.name,
            "grade_band": payload.grade_band,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/cbc/strands")
def create_strand(payload: CreateStrandRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "learning_area_id": str(payload.learning_area_id),
            "code": payload.code,
            "name": payload.name,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/cbc/sub-strands")
def create_sub_strand(payload: CreateSubStrandRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "strand_id": str(payload.strand_id),
            "code": payload.code,
            "name": payload.name,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/cbc/outcomes")
def create_learning_outcome(payload: CreateLearningOutcomeRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "sub_strand_id": str(payload.sub_strand_id),
            "code": payload.code,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/cbc/competencies")
def create_competency(payload: CreateCompetencyRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "name": payload.name,
            "code": payload.code,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/cbc/rubrics")
def create_rubric(payload: CreateRubricRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "name": payload.name,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
