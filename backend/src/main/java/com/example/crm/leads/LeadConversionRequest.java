package com.example.crm.leads;

import com.example.crm.opportunities.OpportunityStage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LeadConversionRequest(
    UUID existingCompanyId,
    @Valid NewCompany newCompany,
    UUID existingContactId,
    @Valid NewContact newContact,
    @Valid NewOpportunity opportunity,
    @Size(max = 4000) String notes
) {
    public record NewCompany(
        @NotBlank @Size(min = 2, max = 180) String name,
        @Size(max = 120) String industry,
        @Size(max = 255) String website,
        @Size(max = 60) String phone,
        @Size(max = 180) String email,
        @Size(max = 255) String address
    ) {}

    public record NewContact(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Size(max = 180) String email,
        @Size(max = 60) String phone,
        @Size(max = 120) String jobTitle
    ) {}

    public record NewOpportunity(
        @NotBlank @Size(min = 3, max = 160) String name,
        @DecimalMin("0.00") BigDecimal amount,
        @Pattern(regexp = "^[A-Z]{3}$") String currencyCode,
        LocalDate expectedCloseDate,
        OpportunityStage stage,
        @Size(max = 4000) String description
    ) {}
}
