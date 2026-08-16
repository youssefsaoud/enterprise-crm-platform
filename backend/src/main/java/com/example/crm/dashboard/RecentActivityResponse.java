package com.example.crm.dashboard;

import com.example.crm.interactions.InteractionType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RecentActivityResponse(
    UUID id,
    InteractionType type,
    String subject,
    OffsetDateTime occurredAt,
    String authorName,
    UUID companyId,
    String companyName,
    UUID contactId,
    String contactName,
    UUID leadId,
    String leadName,
    UUID opportunityId,
    String opportunityName
) {}
