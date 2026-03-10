# DigiSchool EMIS

**World-Class School Management System for Kenya CBC (Grade 1 to Grade 13)**

DigiSchool EMIS is a comprehensive, production-ready School Management System built using Java, designed to fully support the **Kenya Competency-Based Curriculum (CBC)** from Grade 1 through Grade 13 (Junior Secondary School through Senior Secondary School).

---

## 🏫 Overview

DigiSchool EMIS is modeled after global platforms like **PowerSchool**, **Infinite Campus**, and **Skyward** — providing a complete education ERP ecosystem with **20+ integrated modules**.

**Technology Stack:**
- **Java 17** — Desktop application runtime
- **JavaFX 17** — Modern desktop UI framework
- **PostgreSQL / MySQL** — Database (both supported)
- **Hibernate ORM** — Object-relational mapping
- **HikariCP** — High-performance connection pooling
- **Flyway** — Database versioning and migrations
- **Maven** — Build and dependency management

---

## 📦 Modules (20+)

### Core Modules

| # | Module | Description |
|---|--------|-------------|
| 1 | **Student Information System (SIS)** | Student registration, profiles, enrollment, history |
| 2 | **Academic Management** | Curriculum, subjects, classes, grade levels |
| 3 | **CBC Competency Tracking Engine** | Strands, sub-strands, learning outcomes, mastery levels |
| 4 | **Assessment & Grading** | CBC continuous assessments, quizzes, exams, report cards |
| 5 | **Attendance Management** | Daily attendance, absentee alerts, parent notifications |
| 6 | **Finance & Fee Management** | Invoices, M-Pesa/bank payments, scholarships |
| 7 | **Teacher Management** | Profiles, qualifications, lesson plans, evaluations |
| 8 | **Learning Management System (LMS)** | Course materials, assignments, forums |
| 9 | **Timetable & Scheduling** | Class/exam timetables, room allocation |
| 10 | **Communication System** | SMS, email, announcements, messaging |
| 11 | **Parent Portal** | Grades, attendance, fees, homework visibility |

### Advanced Modules

| # | Module | Description |
|---|--------|-------------|
| 12 | **Analytics & AI Predictions** | Performance analytics, dropout risk detection |
| 13 | **Library Management** | Book catalog, borrowing, digital library |
| 14 | **Transport Management** | Bus routes, student assignments, driver management |
| 15 | **Boarding/Hostel Management** | Dormitory allocation, meal plans |
| 16 | **Health & Medical Records** | Allergies, clinic visits, vaccinations |
| 17 | **Discipline & Behavior Management** | Incident reporting, disciplinary actions |
| 18 | **Document & Certificate Management** | Report cards, transcripts, student IDs |
| 19 | **Alumni Management** | Alumni profiles, events, donations |
| 20 | **System Administration & Security** | Roles, permissions, audit logs |

---

## 🏗️ Project Structure

```
digischool-emis/
├── src/
│   ├── main/
│   │   ├── java/com/digischool/emis/
│   │   │   ├── DigiSchoolApp.java          # Main entry point
│   │   │   ├── config/                     # DB config, Flyway
│   │   │   ├── model/                      # Entity classes (20+ modules)
│   │   │   │   ├── student/                # Student, Guardian, Enrollment
│   │   │   │   ├── academic/               # School, Class, Subject, CBC Strands
│   │   │   │   ├── assessment/             # Assessment, Results, Report Cards
│   │   │   │   ├── attendance/             # Attendance records
│   │   │   │   ├── finance/                # Invoices, Payments
│   │   │   │   ├── teacher/                # Teacher, Qualifications
│   │   │   │   ├── communication/          # Announcements, Messages
│   │   │   │   ├── library/                # Books, Borrowings
│   │   │   │   └── health/                 # Medical records, Clinic visits
│   │   │   ├── dao/                        # Data Access Layer (JDBC)
│   │   │   │   └── impl/                  # JDBC implementations
│   │   │   ├── service/                    # Business logic layer
│   │   │   │   └── impl/                  # Service implementations
│   │   │   └── ui/                         # JavaFX controllers
│   │   │       └── controllers/            # FXML controllers
│   │   └── resources/
│   │       ├── application.properties      # App configuration
│   │       ├── logback.xml                 # Logging configuration
│   │       ├── fxml/                       # JavaFX FXML views
│   │       ├── css/                        # Application styles
│   │       └── db/migration/               # Flyway SQL migrations (85+ tables)
│   └── test/
│       └── java/com/digischool/emis/
│           ├── StudentServiceTest.java     # Service unit tests (15 tests)
│           └── ModelEntityTest.java        # Model unit tests (18 tests)
└── pom.xml                                 # Maven build config
```

