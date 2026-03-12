from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateFeeStructureRequest, CreateInvoiceRequest, CreatePaymentRequest

router = APIRouter()


@router.post("/finance/fee-structures")
def create_fee_structure(payload: CreateFeeStructureRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "academic_year_id": str(payload.academic_year_id),
            "term_id": str(payload.term_id) if payload.term_id else None,
            "grade_code": payload.grade_code,
            "fee_category": payload.fee_category,
            "amount": payload.amount,
            "due_date": str(payload.due_date) if payload.due_date else None,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/finance/invoices")
def create_invoice(payload: CreateInvoiceRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "student_id": str(payload.student_id),
            "term_id": str(payload.term_id),
            "invoice_date": str(payload.invoice_date),
            "due_date": str(payload.due_date) if payload.due_date else None,
            "line_items": payload.line_items,
            "status": "unpaid",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/finance/payments")
def create_payment(payload: CreatePaymentRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "student_id": str(payload.student_id),
            "invoice_id": str(payload.invoice_id) if payload.invoice_id else None,
            "amount": payload.amount,
            "payment_method": payload.payment_method,
            "transaction_reference": payload.transaction_reference,
            "paid_at": str(payload.paid_at) if payload.paid_at else None,
            "status": "confirmed",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.get("/finance/ledger/students/{student_id}")
def get_student_ledger(student_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "student_id": str(student_id),
            "balance": 0.00,
            "invoices": [
                {
                    "id": str(uuid4()),
                    "amount": 15000.00,
                    "status": "unpaid",
                    "due_date": "2024-03-31",
                }
            ],
            "payments": [
                {
                    "id": str(uuid4()),
                    "amount": 10000.00,
                    "payment_method": "mpesa",
                    "paid_at": "2024-01-15",
                }
            ],
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
