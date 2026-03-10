from fastapi import FastAPI

from app.routers import academic, attendance, auth, communications, enrollments, students, users

app = FastAPI(title="digischoolEMIS Wave 1 Stubs", version="1.0.0")

app.include_router(auth.router, prefix="/api/v1/auth", tags=["Auth"])
app.include_router(users.router, prefix="/api/v1", tags=["Users"])
app.include_router(students.router, prefix="/api/v1", tags=["Students"])
app.include_router(enrollments.router, prefix="/api/v1", tags=["Enrollments"])
app.include_router(academic.router, prefix="/api/v1", tags=["Academic"])
app.include_router(attendance.router, prefix="/api/v1", tags=["Attendance"])
app.include_router(communications.router, prefix="/api/v1", tags=["Communications"])


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}
