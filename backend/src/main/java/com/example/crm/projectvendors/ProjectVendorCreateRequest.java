package com.example.crm.projectvendors;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectVendorCreateRequest(
    @NotNull UUID vendorId,
    UUID responsibleAssociateId,
    OutreachStatus status,
    LocalDate firstContactDate,
    LocalDate lastContactDate,
    LocalDate nextFollowUpDate,
    @Size(max = 4000) String notes
) {}
