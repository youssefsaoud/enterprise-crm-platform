package com.example.crm.projects;

import com.example.crm.companies.Company;
import com.example.crm.common.model.AuditSupport;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "projects")
public class Project extends AuditSupport {
    @Id private UUID id;
    @Column(nullable = false, length = 180) private String name;
    @Column(name = "reference", nullable = false, length = 40, unique = true) private String reference;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "client_id", nullable = false) private Company client;
    @Column(length = 4000) private String description;
    @Column(length = 120) private String category;
    @Column(length = 255) private String location;
    private LocalDate deadline;
    @Column(precision = 14, scale = 2) private BigDecimal estimatedBudget;
    @Column(nullable = false, length = 3) private String currencyCode = "USD";
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_associate_id") private User assignedAssociate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private ProjectStatus status = ProjectStatus.PLANNED;
    @Column(name = "scope_of_work", columnDefinition = "TEXT") private String scopeOfWork;
    @Column(length = 4000) private String notes;
    private OffsetDateTime deletedAt;
    protected Project() {}
    public Project(String name, String reference, Company client) { this.name = name; this.reference = reference; this.client = client; }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Company getClient() { return client; }
    public void setClient(Company client) { this.client = client; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public BigDecimal getEstimatedBudget() { return estimatedBudget; }
    public void setEstimatedBudget(BigDecimal estimatedBudget) { this.estimatedBudget = estimatedBudget; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public User getAssignedAssociate() { return assignedAssociate; }
    public void setAssignedAssociate(User assignedAssociate) { this.assignedAssociate = assignedAssociate; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public String getScopeOfWork() { return scopeOfWork; }
    public void setScopeOfWork(String scopeOfWork) { this.scopeOfWork = scopeOfWork; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
}