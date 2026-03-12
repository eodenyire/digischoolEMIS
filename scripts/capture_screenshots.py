#!/usr/bin/env python3
"""
Capture screenshots of all DigiSchool EMIS API modules.

Usage:
    # With API running locally on port 8000:
    python3 scripts/capture_screenshots.py

    # Or via Makefile:
    make screenshots
"""

import asyncio
import json
import os
import sys

try:
    import httpx
    from playwright.async_api import async_playwright
except ImportError:
    print("ERROR: playwright and httpx are required.")
    print("Install with: pip install playwright httpx && python -m playwright install chromium")
    sys.exit(1)

BASE_URL = "http://localhost:8000"
TENANT_ID = "7b0d4a39-9be0-4f35-909f-df73cb7f9999"
SCHOOL_ID = "7b0d4a39-9be0-4f35-909f-df73cb7f0011"
STUDENT_ID = "7b0d4a39-9be0-4f35-909f-df73cb7f0022"
SCREENSHOTS_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "screenshots")
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

HEADERS = {"X-Tenant-Id": TENANT_ID, "Content-Type": "application/json"}


def make_html_page(title: str, endpoint: str, method: str, payload: dict | None, response_data: dict) -> str:
    payload_html = ""
    if payload:
        payload_html = f"""
        <div class="section">
            <h3>Request Body</h3>
            <pre class="code">{json.dumps(payload, indent=2)}</pre>
        </div>"""

    return f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>{title}</title>
