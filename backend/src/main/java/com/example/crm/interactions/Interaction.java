package com.example.crm.interactions;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
import com.example.crm.contacts.Contact;
import com.example.crm.leads.Lead;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interactions")
public class Interaction extends AuditSupport {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private InteractionType type;

    @Column(nullable = false, length = 160)
    private String subject;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private OffsetDateTime occurredAt;

    private OffsetDateTime followUpAt;
    private OffsetDateTime deletedAt;

    protected Interaction() {}

    public Interaction(User author, InteractionType type, String subject, OffsetDateTime occurredAt) {
        this.author = author;
        this.type = type;
        this.subject = subject;
        this.occurredAt = occurredAt;
    }

    @Override
    protected void assignIdIfNecessary() {
        if (id == null) {
            id = newId();
        }
    }

    public UUID getId() { return id; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }
    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }
    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }
    public InteractionType getType() { return type; }
    public void setType(InteractionType type) { this.type = type; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
    public OffsetDateTime getFollowUpAt() { return followUpAt; }
    public void setFollowUpAt(OffsetDateTime followUpAt) { this.followUpAt = followUpAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
}
