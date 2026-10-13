package com.example.crm.companies;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record CompanyResponse(UUID id, String name, String industry, String website, String phone, String email, String address, Set<CompanyRole> roles, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    static CompanyResponse from(Company company) {
        return new CompanyResponse(company.getId(), company.getName(), company.getIndustry(), company.getWebsite(), company.getPhone(), company.getEmail(), company.getAddress(), Set.copyOf(company.getRoles()), company.getCreatedAt(), company.getUpdatedAt());
    }
}