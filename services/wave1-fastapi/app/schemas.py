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


# CBC Competency
class CreateLearningAreaRequest(BaseModel):
    school_id: UUID
    code: str
    name: str
    grade_band: str  # e.g. "LOWER_PRIMARY", "UPPER_PRIMARY", "JSS", "SS"


class CreateStrandRequest(BaseModel):
    learning_area_id: UUID
    code: str
    name: str


class CreateSubStrandRequest(BaseModel):
    strand_id: UUID
    code: str
    name: str


class CreateLearningOutcomeRequest(BaseModel):
    sub_strand_id: UUID
    code: str
    description: str


class CreateCompetencyRequest(BaseModel):
    school_id: UUID
    name: str
    code: str
    description: Optional[str] = None


class CreateRubricRequest(BaseModel):
    school_id: UUID
    name: str
    description: Optional[str] = None


# Assessment
class CreateAssessmentPlanRequest(BaseModel):
    school_id: UUID
    term_id: UUID
    class_stream_id: UUID
    name: str
    description: Optional[str] = None


class CreateAssessmentActivityRequest(BaseModel):
    assessment_plan_id: UUID
    subject_id: UUID
    title: str
    activity_type: Literal["observation", "oral", "written", "practical", "portfolio"]
    max_score: Optional[float] = None
    due_at: Optional[date] = None


class CreateAssessmentScoreRequest(BaseModel):
    activity_id: UUID
    student_id: UUID
    score: Optional[float] = None
    performance_level: Literal["BE", "AE", "ME", "EE"]
    teacher_comment: Optional[str] = None


class PublishReportCardRequest(BaseModel):
    school_id: UUID
    class_stream_id: UUID
    term_id: UUID


# Finance
class CreateFeeStructureRequest(BaseModel):
    school_id: UUID
    academic_year_id: UUID
    term_id: Optional[UUID] = None
    grade_code: str
    fee_category: str
    amount: float
    due_date: Optional[date] = None


class CreateInvoiceRequest(BaseModel):
    school_id: UUID
    student_id: UUID
    term_id: UUID
    invoice_date: date
    due_date: Optional[date] = None
    line_items: list[dict] = []


class CreatePaymentRequest(BaseModel):
    school_id: UUID
    student_id: UUID
    invoice_id: Optional[UUID] = None
    amount: float
    payment_method: Literal["mpesa", "bank", "cash", "card"]
    transaction_reference: Optional[str] = None
    paid_at: Optional[date] = None


# Timetable
class CreateTimetableRequest(BaseModel):
    school_id: UUID
    academic_year_id: UUID
    term_id: Optional[UUID] = None
    name: str


class CreateTimetableSlotRequest(BaseModel):
    timetable_id: UUID
    class_stream_id: UUID
    subject_id: UUID
    teacher_id: Optional[UUID] = None
    day_of_week: int  # 1=Monday ... 5=Friday
    start_time: time
    end_time: time
    room: Optional[str] = None


# Teacher
class CreateTeacherRequest(BaseModel):
    school_id: UUID
    first_name: str
    last_name: str
    tsc_number: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    hire_date: Optional[date] = None


class CreateTeachingAllocationRequest(BaseModel):
    school_id: UUID
    academic_year_id: UUID
    term_id: Optional[UUID] = None
    teacher_id: UUID
    class_stream_id: UUID
    subject_id: UUID


# LMS
class CreateCourseRequest(BaseModel):
    school_id: UUID
    class_stream_id: UUID
    subject_id: UUID
    teacher_id: UUID
    title: str
    description: Optional[str] = None


class CreateAssignmentRequest(BaseModel):
    course_id: UUID
    title: str
    instructions: Optional[str] = None
    due_at: Optional[datetime] = None
    max_score: Optional[float] = None


class CreateSubmissionRequest(BaseModel):
    assignment_id: UUID
    student_id: UUID
    content: Optional[str] = None
    file_url: Optional[str] = None


# Discipline
class CreateDisciplineIncidentRequest(BaseModel):
    school_id: UUID
    student_id: UUID
    incident_date: date
    incident_type: str
    severity: Optional[Literal["low", "medium", "high", "critical"]] = None
    description: Optional[str] = None


class CreateDisciplineActionRequest(BaseModel):
    incident_id: UUID
    action_type: str
    action_details: Optional[str] = None
    action_date: date


# Health
class CreateHealthRecordRequest(BaseModel):
    school_id: UUID
    student_id: UUID
    blood_group: Optional[str] = None
    allergies: Optional[str] = None
    chronic_conditions: Optional[str] = None
    emergency_contact_name: Optional[str] = None
    emergency_contact_phone: Optional[str] = None


class CreateClinicVisitRequest(BaseModel):
    school_id: UUID
    student_id: UUID
    visit_at: datetime
    complaint: Optional[str] = None
    diagnosis: Optional[str] = None
    treatment: Optional[str] = None


# Library
class CreateLibraryBookRequest(BaseModel):
    school_id: UUID
    isbn: Optional[str] = None
    title: str
    author: Optional[str] = None
    subject: Optional[str] = None
    copy_count: int = 1


class CreateLibraryLoanRequest(BaseModel):
    school_id: UUID
    book_id: UUID
    student_id: UUID
    due_date: date


# Transport
class CreateTransportRouteRequest(BaseModel):
    school_id: UUID
    route_code: str
    route_name: str
    description: Optional[str] = None


class CreateTransportAssignmentRequest(BaseModel):
    school_id: UUID
    route_id: UUID
    student_id: UUID
    pickup_point: Optional[str] = None


# Boarding
class CreateDormitoryRequest(BaseModel):
    school_id: UUID
    dorm_code: str
    dorm_name: str
    capacity: int
    gender: Optional[Literal["male", "female", "mixed"]] = None


class CreateBoardingAllocationRequest(BaseModel):
    school_id: UUID
    dormitory_id: UUID
    student_id: UUID
    academic_year_id: UUID
    bed_number: Optional[str] = None
