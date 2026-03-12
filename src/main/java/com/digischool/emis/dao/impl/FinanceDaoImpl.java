package com.digischool.emis.dao.impl;

import com.digischool.emis.dao.FinanceDao;
import com.digischool.emis.model.finance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FinanceDaoImpl implements FinanceDao {

    private static final Logger log = LoggerFactory.getLogger(FinanceDaoImpl.class);
    private final DataSource ds;

    public FinanceDaoImpl(DataSource ds) { this.ds = ds; }

    @Override public StudentInvoice save(StudentInvoice inv) {
        String sql = """
            INSERT INTO student_invoices
              (school_id,student_id,term_id,invoice_number,description,
               total_amount,amount_paid,balance,status,due_date,
               created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            int i = 1;
            s.setLong(i++, inv.getSchoolId());
            s.setLong(i++, inv.getStudentId());
            s.setLong(i++, inv.getTermId());
            s.setString(i++, inv.getInvoiceNumber());
            s.setString(i++, inv.getDescription());
            s.setBigDecimal(i++, inv.getTotalAmount());
            s.setBigDecimal(i++, inv.getAmountPaid() != null ? inv.getAmountPaid() : BigDecimal.ZERO);
            s.setBigDecimal(i++, inv.getBalance() != null ? inv.getBalance() : inv.getTotalAmount());
            s.setString(i++, inv.getStatus());
            s.setDate(i++, inv.getDueDate() != null ? Date.valueOf(inv.getDueDate()) : null);
            ResultSet rs = s.executeQuery();
            if (rs.next()) inv.setId(rs.getLong("id"));
            return inv;
        } catch (SQLException e) { throw new RuntimeException("save invoice failed", e); }
    }

    @Override public StudentInvoice update(StudentInvoice inv) {
        exec("UPDATE student_invoices SET amount_paid=?,balance=?,status=?,updated_at=NOW() WHERE id=?",
             inv.getAmountPaid(), inv.getBalance(), inv.getStatus(), inv.getId());
        return inv;
    }
    @Override public Optional<StudentInvoice> findById(Long id) {
        return queryOne("SELECT * FROM student_invoices WHERE id=?", id);
    }
    @Override public List<StudentInvoice> findAll() {
        return queryList("SELECT * FROM student_invoices ORDER BY created_at DESC");
    }
    @Override public void deleteById(Long id) { exec("DELETE FROM student_invoices WHERE id=?", id); }
    @Override public boolean existsById(Long id) {
        return count("SELECT COUNT(*) FROM student_invoices WHERE id=?", id) > 0;
    }
    @Override public long count() { return count("SELECT COUNT(*) FROM student_invoices"); }

    @Override public Optional<StudentInvoice> findByInvoiceNumber(String num) {
        return queryOne("SELECT * FROM student_invoices WHERE invoice_number=?", num);
    }
    @Override public List<StudentInvoice> findByStudent(Long studentId) {
        return queryList("SELECT * FROM student_invoices WHERE student_id=? ORDER BY created_at DESC",
                studentId);
    }
    @Override public List<StudentInvoice> findByStudentAndTerm(Long studentId, Long termId) {
        return queryList("SELECT * FROM student_invoices WHERE student_id=? AND term_id=?",
                studentId, termId);
    }
    @Override public List<StudentInvoice> findUnpaidInvoices(Long schoolId, Long termId) {
        return queryList("SELECT * FROM student_invoices WHERE school_id=? AND term_id=? " +
                         "AND status IN ('UNPAID','PARTIAL') ORDER BY due_date", schoolId, termId);
    }
    @Override public long countUnpaidInvoices(Long schoolId, Long termId) {
        return count("SELECT COUNT(*) FROM student_invoices WHERE school_id=? AND term_id=? " +
                     "AND status IN ('UNPAID','PARTIAL')", schoolId, termId);
    }

    @Override
    public FeePayment savePayment(FeePayment p) {
        String sql = """
            INSERT INTO fee_payments
              (invoice_id,student_id,amount,payment_method,transaction_reference,
               mpesa_code,payment_date,received_by,notes,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            int i = 1;
            s.setLong(i++, p.getInvoiceId());
            s.setLong(i++, p.getStudentId());
            s.setBigDecimal(i++, p.getAmount());
            s.setString(i++, p.getPaymentMethod());
            s.setString(i++, p.getTransactionReference());
            s.setString(i++, p.getMpesaCode());
            s.setDate(i++, p.getPaymentDate() != null ? Date.valueOf(p.getPaymentDate()) : null);
            if (p.getReceivedBy() != null) s.setLong(i++, p.getReceivedBy()); else { s.setNull(i++, Types.BIGINT); }
            s.setString(i++, p.getNotes());
            ResultSet rs = s.executeQuery();
            if (rs.next()) p.setId(rs.getLong("id"));
            // Update invoice balance
            exec("UPDATE student_invoices SET amount_paid = amount_paid + ?, " +
                 "balance = balance - ?, " +
                 "status = CASE WHEN balance - ? <= 0 THEN 'PAID' ELSE 'PARTIAL' END, " +
                 "updated_at = NOW() WHERE id=?",
                 p.getAmount(), p.getAmount(), p.getAmount(), p.getInvoiceId());
            return p;
        } catch (SQLException e) { throw new RuntimeException("savePayment failed", e); }
    }

    @Override public List<FeePayment> findPaymentsByInvoice(Long invoiceId) {
        return queryPayments("SELECT * FROM fee_payments WHERE invoice_id=? ORDER BY payment_date DESC",
                invoiceId);
    }

    @Override public BigDecimal getTotalCollected(Long schoolId, Long termId) {
        return queryBigDecimal("""
            SELECT COALESCE(SUM(fp.amount),0)
            FROM fee_payments fp JOIN student_invoices inv ON fp.invoice_id=inv.id
            WHERE inv.school_id=? AND inv.term_id=?""", schoolId, termId);
    }

    @Override public BigDecimal getTotalOutstanding(Long schoolId, Long termId) {
        return queryBigDecimal("""
            SELECT COALESCE(SUM(balance),0)
            FROM student_invoices
            WHERE school_id=? AND term_id=? AND status IN ('UNPAID','PARTIAL')""", schoolId, termId);
    }

    @Override public List<FeeStructure> findFeeStructures(Long schoolId, Long academicYearId) {
        try (Connection c = ds.getConnection();
             PreparedStatement s = c.prepareStatement(
                "SELECT * FROM fee_structures WHERE school_id=? AND academic_year_id=? AND is_active=TRUE")) {
            s.setLong(1, schoolId); s.setLong(2, academicYearId);
            ResultSet rs = s.executeQuery();
            List<FeeStructure> list = new ArrayList<>();
            while (rs.next()) list.add(mapFS(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public FeeStructure saveFeeStructure(FeeStructure fs) {
        String sql = """
            INSERT INTO fee_structures
              (school_id,academic_year_id,grade_level_id,name,description,is_boarding,is_active,
               created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,NOW(),NOW()) RETURNING id""";
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, fs.getSchoolId()); s.setLong(2, fs.getAcademicYearId());
            s.setLong(3, fs.getGradeLevelId()); s.setString(4, fs.getName());
            s.setString(5, fs.getDescription()); s.setBoolean(6, fs.isBoarding());
            s.setBoolean(7, fs.isActive());
            ResultSet rs = s.executeQuery();
            if (rs.next()) fs.setId(rs.getLong("id"));
            return fs;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private Optional<StudentInvoice> queryOne(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? Optional.of(mapInv(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<StudentInvoice> queryList(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<StudentInvoice> list = new ArrayList<>();
            while (rs.next()) list.add(mapInv(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<FeePayment> queryPayments(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            List<FeePayment> list = new ArrayList<>();
            while (rs.next()) {
                FeePayment fp = new FeePayment();
                fp.setId(rs.getLong("id"));
                fp.setInvoiceId(rs.getLong("invoice_id"));
                fp.setStudentId(rs.getLong("student_id"));
                fp.setAmount(rs.getBigDecimal("amount"));
                fp.setPaymentMethod(rs.getString("payment_method"));
                fp.setTransactionReference(rs.getString("transaction_reference"));
                fp.setMpesaCode(rs.getString("mpesa_code"));
                Date pd = rs.getDate("payment_date"); if (pd != null) fp.setPaymentDate(pd.toLocalDate());
                long rb = rs.getLong("received_by"); if (!rs.wasNull()) fp.setReceivedBy(rb);
                fp.setNotes(rs.getString("notes"));
                Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) fp.setCreatedAt(ca.toLocalDateTime());
                list.add(fp);
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private void exec(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            s.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private long count(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private BigDecimal queryBigDecimal(String sql, Object... p) {
        try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) s.setObject(i + 1, p[i]);
            ResultSet rs = s.executeQuery();
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) { return BigDecimal.ZERO; }
    }

    private StudentInvoice mapInv(ResultSet rs) throws SQLException {
        StudentInvoice inv = new StudentInvoice();
        inv.setId(rs.getLong("id"));
        inv.setSchoolId(rs.getLong("school_id"));
        inv.setStudentId(rs.getLong("student_id"));
        inv.setTermId(rs.getLong("term_id"));
        inv.setInvoiceNumber(rs.getString("invoice_number"));
        inv.setDescription(rs.getString("description"));
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setAmountPaid(rs.getBigDecimal("amount_paid"));
        inv.setBalance(rs.getBigDecimal("balance"));
        inv.setStatus(rs.getString("status"));
        Date dd = rs.getDate("due_date"); if (dd != null) inv.setDueDate(dd.toLocalDate());
        Timestamp ca = rs.getTimestamp("created_at"); if (ca != null) inv.setCreatedAt(ca.toLocalDateTime());
        return inv;
    }

    private FeeStructure mapFS(ResultSet rs) throws SQLException {
        FeeStructure fs = new FeeStructure();
        fs.setId(rs.getLong("id"));
        fs.setSchoolId(rs.getLong("school_id"));
        fs.setAcademicYearId(rs.getLong("academic_year_id"));
        fs.setGradeLevelId(rs.getLong("grade_level_id"));
        fs.setName(rs.getString("name"));
        fs.setDescription(rs.getString("description"));
        fs.setBoarding(rs.getBoolean("is_boarding"));
        fs.setActive(rs.getBoolean("is_active"));
        return fs;
    }
}
