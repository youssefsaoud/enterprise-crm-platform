package com.example.crm.projectvendors;

import com.example.crm.companies.Company;
import com.example.crm.common.model.AuditSupport;
import com.example.crm.projects.Project;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_vendors")
public class ProjectVendor extends AuditSupport {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Company vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_associate_id")
    private User responsibleAssociate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OutreachStatus status = OutreachStatus.NOT_CONTACTED;

    private LocalDate firstContactDate;
    private LocalDate lastContactDate;
    private LocalDate nextFollowUpDate;

    @Column(length = 4000)
    private String notes;

    private OffsetDateTime deletedAt;

    protected ProjectVendor() {}

    public ProjectVendor(Project project, Company vendor) {
        this.project = project;
        this.vendor = vendor;
    }

    @Override
    protected void assignIdIfNecessary() {
        if (id == null) id = newId();
    }

    public UUID getId() { return id; }
    public Project getProject() { return project; }
    public Company getVendor() { return vendor; }
    public User getResponsibleAssociate() { return responsibleAssociate; }
    public OutreachStatus getStatus() { return status; }
    public LocalDate getFirstContactDate() { return firstContactDate; }
    public LocalDate getLastContactDate() { return lastContactDate; }
    public LocalDate getNextFollowUpDate() { return nextFollowUpDate; }
    public String getNotes() { return notes; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setResponsibleAssociate(User responsibleAssociate) { this.responsibleAssociate = responsibleAssociate; }
    public void setStatus(OutreachStatus status) { this.status = status; }
    public void setFirstContactDate(LocalDate firstContactDate) { this.firstContactDate = firstContactDate; }
    public void setLastContactDate(LocalDate lastContactDate) { this.lastContactDate = lastContactDate; }
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
}
