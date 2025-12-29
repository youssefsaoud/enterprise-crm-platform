package com.example.crm.leads;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
import com.example.crm.contacts.Contact;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "leads")
public class Lead extends AuditSupport {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "company_id") private Company company;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "contact_id") private Contact contact;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_to_id") private User assignedTo;
    private String source;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private LeadStatus status = LeadStatus.NEW;
    @Column(nullable = false) private String summary;
    protected Lead() {}
    public Lead(String summary, User assignedTo) { this.summary = summary; this.assignedTo = assignedTo; }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public User getAssignedTo() { return assignedTo; }
    public LeadStatus getStatus() { return status; }
}