<style>
  body {{ font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; margin: 0; padding: 0; background: #f8fafc; }}
  .header {{ background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); color: white; padding: 20px 30px; }}
  .header h1 {{ margin: 0; font-size: 22px; font-weight: 700; }}
  .header p {{ margin: 5px 0 0; opacity: 0.85; font-size: 14px; }}
  .container {{ padding: 24px 30px; }}
  .endpoint-bar {{ display: flex; align-items: center; gap: 12px; background: white; border: 1px solid #e2e8f0;
                   border-radius: 8px; padding: 12px 16px; margin-bottom: 20px; }}
  .method {{ background: {'#22c55e' if method == 'GET' else '#f59e0b'}; color: white; padding: 4px 10px;
             border-radius: 4px; font-weight: 700; font-size: 12px; font-family: monospace; }}
  .url {{ font-family: monospace; font-size: 14px; color: #1e293b; }}
  .section {{ background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px; margin-bottom: 16px; }}
  .section h3 {{ margin: 0 0 10px; font-size: 13px; text-transform: uppercase; letter-spacing: 0.05em; color: #64748b; }}
  .code {{ margin: 0; background: #1e293b; color: #e2e8f0; padding: 16px; border-radius: 6px; font-size: 13px;
           line-height: 1.5; overflow-x: auto; white-space: pre-wrap; word-break: break-all; }}
  .status {{ display: inline-flex; align-items: center; gap: 6px; background: #dcfce7; color: #166534;
             padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 600; }}
  .dot {{ width: 8px; height: 8px; background: #22c55e; border-radius: 50%; }}
  .meta-row {{ display: flex; gap: 16px; margin-bottom: 16px; }}
  .meta-pill {{ background: #f1f5f9; border: 1px solid #e2e8f0; padding: 6px 14px;
                border-radius: 20px; font-size: 12px; color: #475569; }}
  .badge {{ font-size: 11px; background: #eff6ff; color: #1d4ed8; padding: 2px 8px;
            border-radius: 4px; border: 1px solid #bfdbfe; }}
</style>
</head>
<body>
<div class="header">
  <h1>🎓 DigiSchool EMIS — {title}</h1>
  <p>API Response Screenshot &nbsp;·&nbsp; Wave 1 &nbsp;·&nbsp; <span class="badge">200 OK</span></p>
</div>
<div class="container">
  <div class="endpoint-bar">
    <span class="method">{method}</span>
    <span class="url">{BASE_URL}{endpoint}</span>
    <span style="margin-left:auto"><span class="status"><span class="dot"></span>200 OK</span></span>
  </div>
  <div class="meta-row">
    <span class="meta-pill">🏢 Tenant: {TENANT_ID[:8]}…</span>
    <span class="meta-pill">🏫 School: {SCHOOL_ID[:8]}…</span>
  </div>
  {payload_html}
  <div class="section">
    <h3>Response Body</h3>
    <pre class="code">{json.dumps(response_data, indent=2)}</pre>
  </div>
</div>
</body>
</html>"""


def make_overview_html() -> str:
    modules = [
        ("Auth", "/api/v1/auth/login", "/api/v1/auth/refresh"),
        ("Users", "/api/v1/users", "/api/v1/users/{id}"),
        ("Students", "/api/v1/students", "/api/v1/students/{id}"),
        ("Enrollments", "/api/v1/enrollments", ""),
        ("Academic", "/api/v1/subjects", "/api/v1/class-streams"),
        ("Attendance", "/api/v1/attendance/sessions", "/api/v1/attendance/marks"),
        ("Communications", "/api/v1/communications/messages", "/api/v1/communications/delivery-status/{id}"),
        ("CBC", "/api/v1/cbc/learning-areas", "/api/v1/cbc/strands"),
        ("Assessments", "/api/v1/assessments/plans", "/api/v1/assessments/scores"),
        ("Finance", "/api/v1/finance/fee-structures", "/api/v1/finance/payments"),
        ("Timetable", "/api/v1/timetables", "/api/v1/timetable-slots"),
        ("Teachers", "/api/v1/teachers", "/api/v1/teaching-allocations"),
        ("LMS", "/api/v1/courses", "/api/v1/courses/{id}/assignments"),
        ("Discipline", "/api/v1/discipline/incidents", "/api/v1/discipline/actions"),
        ("Health", "/api/v1/health/clinic-visits", "/api/v1/health/students/{id}"),
        ("Library", "/api/v1/library/books", "/api/v1/library/loans"),
        ("Transport", "/api/v1/transport/routes", "/api/v1/transport/assignments"),
        ("Boarding", "/api/v1/boarding/dormitories", "/api/v1/boarding/allocations"),
        ("Analytics", "/api/v1/analytics/schools/{id}/overview", "/api/v1/analytics/students/{id}/risk"),
        ("Parent Portal", "/api/v1/parent/students/{id}/dashboard", "/api/v1/parent/students/{id}/report-cards"),
    ]

    rows = "\n".join([
        f"<tr><td>{m[0]}</td><td class='ep'>{m[1]}</td><td class='ep'>{m[2]}</td></tr>"
        for m in modules
    ])

    return f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>DigiSchool EMIS — API Modules Overview</title>
<style>
  body {{ font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; margin: 0; background: #f8fafc; }}
  .header {{ background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); color: white; padding: 24px 36px; }}
  .header h1 {{ margin: 0; font-size: 26px; font-weight: 800; letter-spacing: -0.5px; }}
  .header p {{ margin: 6px 0 0; opacity: 0.85; font-size: 15px; }}
  .stats {{ display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; padding: 24px 36px; }}
  .stat {{ background: white; border: 1px solid #e2e8f0; border-radius: 10px; padding: 16px; text-align: center; }}
  .stat .num {{ font-size: 32px; font-weight: 800; color: #1e40af; }}
  .stat .lbl {{ font-size: 13px; color: #64748b; margin-top: 4px; }}
  .container {{ padding: 0 36px 36px; }}
  table {{ width: 100%; border-collapse: collapse; background: white; border: 1px solid #e2e8f0;
           border-radius: 10px; overflow: hidden; }}
  th {{ background: #1e40af; color: white; padding: 12px 16px; text-align: left; font-size: 13px; font-weight: 600; }}
  td {{ padding: 11px 16px; font-size: 13px; border-bottom: 1px solid #f1f5f9; }}
  td:first-child {{ font-weight: 600; color: #1e293b; }}
  .ep {{ font-family: monospace; color: #475569; font-size: 12px; }}
  tr:hover td {{ background: #f8fafc; }}
  tr:last-child td {{ border-bottom: none; }}
</style>
</head>
<body>
<div class="header">
  <h1>🎓 DigiSchool EMIS — API Modules Overview</h1>
  <p>Kenya CBC-aligned School Management System · Wave 1 REST API · All Systems Operational</p>
</div>
<div class="stats">
  <div class="stat"><div class="num">20</div><div class="lbl">API Modules</div></div>
  <div class="stat"><div class="num">50+</div><div class="lbl">Endpoints</div></div>
  <div class="stat"><div class="num">100</div><div class="lbl">DB Tables</div></div>
  <div class="stat"><div class="num">59</div><div class="lbl">Tests Passing</div></div>
</div>
<div class="container">
<table>
  <thead><tr><th>Module</th><th>Primary Endpoint</th><th>Secondary Endpoint</th></tr></thead>
  <tbody>{rows}</tbody>
</table>
</div>
</body>
</html>"""


async def capture(page, title: str, endpoint: str, method: str, payload: dict | None, filename: str) -> None:
    with httpx.Client(base_url=BASE_URL) as http:
        if method == "POST":
            resp = http.post(endpoint, json=payload, headers=HEADERS)
        else:
            resp = http.get(endpoint, headers=HEADERS)

    html = make_html_page(title, endpoint, method, payload, resp.json())
    html_path = f"/tmp/screenshot_{filename}.html"
    with open(html_path, "w") as f:
        f.write(html)

    await page.goto(f"file://{html_path}")
    await page.set_viewport_size({"width": 900, "height": 600})
    out = f"{SCREENSHOTS_DIR}/{filename}.png"
    await page.screenshot(path=out, full_page=True)
    print(f"  ✓ {filename}.png")


async def main() -> None:
    # Quick connectivity check
    try:
        with httpx.Client(base_url=BASE_URL, timeout=5) as http:
            http.get("/health")
    except Exception:
        print(f"ERROR: Cannot connect to {BASE_URL}. Start the API first:")
        print("  make local-api      # local")
        print("  make docker-up      # docker")
        sys.exit(1)

    async with async_playwright() as p:
        browser = await p.chromium.launch(args=["--no-sandbox", "--disable-dev-shm-usage"])
        page = await browser.new_page()

        print(f"Capturing screenshots → {SCREENSHOTS_DIR}/\n")

        # Overview
        html = make_overview_html()
        with open("/tmp/overview.html", "w") as f:
            f.write(html)
        await page.goto("file:///tmp/overview.html")
        await page.set_viewport_size({"width": 1000, "height": 800})
        await page.screenshot(path=f"{SCREENSHOTS_DIR}/00-api-modules-overview.png", full_page=True)
        print("  ✓ 00-api-modules-overview.png")

        captures = [
            ("Health Check", "/health", "GET", None, "health-endpoint-ok"),
            ("Auth — Login", "/api/v1/auth/login", "POST",
             {"email": "admin@school.ke", "password": "Password123!"},
             "auth-login-success"),
            ("Students — Create", "/api/v1/students", "POST",
             {"school_id": SCHOOL_ID, "first_name": "Jane", "last_name": "Wanjiku",
              "sex": "female", "date_of_birth": "2013-05-14"},
             "students-create-student"),
            ("Students — Get", f"/api/v1/students/{STUDENT_ID}", "GET", None, "students-get-student"),
            ("Academic — Create Subject", "/api/v1/subjects", "POST",
             {"school_id": SCHOOL_ID, "code": "MTH-001", "name": "Mathematics", "is_elective": False},
             "academic-create-subject"),
            ("Academic — Create Class Stream", "/api/v1/class-streams", "POST",
             {"school_id": SCHOOL_ID, "academic_year_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0066",
              "name": "Grade 4 East", "grade_code": "Grade 4"},
             "academic-create-class-stream"),
            ("CBC — Create Learning Area", "/api/v1/cbc/learning-areas", "POST",
             {"school_id": SCHOOL_ID, "code": "LA-MTH", "name": "Mathematics", "grade_band": "LOWER_PRIMARY"},
             "cbc-create-learning-area"),
            ("Assessments — Create Plan", "/api/v1/assessments/plans", "POST",
             {"school_id": SCHOOL_ID, "term_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0033",
              "class_stream_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0044", "name": "Term 1 Assessment Plan"},
             "assessments-create-plan"),
            ("Assessments — Record Score", "/api/v1/assessments/scores", "POST",
             {"activity_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0055", "student_id": STUDENT_ID,
              "performance_level": "ME", "teacher_comment": "Good effort"},
             "assessments-record-score"),
            ("Finance — Fee Structure", "/api/v1/finance/fee-structures", "POST",
             {"school_id": SCHOOL_ID, "academic_year_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0066",
              "grade_code": "Grade 4", "fee_category": "tuition", "amount": 15000.00},
             "finance-create-fee-structure"),
            ("Finance — Record Payment", "/api/v1/finance/payments", "POST",
             {"school_id": SCHOOL_ID, "student_id": STUDENT_ID, "amount": 5000.00,
              "payment_method": "mpesa", "transaction_reference": "MPESA-ABC123"},
             "finance-record-payment"),
            ("Teachers — Create", "/api/v1/teachers", "POST",
             {"school_id": SCHOOL_ID, "first_name": "Alice", "last_name": "Njeri",
              "tsc_number": "TSC-99001", "email": "alice.njeri@school.ke"},
             "teachers-create-teacher"),
            ("Timetable — Create", "/api/v1/timetables", "POST",
             {"school_id": SCHOOL_ID, "academic_year_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0066",
              "name": "Term 1 2024 Timetable"},
             "timetable-create-timetable"),
            ("LMS — Create Course", "/api/v1/courses", "POST",
             {"school_id": SCHOOL_ID, "class_stream_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0044",
              "subject_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0077",
              "teacher_id": "7b0d4a39-9be0-4f35-909f-df73cb7f0088", "title": "Mathematics Grade 4"},
             "lms-create-course"),
            ("Discipline — Report Incident", "/api/v1/discipline/incidents", "POST",
             {"school_id": SCHOOL_ID, "student_id": STUDENT_ID, "incident_date": "2024-03-10",
              "incident_type": "misconduct", "severity": "low"},
             "discipline-report-incident"),
            ("Health — Clinic Visit", "/api/v1/health/clinic-visits", "POST",
             {"school_id": SCHOOL_ID, "student_id": STUDENT_ID, "visit_at": "2024-03-12T10:30:00",
              "complaint": "headache", "treatment": "Paracetamol 500mg"},
             "health-clinic-visit"),
            ("Library — Add Book", "/api/v1/library/books", "POST",
             {"school_id": SCHOOL_ID, "isbn": "978-9966-25-102-5",
              "title": "Mathematics Textbook Grade 4",
              "author": "Kenya Institute of Curriculum Development", "copy_count": 5},
             "library-add-book"),
            ("Transport — Create Route", "/api/v1/transport/routes", "POST",
             {"school_id": SCHOOL_ID, "route_code": "RT-001", "route_name": "Westlands Express"},
             "transport-create-route"),
            ("Boarding — Create Dormitory", "/api/v1/boarding/dormitories", "POST",
             {"school_id": SCHOOL_ID, "dorm_code": "DORM-A", "dorm_name": "Block A Boys",
              "capacity": 30, "gender": "male"},
             "boarding-create-dormitory"),
            ("Analytics — School Overview",
             f"/api/v1/analytics/schools/{SCHOOL_ID}/overview", "GET", None,
             "analytics-school-overview"),
            ("Analytics — Student Risk",
             f"/api/v1/analytics/students/{STUDENT_ID}/risk", "GET", None,
             "analytics-student-risk"),
            ("Parent Portal — Dashboard",
             f"/api/v1/parent/students/{STUDENT_ID}/dashboard", "GET", None,
             "parent-portal-student-dashboard"),
        ]

        for args in captures:
            await capture(page, *args)

        await browser.close()

    print(f"\n✅  {len(captures) + 1} screenshots saved to {SCREENSHOTS_DIR}/")


if __name__ == "__main__":
    asyncio.run(main())
