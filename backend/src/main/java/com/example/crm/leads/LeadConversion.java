package com.example.crm.leads;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
import com.example.crm.contacts.Contact;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead_conversions")
public class LeadConversion extends AuditSupport {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "converted_by_user_id")
    private User convertedBy;

    @Column(nullable = false)
    private OffsetDateTime convertedAt;

    private String notes;

    protected LeadConversion() {}

    public LeadConversion(Lead lead, Company company, Contact contact, Opportunity opportunity, User convertedBy, OffsetDateTime convertedAt) {
        this.lead = lead;
        this.company = company;
        this.contact = contact;
        this.opportunity = opportunity;
        this.convertedBy = convertedBy;
        this.convertedAt = convertedAt;
    }

    @Override
    protected void assignIdIfNecessary() {
        if (id == null) {
            id = newId();
        }
    }

    public UUID getId() { return id; }
    public Lead getLead() { return lead; }
    public Company getCompany() { return company; }
    public Contact getContact() { return contact; }
    public Opportunity getOpportunity() { return opportunity; }
    public User getConvertedBy() { return convertedBy; }
    public OffsetDateTime getConvertedAt() { return convertedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
