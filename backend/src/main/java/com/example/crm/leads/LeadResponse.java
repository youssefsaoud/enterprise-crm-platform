package com.example.crm.leads;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String companyName,
    UUID companyId,
    String linkedCompanyName,
    LeadSource source,
    LeadStatus status,
    UUID assignedToUserId,
    String assignedToName,
    UUID createdByUserId,
    String createdByName,
    OffsetDateTime assignedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static LeadResponse from(Lead lead) {
        return new LeadResponse(
            lead.getId(),
            lead.getFirstName(),
            lead.getLastName(),
            lead.getEmail(),
            lead.getPhone(),
            lead.getCompanyName(),
            lead.getCompany() == null ? null : lead.getCompany().getId(),
            lead.getCompany() == null ? null : lead.getCompany().getName(),
            lead.getSource(),
            lead.getStatus(),
            lead.getAssignedTo() == null ? null : lead.getAssignedTo().getId(),
            lead.getAssignedTo() == null ? null : lead.getAssignedTo().getFullName(),
            lead.getCreatedBy().getId(),
            lead.getCreatedBy().getFullName(),
            lead.getAssignedAt(),
            lead.getCreatedAt(),
            lead.getUpdatedAt()
        );
    }
}
