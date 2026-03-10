package com.digischool.emis.service.impl;

import com.digischool.emis.dao.FinanceDao;
import com.digischool.emis.dao.StudentDao;
import com.digischool.emis.model.finance.FeePayment;
import com.digischool.emis.model.finance.FeeStructure;
import com.digischool.emis.model.finance.StudentInvoice;
import com.digischool.emis.service.FinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FinanceServiceImpl implements FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceServiceImpl.class);
    private final FinanceDao financeDao;
    private final StudentDao studentDao;

    public FinanceServiceImpl(FinanceDao financeDao, StudentDao studentDao) {
        this.financeDao = financeDao;
        this.studentDao = studentDao;
    }

    @Override
    public StudentInvoice generateInvoice(Long studentId, Long termId, String description,
                                           BigDecimal amount, Long schoolId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invoice amount must be positive");
        studentDao.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        StudentInvoice inv = new StudentInvoice();
        inv.setStudentId(studentId);
        inv.setTermId(termId);
        inv.setSchoolId(schoolId);
        inv.setInvoiceNumber(generateInvoiceNumber());
        inv.setDescription(description);
        inv.setTotalAmount(amount);
        inv.setAmountPaid(BigDecimal.ZERO);
        inv.setBalance(amount);
        inv.setStatus("UNPAID");
        inv.setDueDate(LocalDate.now().plusDays(30));
        return financeDao.save(inv);
    }

    @Override public Optional<StudentInvoice> getInvoice(Long id) { return financeDao.findById(id); }
    @Override public List<StudentInvoice> getStudentInvoices(Long studentId) {
        return financeDao.findByStudent(studentId);
    }
    @Override public List<StudentInvoice> getUnpaidInvoices(Long schoolId, Long termId) {
        return financeDao.findUnpaidInvoices(schoolId, termId);
    }

    @Override
    public FeePayment receivePayment(Long invoiceId, BigDecimal amount, String method,
                                      String mpesaCode, String reference, Long receivedBy, String notes) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Payment amount must be positive");

        StudentInvoice inv = financeDao.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));
        if ("PAID".equals(inv.getStatus()))
            throw new IllegalStateException("Invoice already fully paid: " + invoiceId);

        FeePayment p = new FeePayment();
        p.setInvoiceId(invoiceId);
        p.setStudentId(inv.getStudentId());
        p.setAmount(amount);
        p.setPaymentMethod(method);
        p.setTransactionReference(reference);
        p.setMpesaCode(mpesaCode);
        p.setPaymentDate(LocalDate.now());
        p.setReceivedBy(receivedBy);
        p.setNotes(notes);
        return financeDao.savePayment(p);
    }

    @Override public List<FeePayment> getPaymentsForInvoice(Long invoiceId) {
        return financeDao.findPaymentsByInvoice(invoiceId);
    }
    @Override public BigDecimal getTotalCollected(Long schoolId, Long termId) {
        return financeDao.getTotalCollected(schoolId, termId);
    }
    @Override public BigDecimal getTotalOutstanding(Long schoolId, Long termId) {
        return financeDao.getTotalOutstanding(schoolId, termId);
    }
    @Override public long countUnpaidInvoices(Long schoolId, Long termId) {
        return financeDao.countUnpaidInvoices(schoolId, termId);
    }
    @Override public FeeStructure saveFeeStructure(FeeStructure fs) {
        return financeDao.saveFeeStructure(fs);
    }
    @Override public List<FeeStructure> getFeeStructures(Long schoolId, Long academicYearId) {
        return financeDao.findFeeStructures(schoolId, academicYearId);
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() % 1_000_000L
               + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
