package com.example.crm.quotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record QuotationResponse(
    UUID id,
    UUID projectVendorId,
    UUID projectId,
    String projectName,
    String vendorName,
    BigDecimal amount,
    String currencyCode,
    LocalDate receivedDate,
    LocalDate validUntil,
    QuotationStatus status,
    int revisionNumber,
    boolean selected,
    String notes,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static QuotationResponse from(Quotation value) {
        return new QuotationResponse(
            value.getId(), value.getProjectVendor().getId(), value.getProjectVendor().getProject().getId(),
            value.getProjectVendor().getProject().getName(), value.getProjectVendor().getVendor().getName(),
            value.getAmount(), value.getCurrencyCode(), value.getReceivedDate(), value.getValidUntil(),
            value.getStatus(), value.getRevisionNumber(), value.isSelected(), value.getNotes(),
            value.getCreatedAt(), value.getUpdatedAt()
        );
    }
}
