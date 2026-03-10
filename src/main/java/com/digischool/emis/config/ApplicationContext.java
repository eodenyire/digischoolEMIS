package com.digischool.emis.config;

import com.digischool.emis.dao.*;
import com.digischool.emis.dao.impl.*;
import com.digischool.emis.service.*;
import com.digischool.emis.service.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Central application context — single registry for all DAOs and Services.
 *
 * Acts as a lightweight dependency-injection container, similar to the
 * service-layer architecture used by PowerSchool and Infinite Campus.
 * Every module is wired here so controllers never instantiate services
 * themselves, enabling clean separation of concerns and easy testing.
 *
 * Usage:
 *   ApplicationContext ctx = ApplicationContext.getInstance();
 *   StudentService svc = ctx.getStudentService();
 */
public class ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private static ApplicationContext instance;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    private final StudentDao       studentDao;
    private final TeacherDao       teacherDao;
    private final AssessmentDao    assessmentDao;
    private final AttendanceDao    attendanceDao;
    private final FinanceDao       financeDao;
    private final CbcDao           cbcDao;
    private final SchoolDao        schoolDao;
    private final ReportCardDao    reportCardDao;
    private final AnalyticsDao     analyticsDao;

    // ── Services ─────────────────────────────────────────────────────────────
    private final StudentService    studentService;
    private final TeacherService    teacherService;
    private final AssessmentService assessmentService;
    private final AttendanceService attendanceService;
    private final FinanceService    financeService;
    private final CbcService        cbcService;
    private final ReportCardService reportCardService;
    private final AnalyticsService  analyticsService;
    private final AuthenticationService authService;

    private ApplicationContext(DataSource dataSource, DatabaseConfig dbConfig) {
        logger.info("Initialising ApplicationContext — wiring all modules...");

        // ── Instantiate DAOs ─────────────────────────────────────────────────
        this.studentDao    = new StudentDaoImpl(dataSource);
        this.teacherDao    = new TeacherDaoImpl(dataSource);
        this.assessmentDao = new AssessmentDaoImpl(dataSource);
        this.attendanceDao = new AttendanceDaoImpl(dataSource);
        this.financeDao    = new FinanceDaoImpl(dataSource);
        this.cbcDao        = new CbcDaoImpl(dataSource);
        this.schoolDao     = new SchoolDaoImpl(dataSource);
        this.reportCardDao = new ReportCardDaoImpl(dataSource);
        this.analyticsDao  = new AnalyticsDaoImpl(dataSource);

        // ── Instantiate Services (injecting their DAOs) ───────────────────────
        this.studentService    = new StudentServiceImpl(studentDao);
        this.teacherService    = new TeacherServiceImpl(teacherDao);
        this.assessmentService = new AssessmentServiceImpl(assessmentDao, studentDao);
        this.attendanceService = new AttendanceServiceImpl(attendanceDao, studentDao);
        this.financeService    = new FinanceServiceImpl(financeDao, studentDao);
        this.cbcService        = new CbcServiceImpl(cbcDao);
        this.authService       = new AuthenticationService(dataSource, dbConfig);

        // Cross-module services (depend on multiple other services)
        this.reportCardService = new ReportCardServiceImpl(
                reportCardDao, assessmentService, attendanceService, cbcService, studentService);
        this.analyticsService  = new AnalyticsServiceImpl(
                analyticsDao, studentService, assessmentService, attendanceService);

        logger.info("ApplicationContext initialised — all {} modules wired", 10);
    }

    /**
     * Initialises the singleton. Must be called once on startup after the
     * DataSource is available.
     */
    public static synchronized void initialise(DataSource dataSource, DatabaseConfig dbConfig) {
        if (instance == null) {
            instance = new ApplicationContext(dataSource, dbConfig);
        }
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "ApplicationContext not initialised. Call initialise() first.");
        }
        return instance;
    }

    // ── DAO accessors ────────────────────────────────────────────────────────
    public StudentDao       getStudentDao()    { return studentDao; }
    public TeacherDao       getTeacherDao()    { return teacherDao; }
    public AssessmentDao    getAssessmentDao() { return assessmentDao; }
    public AttendanceDao    getAttendanceDao() { return attendanceDao; }
    public FinanceDao       getFinanceDao()    { return financeDao; }
    public CbcDao           getCbcDao()        { return cbcDao; }
    public SchoolDao        getSchoolDao()     { return schoolDao; }
    public ReportCardDao    getReportCardDao() { return reportCardDao; }
    public AnalyticsDao     getAnalyticsDao()  { return analyticsDao; }

    // ── Service accessors ─────────────────────────────────────────────────────
    public StudentService       getStudentService()    { return studentService; }
    public TeacherService       getTeacherService()    { return teacherService; }
    public AssessmentService    getAssessmentService() { return assessmentService; }
    public AttendanceService    getAttendanceService() { return attendanceService; }
    public FinanceService       getFinanceService()    { return financeService; }
    public CbcService           getCbcService()        { return cbcService; }
    public ReportCardService    getReportCardService() { return reportCardService; }
    public AnalyticsService     getAnalyticsService()  { return analyticsService; }
    public AuthenticationService getAuthService()      { return authService; }
}
