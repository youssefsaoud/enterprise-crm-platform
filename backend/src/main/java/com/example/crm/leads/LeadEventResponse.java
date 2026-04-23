package com.example.crm.leads;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadEventResponse(
    UUID id,
    LeadEventType eventType,
    String oldValue,
    String newValue,
    UUID performedByUserId,
    String performedByName,
    OffsetDateTime createdAt
) {
    static LeadEventResponse from(LeadEvent event) {
        return new LeadEventResponse(
            event.getId(),
            event.getEventType(),
            event.getOldValue(),
            event.getNewValue(),
            event.getPerformedBy().getId(),
            event.getPerformedBy().getFullName(),
            event.getCreatedAt()
        );
    }
}
