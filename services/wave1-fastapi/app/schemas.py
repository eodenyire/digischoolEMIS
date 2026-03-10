from datetime import date, datetime, time
from typing import Literal, Optional
from uuid import UUID

from pydantic import BaseModel, EmailStr, Field


class Meta(BaseModel):
    request_id: UUID
    version: str = "v1"


class Envelope(BaseModel):
    data: dict
    meta: Meta
    errors: list[dict] = []


class LoginRequest(BaseModel):
    email: EmailStr
    password: str = Field(min_length=8)


class RefreshRequest(BaseModel):
    refresh_token: str


class CreateUserRequest(BaseModel):
    school_id: UUID
    first_name: str
    last_name: str
    email: EmailStr
    phone: Optional[str] = None
    password: str = Field(min_length=8)


class CreateStudentRequest(BaseModel):
    school_id: UUID
    campus_id: Optional[UUID] = None
    first_name: str
    middle_name: Optional[str] = None
    last_name: str
    sex: Optional[Literal["male", "female", "other"]] = None
    date_of_birth: Optional[date] = None


class UpdateStudentRequest(BaseModel):
    first_name: Optional[str] = None
    middle_name: Optional[str] = None
    last_name: Optional[str] = None
    sex: Optional[Literal["male", "female", "other"]] = None
    date_of_birth: Optional[date] = None


class CreateEnrollmentRequest(BaseModel):
    student_id: UUID
    school_id: UUID
    academic_year_id: UUID
    term_id: Optional[UUID] = None
    enrollment_status: Literal["active", "inactive", "graduated", "transferred"] = "active"
    enrolled_at: date


class CreateSubjectRequest(BaseModel):
    school_id: UUID
    learning_area_id: Optional[UUID] = None
    code: str
    name: str
    is_elective: bool = False


class CreateClassStreamRequest(BaseModel):
    school_id: UUID
    academic_year_id: UUID
    name: str
    grade_code: str
    capacity: Optional[int] = None


class CreateAttendanceSessionRequest(BaseModel):
    school_id: UUID
    class_stream_id: UUID
    session_date: date
    session_type: Literal["morning", "afternoon", "full_day"]


class CreateAttendanceMarkRequest(BaseModel):
    attendance_session_id: UUID
    student_id: UUID
    status: Literal["present", "absent", "late", "excused"]
    arrived_at: Optional[time] = None
    reason: Optional[str] = None


class Recipient(BaseModel):
    recipient_type: Literal["student", "guardian", "teacher", "custom"]
    recipient_id: Optional[UUID] = None
    destination: str


class CreateMessageRequest(BaseModel):
    school_id: UUID
    channel: Literal["sms", "email", "push"]
    subject: Optional[str] = None
    body: str
    recipients: list[Recipient]


def now_utc() -> datetime:
    return datetime.utcnow()
