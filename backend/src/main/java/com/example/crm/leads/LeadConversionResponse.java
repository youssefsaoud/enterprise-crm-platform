package com.example.crm.leads;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadConversionResponse(
    UUID id,
    UUID leadId,
    UUID companyId,
    String companyName,
    UUID contactId,
    String contactName,
    UUID opportunityId,
    String opportunityName,
    UUID convertedByUserId,
    String convertedByName,
    OffsetDateTime convertedAt,
    String notes
) {
    public static LeadConversionResponse from(LeadConversion conversion) {
        return new LeadConversionResponse(
            conversion.getId(),
            conversion.getLead().getId(),
            conversion.getCompany().getId(),
            conversion.getCompany().getName(),
            conversion.getContact().getId(),
            conversion.getContact().getFirstName() + " " + conversion.getContact().getLastName(),
            conversion.getOpportunity() == null ? null : conversion.getOpportunity().getId(),
            conversion.getOpportunity() == null ? null : conversion.getOpportunity().getName(),
            conversion.getConvertedBy().getId(),
            conversion.getConvertedBy().getFullName(),
            conversion.getConvertedAt(),
            conversion.getNotes()
        );
    }
}
