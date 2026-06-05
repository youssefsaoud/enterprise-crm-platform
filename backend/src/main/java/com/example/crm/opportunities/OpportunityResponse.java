package com.example.crm.opportunities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OpportunityResponse(
    UUID id,
    String name,
    UUID companyId,
    String companyName,
    UUID contactId,
    String contactName,
    UUID ownerUserId,
    String ownerName,
    OpportunityStage stage,
    BigDecimal amount,
    String currencyCode,
    LocalDate expectedCloseDate,
    String description,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static OpportunityResponse from(Opportunity opportunity) {
        String contactName = opportunity.getContact() == null ? null : opportunity.getContact().getFirstName() + " " + opportunity.getContact().getLastName();
        return new OpportunityResponse(
            opportunity.getId(), opportunity.getName(),
            opportunity.getCompany().getId(), opportunity.getCompany().getName(),
            opportunity.getContact() == null ? null : opportunity.getContact().getId(), contactName,
            opportunity.getOwner() == null ? null : opportunity.getOwner().getId(),
            opportunity.getOwner() == null ? null : opportunity.getOwner().getFullName(),
            opportunity.getStage(), opportunity.getAmount(), opportunity.getCurrencyCode(),
            opportunity.getExpectedCloseDate(), opportunity.getDescription(),
            opportunity.getCreatedAt(), opportunity.getUpdatedAt()
        );
    }
}
