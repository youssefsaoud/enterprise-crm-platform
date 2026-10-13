package com.example.crm.projectvendors;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectVendorResponse(
    UUID id,
    UUID projectId,
    String projectName,
    String projectReference,
    UUID vendorId,
    String vendorName,
    String vendorIndustry,
    UUID responsibleAssociateId,
    String responsibleAssociateName,
    OutreachStatus status,
    LocalDate firstContactDate,
    LocalDate lastContactDate,
    LocalDate nextFollowUpDate,
    String notes,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static ProjectVendorResponse from(ProjectVendor value) {
        return new ProjectVendorResponse(
            value.getId(), value.getProject().getId(), value.getProject().getName(), value.getProject().getReference(),
            value.getVendor().getId(), value.getVendor().getName(), value.getVendor().getIndustry(),
            value.getResponsibleAssociate() == null ? null : value.getResponsibleAssociate().getId(),
            value.getResponsibleAssociate() == null ? null : value.getResponsibleAssociate().getFullName(),
            value.getStatus(), value.getFirstContactDate(), value.getLastContactDate(), value.getNextFollowUpDate(),
            value.getNotes(), value.getCreatedAt(), value.getUpdatedAt()
        );
    }
}
