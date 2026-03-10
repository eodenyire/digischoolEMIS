package com.digischool.emis.dao;

import com.digischool.emis.model.finance.FeePayment;
import com.digischool.emis.model.finance.FeeStructure;
import com.digischool.emis.model.finance.StudentInvoice;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FinanceDao extends GenericDao<StudentInvoice, Long> {
    Optional<StudentInvoice> findByInvoiceNumber(String invoiceNumber);
    List<StudentInvoice> findByStudent(Long studentId);
    List<StudentInvoice> findByStudentAndTerm(Long studentId, Long termId);
    List<StudentInvoice> findUnpaidInvoices(Long schoolId, Long termId);
    long countUnpaidInvoices(Long schoolId, Long termId);
    FeePayment savePayment(FeePayment payment);
    List<FeePayment> findPaymentsByInvoice(Long invoiceId);
    /** Total fees collected for a school in a term. */
    BigDecimal getTotalCollected(Long schoolId, Long termId);
    /** Total outstanding balance across all students. */
    BigDecimal getTotalOutstanding(Long schoolId, Long termId);
    List<FeeStructure> findFeeStructures(Long schoolId, Long academicYearId);
    FeeStructure saveFeeStructure(FeeStructure structure);
}
