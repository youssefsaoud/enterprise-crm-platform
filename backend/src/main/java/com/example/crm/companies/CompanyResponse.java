package com.example.crm.companies;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanyResponse(
    UUID id,
    String name,
    String industry,
    String website,
    String phone,
    String email,
    String address,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static CompanyResponse from(Company company) {
        return new CompanyResponse(
            company.getId(),
            company.getName(),
            company.getIndustry(),
            company.getWebsite(),
            company.getPhone(),
            company.getEmail(),
            company.getAddress(),
            company.getCreatedAt(),
            company.getUpdatedAt()
        );
    }
}
