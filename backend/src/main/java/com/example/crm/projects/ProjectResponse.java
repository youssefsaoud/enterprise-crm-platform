package com.example.crm.projects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String reference, UUID clientId, String clientName, String description, String category, String location, LocalDate deadline, BigDecimal estimatedBudget, String currencyCode, UUID assignedAssociateId, String assignedAssociateName, ProjectStatus status, String scopeOfWork, String notes, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    static ProjectResponse from(Project project) { return new ProjectResponse(project.getId(), project.getName(), project.getReference(), project.getClient().getId(), project.getClient().getName(), project.getDescription(), project.getCategory(), project.getLocation(), project.getDeadline(), project.getEstimatedBudget(), project.getCurrencyCode(), project.getAssignedAssociate() == null ? null : project.getAssignedAssociate().getId(), project.getAssignedAssociate() == null ? null : project.getAssignedAssociate().getFullName(), project.getStatus(), project.getScopeOfWork(), project.getNotes(), project.getCreatedAt(), project.getUpdatedAt()); }
}