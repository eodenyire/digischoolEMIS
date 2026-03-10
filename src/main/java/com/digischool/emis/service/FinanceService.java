package com.digischool.emis.service;

import com.digischool.emis.model.finance.FeePayment;
import com.digischool.emis.model.finance.FeeStructure;
import com.digischool.emis.model.finance.StudentInvoice;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FinanceService {
    StudentInvoice generateInvoice(Long studentId, Long termId, String description,
                                   BigDecimal amount, Long schoolId);
    Optional<StudentInvoice> getInvoice(Long invoiceId);
    List<StudentInvoice> getStudentInvoices(Long studentId);
    List<StudentInvoice> getUnpaidInvoices(Long schoolId, Long termId);
    /** Record a fee payment (cash, M-Pesa, bank). Updates invoice balance automatically. */
    FeePayment receivePayment(Long invoiceId, BigDecimal amount, String method,
                              String mpesaCode, String reference, Long receivedBy, String notes);
    List<FeePayment> getPaymentsForInvoice(Long invoiceId);
    BigDecimal getTotalCollected(Long schoolId, Long termId);
    BigDecimal getTotalOutstanding(Long schoolId, Long termId);
    long countUnpaidInvoices(Long schoolId, Long termId);
    FeeStructure saveFeeStructure(FeeStructure structure);
    List<FeeStructure> getFeeStructures(Long schoolId, Long academicYearId);
}
