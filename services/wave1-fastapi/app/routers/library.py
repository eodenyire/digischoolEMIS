from uuid import UUID, uuid4

from fastapi import APIRouter, Header

from app.schemas import CreateLibraryBookRequest, CreateLibraryLoanRequest

router = APIRouter()


@router.post("/library/books")
def create_book(payload: CreateLibraryBookRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "isbn": payload.isbn,
            "title": payload.title,
            "author": payload.author,
            "subject": payload.subject,
            "copy_count": payload.copy_count,
            "available_copies": payload.copy_count,
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/library/loans")
def create_loan(payload: CreateLibraryLoanRequest, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(uuid4()),
            "school_id": str(payload.school_id),
            "book_id": str(payload.book_id),
            "student_id": str(payload.student_id),
            "loaned_at": "2024-04-01",
            "due_date": str(payload.due_date),
            "returned_at": None,
            "status": "active",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }


@router.post("/library/returns/{loan_id}")
def return_book(loan_id: UUID, x_tenant_id: UUID = Header(...)) -> dict:
    return {
        "data": {
            "id": str(loan_id),
            "returned_at": "2024-04-10",
            "status": "returned",
            "tenant_id": str(x_tenant_id),
        },
        "meta": {"request_id": str(uuid4()), "version": "v1"},
        "errors": [],
    }