---

## 🗄️ Database Schema

The database schema (`V1__Create_Complete_Schema.sql`) contains **85+ tables** covering all 20 modules:

- **System**: roles, permissions, users, user_roles, audit_logs, system_settings
- **School**: schools, academic_years, terms, school_houses
- **Students**: students, guardians, student_enrollments, student_documents
- **Academic**: grade_levels, classes, learning_areas, subjects
- **CBC**: cbc_strands, cbc_sub_strands, cbc_learning_outcomes, cbc_core_competencies, cbc_values
- **Assessments**: assessments, assessment_results, report_cards, grading_scales
- **Attendance**: attendance_sessions, student_attendance, teacher_attendance
- **Finance**: fee_structures, student_invoices, fee_payments, scholarships
- **Teachers**: teachers, teacher_qualifications, lesson_plans, teacher_evaluations
- **LMS**: courses, course_topics, learning_materials, assignments, forums
- **Timetable**: time_slots, timetables, timetable_entries, exam_schedules
- **Communication**: announcements, messages, notifications, sms_logs
- **Library**: library_books, library_borrowings, digital_resources
- **Transport**: vehicles, transport_routes, transport_stops
- **Boarding**: dormitories, dormitory_rooms, student_boarding_allocations
- **Health**: student_medical_records, clinic_visits, vaccination_records
- **Discipline**: discipline_incidents, disciplinary_actions, behavior_records
- **Documents**: document_templates, generated_documents
- **Alumni**: alumni, alumni_events, alumni_donations

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+ or MySQL 8+

### Database Setup

**PostgreSQL:**
```sql
CREATE DATABASE digischool_emis;
```

**MySQL:**
```sql
CREATE DATABASE digischool_emis;
```

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Select: POSTGRESQL or MYSQL
db.type=POSTGRESQL

db.postgresql.host=localhost
db.postgresql.port=5432
db.postgresql.name=digischool_emis
db.postgresql.username=postgres
db.postgresql.password=your_password
```

### Build and Run

```bash
# Build
mvn clean compile

# Run tests (33 tests)
mvn test

# Run application
mvn javafx:run

# Build JAR
mvn clean package
java -jar target/digischool-emis-1.0.0-SNAPSHOT.jar
```

---

## 🇰🇪 Kenya CBC Support

The system fully supports the **Kenya Competency-Based Curriculum (CBC)**:

| Feature | Details |
|---------|---------|
| **Grade Range** | Grade 1 to Grade 13 |
| **Grade Categories** | Lower Primary (1-3), Upper Primary (4-6), JSS (7-9), SS (10-13) |
| **Core Competencies** | 7 competencies: Communication, Critical Thinking, Creativity, Citizenship, Digital Literacy, Learning to Learn, Self-Efficacy |
| **CBC Values** | Love, Responsibility, Respect, Unity, Peace, Patriotism, Social Justice |
| **Mastery Levels** | Exceeds, Meets, Approaches, Below Expectations |
| **NEMIS Integration** | NEMIS ID tracking for all students |
| **Payment Methods** | M-Pesa, Bank Transfer, Cash, Cheque |
| **Counties** | Kenya county/sub-county/ward structure |

---

## 🔐 Demo Login

When no database is configured, the application runs in demo mode:
- **Username**: `admin`
- **Password**: `Admin@123`

---

## 🧪 Tests

```bash
mvn test
```

- 33 unit tests covering student service and model entity behavior
- Tests run without requiring a database connection

