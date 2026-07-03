package com.example.crm.interactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InteractionCreateRequest(
    @NotNull InteractionType type,
    @NotBlank @Size(min = 3, max = 160) String subject,
    @Size(max = 4000) String description,
    @NotNull OffsetDateTime occurredAt,
    OffsetDateTime followUpAt,
    UUID companyId,
    UUID contactId,
    UUID leadId,
    UUID opportunityId
) {}
