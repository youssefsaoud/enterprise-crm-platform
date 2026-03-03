package com.example.crm.contacts;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContactResponse(
    UUID id,
    UUID companyId,
    String companyName,
    String firstName,
    String lastName,
    String email,
    String phone,
    String jobTitle,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    static ContactResponse from(Contact contact) {
        return new ContactResponse(
            contact.getId(),
            contact.getCompany().getId(),
            contact.getCompany().getName(),
            contact.getFirstName(),
            contact.getLastName(),
            contact.getEmail(),
            contact.getPhone(),
            contact.getJobTitle(),
            contact.getCreatedAt(),
            contact.getUpdatedAt()
        );
    }
}
