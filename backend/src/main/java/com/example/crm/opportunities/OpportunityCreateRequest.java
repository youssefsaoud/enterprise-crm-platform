package com.example.crm.opportunities;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OpportunityCreateRequest(
    @NotBlank @Size(min = 3, max = 160) String name,
    @NotNull UUID companyId,
    UUID contactId,
    UUID ownerUserId,
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    @Pattern(regexp = "^[A-Z]{3}$") String currencyCode,
    LocalDate expectedCloseDate,
    @Size(max = 4000) String description
) {}
