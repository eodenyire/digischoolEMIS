from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateBoardingAllocationRequest, CreateDormitoryRequest

router = APIRouter()


@router.post("/boarding/dormitories")
def create_dormitory(payload: CreateDormitoryRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "dorm_code": payload.dorm_code,
            "dorm_name": payload.dorm_name,
            "capacity": payload.capacity,
            "gender": payload.gender,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/boarding/allocations")
def create_boarding_allocation(payload: CreateBoardingAllocationRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "dormitory_id": str(payload.dormitory_id),
            "student_id": str(payload.student_id),
            "academic_year_id": str(payload.academic_year_id),
            "bed_number": payload.bed_number,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/boarding/dormitories/{dorm_id}/occupants")
def get_dorm_occupants(dorm_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "dormitory_id": str(dorm_id),
                "student_id": str(uuid4()),
                "bed_number": "A-01",
                "academic_year_id": str(uuid4()),
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
