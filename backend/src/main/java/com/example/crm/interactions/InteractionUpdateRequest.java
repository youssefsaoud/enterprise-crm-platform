package com.example.crm.interactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record InteractionUpdateRequest(
    @NotNull InteractionType type,
    @NotBlank @Size(min = 3, max = 160) String subject,
    @Size(max = 4000) String description,
    @NotNull OffsetDateTime occurredAt,
    OffsetDateTime followUpAt
) {}
