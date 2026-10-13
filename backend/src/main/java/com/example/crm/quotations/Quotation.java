package com.example.crm.quotations;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.projectvendors.ProjectVendor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "quotations")
public class Quotation extends AuditSupport {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_vendor_id", nullable = false)
    private ProjectVendor projectVendor;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false)
    private LocalDate receivedDate;

    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private QuotationStatus status = QuotationStatus.RECEIVED;

    @Column(nullable = false)
    private int revisionNumber = 1;

    @Column(nullable = false)
    private boolean selected;

    @Column(length = 4000)
    private String notes;

    protected Quotation() {}

    public Quotation(ProjectVendor projectVendor, BigDecimal amount, String currencyCode, LocalDate receivedDate, int revisionNumber) {
        this.projectVendor = projectVendor;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.receivedDate = receivedDate;
        this.revisionNumber = revisionNumber;
    }

    @Override
    protected void assignIdIfNecessary() {
        if (id == null) id = newId();
    }

    public UUID getId() { return id; }
    public ProjectVendor getProjectVendor() { return projectVendor; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public LocalDate getValidUntil() { return validUntil; }
    public QuotationStatus getStatus() { return status; }
    public int getRevisionNumber() { return revisionNumber; }
    public boolean isSelected() { return selected; }
    public String getNotes() { return notes; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public void setStatus(QuotationStatus status) { this.status = status; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public void setNotes(String notes) { this.notes = notes; }
}
