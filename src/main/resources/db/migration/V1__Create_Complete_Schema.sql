-- DigiSchool EMIS - Complete Database Schema
-- Version: 1.0.0
-- Supports: Kenya CBC System Grade 1 to Grade 13
-- Compatible with: PostgreSQL and MySQL
-- 
-- Module 1: System Administration & Security
-- Module 2: School Information
-- Module 3: Student Information System (SIS)
-- Module 4: Academic Management
-- Module 5: CBC Competency Tracking Engine
-- Module 6: Assessment & Grading
-- Module 7: Attendance Management
-- Module 8: Finance & Fee Management
-- Module 9: Teacher Management
-- Module 10: Learning Management System (LMS)
-- Module 11: Timetable & Scheduling
-- Module 12: Communication System
-- Module 13: Parent Portal
-- Module 14: Analytics & AI
-- Module 15: Library Management
-- Module 16: Transport Management
-- Module 17: Boarding/Hostel Management
-- Module 18: Health & Medical Records
-- Module 19: Discipline & Behavior Management
-- Module 20: Document & Certificate Management
-- Module 21: Alumni Management

-- ============================================================
-- MODULE 1: SYSTEM ADMINISTRATION & SECURITY
-- ============================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    module VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    failed_login_attempts INT DEFAULT 0,
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    must_change_password BOOLEAN DEFAULT FALSE,
    profile_picture_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    module VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(200) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(500) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    device_info VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 2: SCHOOL INFORMATION
-- ============================================================

