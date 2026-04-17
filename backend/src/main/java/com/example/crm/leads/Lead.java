package com.example.crm.leads;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
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
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "leads")
public class Lead extends AuditSupport {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(length = 180)
    private String email;

    @Column(length = 60)
    private String phone;

    @Column(length = 180)
    private String companyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadStatus status = LeadStatus.NEW;

    @Column(nullable = false, length = 255)
    private String summary;

    private OffsetDateTime assignedAt;
    private OffsetDateTime deletedAt;

    @Version
    private long version;

    protected Lead() {}

    public Lead(String firstName, String lastName, LeadSource source, User createdBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.source = source;
        this.createdBy = createdBy;
        this.summary = firstName + " " + lastName;
    }

    @Override
    protected void assignIdIfNecessary() {
        if (id == null) {
            id = newId();
        }
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; refreshSummary(); }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; refreshSummary(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    public User getCreatedBy() { return createdBy; }
    public LeadSource getSource() { return source; }
    public void setSource(LeadSource source) { this.source = source; }
    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }
    public String getSummary() { return summary; }
    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
    public long getVersion() { return version; }

    private void refreshSummary() {
        if (firstName != null && lastName != null) {
            summary = firstName + " " + lastName;
        }
    }
}
