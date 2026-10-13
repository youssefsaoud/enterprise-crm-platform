package com.example.crm.projects;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectUpdateRequest(@NotBlank @Size(max = 180) String name, @NotBlank @Size(max = 40) String reference, @NotNull UUID clientId, @Size(max = 4000) String description, @Size(max = 120) String category, @Size(max = 255) String location, LocalDate deadline, @DecimalMin("0.00") BigDecimal estimatedBudget, @Pattern(regexp = "^[A-Z]{3}$", message = "must be a three-letter uppercase currency code") String currencyCode, UUID assignedAssociateId, @NotNull ProjectStatus status, @Size(max = 10000) String scopeOfWork, @Size(max = 4000) String notes) {}