CREATE TABLE IF NOT EXISTS schools (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    knec_code VARCHAR(50) UNIQUE,
    nemis_code VARCHAR(50) UNIQUE,
    registration_number VARCHAR(100) UNIQUE,
    school_type VARCHAR(50) NOT NULL, -- PUBLIC, PRIVATE, MISSION, INTERNATIONAL
    school_level VARCHAR(50) NOT NULL, -- PRIMARY, SECONDARY, MIXED
    curriculum_type VARCHAR(50) DEFAULT 'CBC', -- CBC, IGCSE, IB
    email VARCHAR(255),
    phone VARCHAR(20),
    fax VARCHAR(20),
    website VARCHAR(255),
    physical_address TEXT,
    postal_address VARCHAR(200),
    county VARCHAR(100),
    sub_county VARCHAR(100),
    ward VARCHAR(100),
    logo_url VARCHAR(500),
    established_year INT,
    motto VARCHAR(500),
    vision TEXT,
    mission TEXT,
    principal_name VARCHAR(255),
    is_boarding BOOLEAN DEFAULT FALSE,
    is_day_school BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS academic_years (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, COMPLETED, UPCOMING
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    UNIQUE (school_id, name)
);

CREATE TABLE IF NOT EXISTS terms (
    id BIGSERIAL PRIMARY KEY,
    academic_year_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    term_number INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS school_houses (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    motto VARCHAR(255),
    house_master_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 3: STUDENT INFORMATION SYSTEM (SIS)
-- ============================================================

CREATE TABLE IF NOT EXISTS grade_levels (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL, -- Grade 1, Grade 2, ... Grade 13
    level_number INT NOT NULL,
    category VARCHAR(50), -- LOWER_PRIMARY, UPPER_PRIMARY, JSS, SS
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    UNIQUE (school_id, level_number)
);

CREATE TABLE IF NOT EXISTS classes (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    grade_level_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    section VARCHAR(10),
    capacity INT DEFAULT 45,
    class_teacher_id BIGINT,
    room_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,
    school_id BIGINT NOT NULL,
    admission_number VARCHAR(50) NOT NULL,
    nemis_id VARCHAR(50) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL, -- MALE, FEMALE, OTHER
    nationality VARCHAR(100) DEFAULT 'Kenyan',
    national_id VARCHAR(50),
    birth_certificate_number VARCHAR(100),
    religion VARCHAR(100),
    ethnicity VARCHAR(100),
    blood_group VARCHAR(10),
    home_language VARCHAR(100),
    profile_picture_url VARCHAR(500),
    admission_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    house_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (house_id) REFERENCES school_houses(id),
    UNIQUE (school_id, admission_number)
);

CREATE TABLE IF NOT EXISTS student_enrollments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    exit_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, TRANSFERRED, COMPLETED, WITHDRAWN
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

CREATE TABLE IF NOT EXISTS guardians (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL, -- FATHER, MOTHER, GUARDIAN, SIBLING
    national_id VARCHAR(50),
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(255),
    occupation VARCHAR(200),
    employer VARCHAR(200),
    physical_address TEXT,
    postal_address VARCHAR(200),
    is_emergency_contact BOOLEAN DEFAULT FALSE,
    is_fee_payer BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS student_guardians (
    student_id BIGINT NOT NULL,
    guardian_id BIGINT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, guardian_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_documents (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    document_number VARCHAR(200),
    document_url VARCHAR(500),
    description TEXT,
    uploaded_by BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_transfers (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    from_school_id BIGINT,
    to_school_id BIGINT,
    transfer_date DATE NOT NULL,
    reason TEXT,
    transfer_letter_url VARCHAR(500),
    approved_by BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (from_school_id) REFERENCES schools(id),
    FOREIGN KEY (to_school_id) REFERENCES schools(id)
);

-- ============================================================
-- MODULE 4: ACADEMIC MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS learning_areas (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    grade_level_category VARCHAR(50), -- LOWER_PRIMARY, UPPER_PRIMARY, JSS, SS
    is_compulsory BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGSERIAL PRIMARY KEY,
    learning_area_id BIGINT NOT NULL,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    is_elective BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (learning_area_id) REFERENCES learning_areas(id),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grade_subjects (
    id BIGSERIAL PRIMARY KEY,
    grade_level_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    is_compulsory BOOLEAN DEFAULT TRUE,
    weekly_periods INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    UNIQUE (grade_level_id, subject_id)
);

-- ============================================================
-- MODULE 5: CBC COMPETENCY TRACKING ENGINE
-- ============================================================

CREATE TABLE IF NOT EXISTS cbc_strands (
    id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    grade_level_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id)
);

CREATE TABLE IF NOT EXISTS cbc_sub_strands (
    id BIGSERIAL PRIMARY KEY,
    strand_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (strand_id) REFERENCES cbc_strands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cbc_learning_outcomes (
    id BIGSERIAL PRIMARY KEY,
    sub_strand_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    outcome_code VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sub_strand_id) REFERENCES cbc_sub_strands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cbc_core_competencies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    category VARCHAR(100), -- COGNITIVE, SOCIAL, CREATIVE, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cbc_values (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cbc_pertinent_issues (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student_competency_assessments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    competency_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    mastery_level VARCHAR(20) NOT NULL, -- EXCEEDS, MEETS, APPROACHES, BELOW
    score DECIMAL(5,2),
    teacher_remarks TEXT,
    assessed_by BIGINT NOT NULL,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (competency_id) REFERENCES cbc_core_competencies(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    FOREIGN KEY (assessed_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS student_strand_assessments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    sub_strand_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    mastery_level VARCHAR(20) NOT NULL,
    score DECIMAL(5,2),
    teacher_remarks TEXT,
    assessed_by BIGINT NOT NULL,
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (sub_strand_id) REFERENCES cbc_sub_strands(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    FOREIGN KEY (assessed_by) REFERENCES users(id)
);

-- ============================================================
-- MODULE 6: ASSESSMENT & GRADING SYSTEM
-- ============================================================

CREATE TABLE IF NOT EXISTS assessment_types (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    assessment_category VARCHAR(50) NOT NULL, -- FORMATIVE, SUMMATIVE, PRACTICAL, PROJECT, PORTFOLIO
    weight_percentage DECIMAL(5,2),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grading_scales (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    grade_system VARCHAR(20) NOT NULL, -- CBC, KCSE, PERCENTAGE, LETTER
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grading_scale_entries (
    id BIGSERIAL PRIMARY KEY,
    grading_scale_id BIGINT NOT NULL,
    grade_label VARCHAR(20) NOT NULL,
    min_score DECIMAL(5,2) NOT NULL,
    max_score DECIMAL(5,2) NOT NULL,
    grade_points DECIMAL(5,2),
    description VARCHAR(200),
    color_code VARCHAR(10),
    FOREIGN KEY (grading_scale_id) REFERENCES grading_scales(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assessments (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    assessment_type_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    total_marks DECIMAL(6,2) NOT NULL,
    assessment_date DATE,
    due_date DATE,
    submission_deadline TIMESTAMP,
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    FOREIGN KEY (assessment_type_id) REFERENCES assessment_types(id)
);

CREATE TABLE IF NOT EXISTS assessment_results (
    id BIGSERIAL PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    marks_obtained DECIMAL(6,2),
    grade VARCHAR(20),
    grade_points DECIMAL(5,2),
    percentage DECIMAL(5,2),
    teacher_remarks TEXT,
    is_absent BOOLEAN DEFAULT FALSE,
    is_excused BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMP,
    marked_at TIMESTAMP,
    marked_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (marked_by) REFERENCES users(id),
    UNIQUE (assessment_id, student_id)
);

CREATE TABLE IF NOT EXISTS report_cards (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    overall_grade VARCHAR(20),
    overall_points DECIMAL(5,2),
    class_position INT,
    class_size INT,
    stream_position INT,
    overall_remarks TEXT,
    class_teacher_remarks TEXT,
    principal_remarks TEXT,
    parent_remarks TEXT,
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    UNIQUE (student_id, term_id)
);

CREATE TABLE IF NOT EXISTS report_card_subjects (
    id BIGSERIAL PRIMARY KEY,
    report_card_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    total_score DECIMAL(6,2),
    grade VARCHAR(20),
    grade_points DECIMAL(5,2),
    subject_position INT,
    teacher_remarks TEXT,
    FOREIGN KEY (report_card_id) REFERENCES report_cards(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- ============================================================
-- MODULE 7: ATTENDANCE MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS attendance_sessions (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT,
    teacher_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    period_number INT,
    start_time TIME,
    end_time TIME,
    session_type VARCHAR(20) DEFAULT 'CLASS', -- CLASS, EXAM, ACTIVITY
    is_finalized BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS student_attendance (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE, EXCUSED, SICK
    check_in_time TIME,
    minutes_late INT,
    remarks TEXT,
    recorded_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (recorded_by) REFERENCES users(id),
    UNIQUE (session_id, student_id)
);

CREATE TABLE IF NOT EXISTS teacher_attendance (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, ON_LEAVE, OFFICIAL_DUTY
    leave_type VARCHAR(50),
    remarks TEXT,
    recorded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    UNIQUE (teacher_id, attendance_date)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested INT,
    reason TEXT,
    supporting_document_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'PENDING',
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS absentee_notifications (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    guardian_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    notification_type VARCHAR(20) NOT NULL, -- SMS, EMAIL, PUSH
    message TEXT NOT NULL,
    sent_at TIMESTAMP,
    is_delivered BOOLEAN DEFAULT FALSE,
    delivery_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (guardian_id) REFERENCES guardians(id)
);

-- ============================================================
-- MODULE 8: FINANCE & FEE MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS fee_structures (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    grade_level_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_boarding BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id)
);

CREATE TABLE IF NOT EXISTS fee_items (
    id BIGSERIAL PRIMARY KEY,
    fee_structure_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(12,2) NOT NULL,
    is_compulsory BOOLEAN DEFAULT TRUE,
    fee_category VARCHAR(100), -- TUITION, BOARDING, ACTIVITY, UNIFORM, etc.
    term_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(id) ON DELETE CASCADE,
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

CREATE TABLE IF NOT EXISTS student_invoices (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    invoice_date DATE NOT NULL,
    due_date DATE,
    total_amount DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) DEFAULT 0,
    scholarship_amount DECIMAL(12,2) DEFAULT 0,
    net_amount DECIMAL(12,2) NOT NULL,
    amount_paid DECIMAL(12,2) DEFAULT 0,
    balance DECIMAL(12,2),
    status VARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PARTIAL, PAID, OVERDUE
    notes TEXT,
    generated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(id),
    FOREIGN KEY (term_id) REFERENCES terms(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

CREATE TABLE IF NOT EXISTS fee_payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    receipt_number VARCHAR(100) NOT NULL UNIQUE,
    payment_date DATE NOT NULL,
    amount_paid DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CASH, MPESA, BANK_TRANSFER, CHEQUE
    transaction_reference VARCHAR(200),
    mpesa_code VARCHAR(100),
    bank_name VARCHAR(200),
    cheque_number VARCHAR(100),
    received_by BIGINT,
    notes TEXT,
    is_reversed BOOLEAN DEFAULT FALSE,
    reversed_at TIMESTAMP,
    reversed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES student_invoices(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (received_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS scholarships (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED
    discount_value DECIMAL(10,2) NOT NULL,
    sponsor_name VARCHAR(255),
    applicable_items TEXT, -- JSON array of fee item categories
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_scholarships (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    scholarship_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    awarded_date DATE NOT NULL,
    notes TEXT,
    awarded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (scholarship_id) REFERENCES scholarships(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

-- ============================================================
-- MODULE 9: TEACHER MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS teachers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    school_id BIGINT NOT NULL,
    teacher_number VARCHAR(50) UNIQUE,
    tsc_number VARCHAR(50) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(10),
    nationality VARCHAR(100) DEFAULT 'Kenyan',
    national_id VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(255),
    physical_address TEXT,
    profile_picture_url VARCHAR(500),
    date_joined DATE,
    employment_type VARCHAR(50), -- PERMANENT, CONTRACT, VOLUNTEER
    department VARCHAR(100),
    designation VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    is_class_teacher BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teacher_qualifications (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    qualification_level VARCHAR(100) NOT NULL,
    institution_name VARCHAR(255) NOT NULL,
    field_of_study VARCHAR(255),
    start_year INT,
    end_year INT,
    certificate_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teacher_subject_assignments (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    UNIQUE (teacher_id, subject_id, class_id, academic_year_id)
);

CREATE TABLE IF NOT EXISTS teacher_evaluations (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    evaluated_by BIGINT NOT NULL,
    evaluation_date DATE NOT NULL,
    overall_score DECIMAL(5,2),
    teaching_effectiveness DECIMAL(5,2),
    professional_development DECIMAL(5,2),
    student_results_impact DECIMAL(5,2),
    classroom_management DECIMAL(5,2),
    punctuality_attendance DECIMAL(5,2),
    comments TEXT,
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    FOREIGN KEY (evaluated_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS lesson_plans (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    strand_id BIGINT,
    sub_strand_id BIGINT,
    term_id BIGINT NOT NULL,
    week_number INT NOT NULL,
    lesson_number INT,
    lesson_date DATE,
    duration_minutes INT DEFAULT 40,
    specific_objectives TEXT,
    materials_resources TEXT,
    lesson_activities TEXT,
    assessment_method TEXT,
    homework TEXT,
    teacher_notes TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, SUBMITTED, APPROVED, REVIEWED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (strand_id) REFERENCES cbc_strands(id),
    FOREIGN KEY (sub_strand_id) REFERENCES cbc_sub_strands(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

-- ============================================================
-- MODULE 10: LEARNING MANAGEMENT SYSTEM (LMS)
-- ============================================================

CREATE TABLE IF NOT EXISTS courses (
    id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

CREATE TABLE IF NOT EXISTS course_topics (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_position INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learning_materials (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    material_type VARCHAR(50) NOT NULL, -- PDF, VIDEO, AUDIO, IMAGE, LINK, DOCUMENT
    file_url VARCHAR(500),
    external_url VARCHAR(500),
    file_size_kb INT,
    duration_minutes INT,
    order_position INT,
    is_downloadable BOOLEAN DEFAULT TRUE,
    uploaded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES course_topics(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    topic_id BIGINT,
    assessment_id BIGINT,
    title VARCHAR(255) NOT NULL,
    instructions TEXT,
    attachment_url VARCHAR(500),
    max_score DECIMAL(6,2),
    due_date TIMESTAMP,
    allow_late_submission BOOLEAN DEFAULT FALSE,
    late_penalty_percentage DECIMAL(5,2),
    submission_type VARCHAR(50), -- TEXT, FILE, URL
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES course_topics(id),
    FOREIGN KEY (assessment_id) REFERENCES assessments(id)
);

CREATE TABLE IF NOT EXISTS assignment_submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    submission_text TEXT,
    file_url VARCHAR(500),
    external_url VARCHAR(500),
    submitted_at TIMESTAMP NOT NULL,
    is_late BOOLEAN DEFAULT FALSE,
    score DECIMAL(6,2),
    feedback TEXT,
    graded_by BIGINT,
    graded_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUBMITTED', -- SUBMITTED, GRADED, RETURNED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (graded_by) REFERENCES users(id),
    UNIQUE (assignment_id, student_id)
);

CREATE TABLE IF NOT EXISTS discussion_forums (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_locked BOOLEAN DEFAULT FALSE,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS forum_posts (
    id BIGSERIAL PRIMARY KEY,
    forum_id BIGINT NOT NULL,
    parent_post_id BIGINT,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    attachment_url VARCHAR(500),
    is_pinned BOOLEAN DEFAULT FALSE,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (forum_id) REFERENCES discussion_forums(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_post_id) REFERENCES forum_posts(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ============================================================
-- MODULE 11: TIMETABLE & SCHEDULING
-- ============================================================

CREATE TABLE IF NOT EXISTS time_slots (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    period_number INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_break BOOLEAN DEFAULT FALSE,
    day_type VARCHAR(20) DEFAULT 'REGULAR', -- REGULAR, SPECIAL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS timetables (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

CREATE TABLE IF NOT EXISTS timetable_entries (
    id BIGSERIAL PRIMARY KEY,
    timetable_id BIGINT NOT NULL,
    day_of_week INT NOT NULL, -- 1=Monday, 2=Tuesday ... 5=Friday
    time_slot_id BIGINT NOT NULL,
    subject_id BIGINT,
    teacher_id BIGINT,
    room VARCHAR(50),
    entry_type VARCHAR(20) DEFAULT 'LESSON', -- LESSON, BREAK, ACTIVITY
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (timetable_id) REFERENCES timetables(id) ON DELETE CASCADE,
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

CREATE TABLE IF NOT EXISTS exam_schedules (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    exam_type VARCHAR(50) NOT NULL, -- INTERNAL, KCPE, KCSE, TERM_END
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

CREATE TABLE IF NOT EXISTS exam_timetable_entries (
    id BIGSERIAL PRIMARY KEY,
    exam_schedule_id BIGINT NOT NULL,
    grade_level_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(50),
    invigilator_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_schedule_id) REFERENCES exam_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (invigilator_id) REFERENCES teachers(id)
);

-- ============================================================
-- MODULE 12: COMMUNICATION SYSTEM
-- ============================================================

CREATE TABLE IF NOT EXISTS announcements (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    announcement_type VARCHAR(50) DEFAULT 'GENERAL',
    target_audience VARCHAR(50) DEFAULT 'ALL', -- ALL, TEACHERS, STUDENTS, PARENTS
    target_class_ids TEXT, -- JSON array of class IDs
    attachment_url VARCHAR(500),
    is_published BOOLEAN DEFAULT FALSE,
    publish_date TIMESTAMP,
    expire_date TIMESTAMP,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    parent_message_id BIGINT,
    attachment_url VARCHAR(500),
    is_deleted_by_sender BOOLEAN DEFAULT FALSE,
    is_deleted_by_recipient BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id),
    FOREIGN KEY (parent_message_id) REFERENCES messages(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    reference_type VARCHAR(100),
    reference_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    is_sent_sms BOOLEAN DEFAULT FALSE,
    is_sent_email BOOLEAN DEFAULT FALSE,
    is_sent_push BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sms_logs (
    id BIGSERIAL PRIMARY KEY,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_id BIGINT,
    message TEXT NOT NULL,
    sender_id TEXT DEFAULT 'DigiSchool',
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, DELIVERED, FAILED
    provider_response TEXT,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- ============================================================
-- MODULE 13: PARENT PORTAL (extends existing tables)
-- ============================================================

CREATE TABLE IF NOT EXISTS parent_student_access (
    id BIGSERIAL PRIMARY KEY,
    guardian_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    can_view_grades BOOLEAN DEFAULT TRUE,
    can_view_attendance BOOLEAN DEFAULT TRUE,
    can_view_fees BOOLEAN DEFAULT TRUE,
    can_view_homework BOOLEAN DEFAULT TRUE,
    can_message_teachers BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE (guardian_id, student_id)
);

-- ============================================================
-- MODULE 14: ANALYTICS & AI PREDICTIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS student_performance_analytics (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    term_id BIGINT,
    overall_performance_score DECIMAL(5,2),
    attendance_rate DECIMAL(5,2),
    assignment_completion_rate DECIMAL(5,2),
    improvement_trend VARCHAR(20), -- IMPROVING, DECLINING, STABLE
    risk_level VARCHAR(20), -- LOW, MEDIUM, HIGH, CRITICAL
    dropout_risk_score DECIMAL(5,2),
    predicted_grade VARCHAR(20),
    key_insights TEXT,
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

CREATE TABLE IF NOT EXISTS class_analytics (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT,
    term_id BIGINT NOT NULL,
    average_score DECIMAL(5,2),
    highest_score DECIMAL(5,2),
    lowest_score DECIMAL(5,2),
    pass_rate DECIMAL(5,2),
    attendance_rate DECIMAL(5,2),
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

-- ============================================================
-- MODULE 15: LIBRARY MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS library_books (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    isbn VARCHAR(20),
    author VARCHAR(255),
    publisher VARCHAR(255),
    publication_year INT,
    edition VARCHAR(50),
    category VARCHAR(100),
    subject_id BIGINT,
    grade_level_id BIGINT,
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    shelf_location VARCHAR(100),
    cover_image_url VARCHAR(500),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id)
);

CREATE TABLE IF NOT EXISTS library_borrowings (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    borrowed_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE,
    is_returned BOOLEAN DEFAULT FALSE,
    fine_amount DECIMAL(8,2) DEFAULT 0,
    fine_paid BOOLEAN DEFAULT FALSE,
    condition_borrowed VARCHAR(50) DEFAULT 'GOOD',
    condition_returned VARCHAR(50),
    notes TEXT,
    issued_by BIGINT,
    returned_to BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES library_books(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (issued_by) REFERENCES users(id),
    FOREIGN KEY (returned_to) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS digital_resources (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    resource_type VARCHAR(50) NOT NULL, -- PDF, VIDEO, EBOOK, JOURNAL
    url VARCHAR(500),
    file_url VARCHAR(500),
    subject_id BIGINT,
    grade_level_id BIGINT,
    access_level VARCHAR(20) DEFAULT 'ALL', -- ALL, TEACHERS, STUDENTS
    description TEXT,
    uploaded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (grade_level_id) REFERENCES grade_levels(id)
);

-- ============================================================
-- MODULE 16: TRANSPORT MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    registration_plate VARCHAR(20) NOT NULL UNIQUE,
    make VARCHAR(100),
    model VARCHAR(100),
    capacity INT,
    vehicle_type VARCHAR(50) DEFAULT 'BUS',
    is_active BOOLEAN DEFAULT TRUE,
    insurance_expiry DATE,
    inspection_expiry DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transport_routes (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    vehicle_id BIGINT,
    driver_id BIGINT,
    morning_departure_time TIME,
    afternoon_departure_time TIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (driver_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transport_stops (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL,
    stop_name VARCHAR(255) NOT NULL,
    stop_order INT NOT NULL,
    pickup_time TIME,
    dropoff_time TIME,
    landmark VARCHAR(255),
    coordinates VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES transport_routes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_transport_assignments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (route_id) REFERENCES transport_routes(id),
    FOREIGN KEY (stop_id) REFERENCES transport_stops(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id)
);

-- ============================================================
-- MODULE 17: BOARDING / HOSTEL MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS dormitories (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL, -- MALE, FEMALE
    capacity INT NOT NULL,
    warden_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (warden_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS dormitory_rooms (
    id BIGSERIAL PRIMARY KEY,
    dormitory_id BIGINT NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 4,
    occupied INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dormitory_id) REFERENCES dormitories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_boarding_allocations (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    bed_number INT,
    check_in_date DATE,
    check_out_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (room_id) REFERENCES dormitory_rooms(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    FOREIGN KEY (term_id) REFERENCES terms(id)
);

CREATE TABLE IF NOT EXISTS meal_plans (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    breakfast BOOLEAN DEFAULT TRUE,
    lunch BOOLEAN DEFAULT TRUE,
    dinner BOOLEAN DEFAULT TRUE,
    snacks BOOLEAN DEFAULT FALSE,
    cost_per_term DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 18: HEALTH & MEDICAL RECORDS
-- ============================================================

CREATE TABLE IF NOT EXISTS student_medical_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    blood_group VARCHAR(10),
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    disabilities TEXT,
    special_needs TEXT,
    dietary_restrictions TEXT,
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    medical_insurance_provider VARCHAR(200),
    insurance_policy_number VARCHAR(100),
    doctor_name VARCHAR(200),
    doctor_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medical_conditions (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    condition_name VARCHAR(255) NOT NULL,
    condition_type VARCHAR(50), -- CHRONIC, ALLERGY, MENTAL_HEALTH, OTHER
    description TEXT,
    medication TEXT,
    severity VARCHAR(20), -- MILD, MODERATE, SEVERE
    diagnosed_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vaccination_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    vaccine_name VARCHAR(255) NOT NULL,
    dose_number INT DEFAULT 1,
    vaccination_date DATE NOT NULL,
    next_due_date DATE,
    administered_by VARCHAR(255),
    batch_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clinic_visits (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    visit_date TIMESTAMP NOT NULL,
    complaint TEXT NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    medication_prescribed TEXT,
    is_referred_to_hospital BOOLEAN DEFAULT FALSE,
    hospital_referred_to VARCHAR(255),
    follow_up_date DATE,
    attended_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 19: DISCIPLINE & BEHAVIOR MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS discipline_categories (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    severity VARCHAR(20) NOT NULL, -- MINOR, MODERATE, MAJOR, EXPULSION
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS discipline_incidents (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    incident_date DATE NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255),
    witnesses TEXT,
    evidence_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'OPEN', -- OPEN, UNDER_REVIEW, RESOLVED, ESCALATED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (category_id) REFERENCES discipline_categories(id),
    FOREIGN KEY (reported_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS disciplinary_actions (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    action_type VARCHAR(100) NOT NULL, -- WARNING, DETENTION, SUSPENSION, EXPULSION, COUNSELING
    start_date DATE,
    end_date DATE,
    description TEXT,
    imposed_by BIGINT NOT NULL,
    parent_notified BOOLEAN DEFAULT FALSE,
    parent_notified_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (incident_id) REFERENCES discipline_incidents(id) ON DELETE CASCADE,
    FOREIGN KEY (imposed_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS behavior_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    positive_behaviors INT DEFAULT 0,
    negative_behaviors INT DEFAULT 0,
    overall_behavior_grade VARCHAR(20),
    teacher_comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id),
    UNIQUE (student_id, academic_year_id)
);

-- ============================================================
-- MODULE 20: DOCUMENT & CERTIFICATE MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS document_templates (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    document_type VARCHAR(100) NOT NULL, -- REPORT_CARD, TRANSCRIPT, CERTIFICATE, LETTER, ID
    template_content TEXT,
    template_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS generated_documents (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    student_id BIGINT,
    user_id BIGINT,
    document_number VARCHAR(100) UNIQUE,
    title VARCHAR(255) NOT NULL,
    file_url VARCHAR(500),
    generated_by BIGINT NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_signed BOOLEAN DEFAULT FALSE,
    signed_by VARCHAR(255),
    signed_at TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(100) UNIQUE,
    FOREIGN KEY (template_id) REFERENCES document_templates(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (generated_by) REFERENCES users(id)
);

-- ============================================================
-- MODULE 21: ALUMNI MANAGEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS alumni (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    graduation_year INT NOT NULL,
    graduation_grade VARCHAR(20),
    graduation_grade_points DECIMAL(5,2),
    current_email VARCHAR(255),
    current_phone VARCHAR(20),
    current_address TEXT,
    current_employer VARCHAR(255),
    current_job_title VARCHAR(255),
    higher_institution VARCHAR(255),
    current_program VARCHAR(255),
    linkedin_profile VARCHAR(500),
    achievements TEXT,
    is_contact_consented BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS alumni_events (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_time TIME,
    location VARCHAR(500),
    is_virtual BOOLEAN DEFAULT FALSE,
    virtual_link VARCHAR(500),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS alumni_event_registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    alumni_id BIGINT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    attended BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (event_id, alumni_id),
    FOREIGN KEY (event_id) REFERENCES alumni_events(id) ON DELETE CASCADE,
    FOREIGN KEY (alumni_id) REFERENCES alumni(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alumni_donations (
    id BIGSERIAL PRIMARY KEY,
    alumni_id BIGINT NOT NULL,
    school_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'KES',
    donation_date DATE NOT NULL,
    purpose TEXT,
    is_anonymous BOOLEAN DEFAULT FALSE,
    receipt_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alumni_id) REFERENCES alumni(id),
    FOREIGN KEY (school_id) REFERENCES schools(id)
);

-- ============================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_students_school ON students(school_id);
CREATE INDEX IF NOT EXISTS idx_students_admission ON students(admission_number);
CREATE INDEX IF NOT EXISTS idx_students_nemis ON students(nemis_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON student_enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_class ON student_enrollments(class_id);
CREATE INDEX IF NOT EXISTS idx_attendance_session ON student_attendance(session_id);
CREATE INDEX IF NOT EXISTS idx_attendance_student ON student_attendance(student_id);
CREATE INDEX IF NOT EXISTS idx_results_assessment ON assessment_results(assessment_id);
CREATE INDEX IF NOT EXISTS idx_results_student ON assessment_results(student_id);
CREATE INDEX IF NOT EXISTS idx_payments_invoice ON fee_payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payments_student ON fee_payments(student_id);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_messages_recipient ON messages(recipient_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_teachers_school ON teachers(school_id);
CREATE INDEX IF NOT EXISTS idx_classes_school ON classes(school_id);
CREATE INDEX IF NOT EXISTS idx_classes_grade ON classes(grade_level_id);

-- ============================================================
-- INITIAL DATA SEEDS
-- ============================================================

-- Insert default system roles
INSERT INTO roles (name, description, is_system_role) VALUES
    ('SUPER_ADMIN', 'Full system administrator with all privileges', TRUE),
    ('SCHOOL_ADMIN', 'School-level administrator', TRUE),
    ('PRINCIPAL', 'School principal with management access', TRUE),
    ('DEPUTY_PRINCIPAL', 'Deputy principal', TRUE),
    ('HOD', 'Head of Department', TRUE),
    ('TEACHER', 'Class or subject teacher', TRUE),
    ('LIBRARIAN', 'Library management access', TRUE),
    ('ACCOUNTANT', 'Finance and fee management access', TRUE),
    ('COUNSELOR', 'Student counseling access', TRUE),
    ('NURSE', 'Health records access', TRUE),
    ('STUDENT', 'Student access to portal', TRUE),
    ('PARENT', 'Parent/guardian portal access', TRUE)
ON CONFLICT (name) DO NOTHING;

-- Insert CBC Core Competencies
INSERT INTO cbc_core_competencies (name, code, description, category) VALUES
    ('Communication and Collaboration', 'CC', 'Ability to communicate effectively and work collaboratively', 'SOCIAL'),
    ('Critical Thinking and Problem Solving', 'CT', 'Ability to analyze, evaluate and solve problems', 'COGNITIVE'),
    ('Creativity and Imagination', 'CI', 'Ability to generate new ideas and think creatively', 'CREATIVE'),
    ('Citizenship', 'CZ', 'Responsible citizenship and civic engagement', 'SOCIAL'),
    ('Digital Literacy', 'DL', 'Proficiency in using digital tools and technologies', 'DIGITAL'),
    ('Learning to Learn', 'LL', 'Self-directed learning and metacognitive skills', 'COGNITIVE'),
    ('Self-Efficacy', 'SE', 'Confidence and belief in one ability to succeed', 'PERSONAL')
ON CONFLICT (name) DO NOTHING;

-- Insert CBC Values
INSERT INTO cbc_values (name, code, description) VALUES
    ('Love', 'LV', 'Love for oneself, family, community and country'),
    ('Responsibility', 'RS', 'Taking responsibility for ones actions'),
    ('Respect', 'RP', 'Respect for self, others and the environment'),
    ('Unity', 'UN', 'National unity and cohesion'),
    ('Peace', 'PE', 'Promotion of peace and harmony'),
    ('Patriotism', 'PA', 'Love for and loyalty to Kenya'),
    ('Social Justice', 'SJ', 'Fairness, equity and justice')
ON CONFLICT (name) DO NOTHING;

-- Insert system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description, is_public) VALUES
    ('app.name', 'DigiSchool EMIS', 'STRING', 'Application name', TRUE),
    ('app.version', '1.0.0', 'STRING', 'Application version', TRUE),
    ('app.country', 'Kenya', 'STRING', 'Country of operation', TRUE),
    ('app.currency', 'KES', 'STRING', 'Default currency code', TRUE),
    ('app.curriculum', 'CBC', 'STRING', 'Default curriculum type', TRUE),
    ('app.academic_year_start_month', '1', 'INTEGER', 'Month academic year starts (1=January)', FALSE),
    ('app.max_class_size', '45', 'INTEGER', 'Maximum students per class', FALSE),
    ('sms.provider', 'AFRICAS_TALKING', 'STRING', 'SMS service provider', FALSE),
    ('email.smtp_host', 'smtp.gmail.com', 'STRING', 'SMTP host for emails', FALSE),
    ('finance.late_fee_percentage', '5', 'DECIMAL', 'Late fee penalty percentage', FALSE),
    ('library.max_borrow_days', '14', 'INTEGER', 'Maximum book borrowing days', FALSE),
    ('library.fine_per_day', '10', 'DECIMAL', 'Fine per day for overdue books (KES)', FALSE),
    ('attendance.minimum_percentage', '75', 'DECIMAL', 'Minimum attendance percentage required', FALSE),
    ('grades.pass_mark', '40', 'DECIMAL', 'Minimum pass mark percentage', FALSE)
ON CONFLICT (setting_key) DO NOTHING;
