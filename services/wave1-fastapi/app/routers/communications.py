from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateMessageRequest

router = APIRouter()


@router.post("/communications/messages", status_code=202)
def create_message(payload: CreateMessageRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "channel": payload.channel,
            "subject": payload.subject,
            "body": payload.body,
            "status": "queued",
            "sent_at": None,
            "recipient_count": len(payload.recipients),
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/communications/delivery-status/{message_id}")
def get_delivery_status(message_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(message_id),
            "school_id": str(uuid4()),
            "channel": "sms",
            "subject": None,
            "body": "Message accepted",
            "status": "sent",
            "sent_at": "2026-03-10T08:30:00Z",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
