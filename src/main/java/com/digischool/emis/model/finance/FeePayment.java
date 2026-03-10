package com.digischool.emis.model.finance;

import com.digischool.emis.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Records a fee payment made by a student/guardian.
 * Supports M-Pesa, bank transfer, cash, and cheque payments.
 */
public class FeePayment extends BaseEntity {

    private Long invoiceId;
    private Long studentId;
    private String receiptNumber;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private String paymentMethod; // CASH, MPESA, BANK_TRANSFER, CHEQUE
    private String transactionReference;
    private String mpesaCode;
    private String bankName;
    private String chequeNumber;
    private Long receivedBy;
    private String notes;
    private boolean isReversed;

    public FeePayment() {
        this.isReversed = false;
    }

    // Getters and Setters
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getMpesaCode() { return mpesaCode; }
    public void setMpesaCode(String mpesaCode) { this.mpesaCode = mpesaCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }

    public Long getReceivedBy() { return receivedBy; }
    public void setReceivedBy(Long receivedBy) { this.receivedBy = receivedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isReversed() { return isReversed; }
    public void setReversed(boolean reversed) { isReversed = reversed; }
}
