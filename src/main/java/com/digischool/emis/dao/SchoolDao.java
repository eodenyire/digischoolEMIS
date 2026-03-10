package com.digischool.emis.dao;

import com.digischool.emis.model.academic.School;
import com.digischool.emis.model.academic.AcademicYear;
import com.digischool.emis.model.academic.Term;
import com.digischool.emis.model.academic.GradeLevel;
import com.digischool.emis.model.academic.SchoolClass;
import java.util.List;
import java.util.Optional;

public interface SchoolDao extends GenericDao<School, Long> {
    Optional<School> findByKnecCode(String knecCode);
    Optional<AcademicYear> findCurrentAcademicYear(Long schoolId);
    Optional<Term> findCurrentTerm(Long schoolId);
    List<Term> findTermsByAcademicYear(Long academicYearId);
    List<GradeLevel> findGradeLevelsBySchool(Long schoolId);
    List<SchoolClass> findActiveClasses(Long schoolId, Long academicYearId);
    Optional<SchoolClass> findClassById(Long classId);
    long countActiveClasses(Long schoolId, Long academicYearId);
    AcademicYear saveAcademicYear(AcademicYear year);
    Term saveTerm(Term term);
    SchoolClass saveClass(SchoolClass schoolClass);
    GradeLevel saveGradeLevel(GradeLevel gradeLevel);
}
