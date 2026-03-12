from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateTransportAssignmentRequest, CreateTransportRouteRequest

router = APIRouter()


@router.post("/transport/routes")
def create_route(payload: CreateTransportRouteRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "route_code": payload.route_code,
            "route_name": payload.route_name,
            "description": payload.description,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/transport/assignments")
def create_transport_assignment(payload: CreateTransportAssignmentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "route_id": str(payload.route_id),
            "student_id": str(payload.student_id),
            "pickup_point": payload.pickup_point,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/transport/routes/{route_id}/students")
def get_route_students(route_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": [
            {
                "id": str(uuid4()),
                "route_id": str(route_id),
                "student_id": str(uuid4()),
                "pickup_point": "Westlands Stage",
                "tenant_id": str(x_tenant_id),
            }
        ],
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
