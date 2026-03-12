from fastapi import FastAPI

from app.routers import (
    academic,
    analytics,
    assessments,
    attendance,
    auth,
    boarding,
    cbc,
    communications,
    discipline,
    enrollments,
    finance,
    health_service,
    library,
    lms,
    parent,
    students,
    teachers,
    timetable,
    transport,
    users,
)

app = FastAPI(title="digischoolEMIS Wave 1 Stubs", version="1.0.0")

app.include_router(auth.router, prefix="/api/v1/auth", tags=["Auth"])
app.include_router(users.router, prefix="/api/v1", tags=["Users"])
app.include_router(students.router, prefix="/api/v1", tags=["Students"])
app.include_router(enrollments.router, prefix="/api/v1", tags=["Enrollments"])
app.include_router(academic.router, prefix="/api/v1", tags=["Academic"])
app.include_router(attendance.router, prefix="/api/v1", tags=["Attendance"])
app.include_router(communications.router, prefix="/api/v1", tags=["Communications"])
app.include_router(cbc.router, prefix="/api/v1", tags=["CBC"])
app.include_router(assessments.router, prefix="/api/v1", tags=["Assessments"])
app.include_router(finance.router, prefix="/api/v1", tags=["Finance"])
app.include_router(timetable.router, prefix="/api/v1", tags=["Timetable"])
app.include_router(teachers.router, prefix="/api/v1", tags=["Teachers"])
app.include_router(lms.router, prefix="/api/v1", tags=["LMS"])
app.include_router(discipline.router, prefix="/api/v1", tags=["Discipline"])
app.include_router(health_service.router, prefix="/api/v1", tags=["Health"])
app.include_router(library.router, prefix="/api/v1", tags=["Library"])
app.include_router(transport.router, prefix="/api/v1", tags=["Transport"])
app.include_router(boarding.router, prefix="/api/v1", tags=["Boarding"])
app.include_router(analytics.router, prefix="/api/v1", tags=["Analytics"])
app.include_router(parent.router, prefix="/api/v1", tags=["Parent Portal"])


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}
