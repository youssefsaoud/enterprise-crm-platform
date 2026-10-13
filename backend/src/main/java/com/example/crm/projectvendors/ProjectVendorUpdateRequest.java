package com.example.crm.projectvendors;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectVendorUpdateRequest(
    UUID responsibleAssociateId,
    OutreachStatus status,
    LocalDate firstContactDate,
    LocalDate lastContactDate,
    LocalDate nextFollowUpDate,
    @Size(max = 4000) String notes
) {}
