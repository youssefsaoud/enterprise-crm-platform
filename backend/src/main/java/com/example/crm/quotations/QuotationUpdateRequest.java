package com.example.crm.quotations;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record QuotationUpdateRequest(
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    @NotNull @Pattern(regexp = "^[A-Z]{3}$", message = "must be a three-letter uppercase currency code") String currencyCode,
    @NotNull LocalDate receivedDate,
    LocalDate validUntil,
    @NotNull QuotationStatus status,
    @Size(max = 4000) String notes
) {}
