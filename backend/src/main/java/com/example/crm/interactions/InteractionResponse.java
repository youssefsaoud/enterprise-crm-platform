package com.example.crm.interactions;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InteractionResponse(
    UUID id, InteractionType type, String subject, String description, OffsetDateTime occurredAt, OffsetDateTime followUpAt,
    UUID authorUserId, String authorName, UUID companyId, String companyName, UUID contactId, String contactName,
    UUID leadId, String leadName, UUID opportunityId, String opportunityName, UUID projectId, String projectName,
    UUID projectVendorId, String projectVendorName, UUID quotationId, OffsetDateTime createdAt, OffsetDateTime updatedAt
) {
    public InteractionResponse(UUID id, InteractionType type, String subject, String description, OffsetDateTime occurredAt, OffsetDateTime followUpAt, UUID authorUserId, String authorName, UUID companyId, String companyName, UUID contactId, String contactName, UUID leadId, String leadName, UUID opportunityId, String opportunityName, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this(id, type, subject, description, occurredAt, followUpAt, authorUserId, authorName, companyId, companyName, contactId, contactName, leadId, leadName, opportunityId, opportunityName, null, null, null, null, null, createdAt, updatedAt);
    }

    public static InteractionResponse from(Interaction interaction) {
        return new InteractionResponse(
            interaction.getId(), interaction.getType(), interaction.getSubject(), interaction.getDescription(), interaction.getOccurredAt(), interaction.getFollowUpAt(),
            interaction.getAuthor().getId(), interaction.getAuthor().getFullName(),
            interaction.getCompany() == null ? null : interaction.getCompany().getId(), interaction.getCompany() == null ? null : interaction.getCompany().getName(),
            interaction.getContact() == null ? null : interaction.getContact().getId(), interaction.getContact() == null ? null : interaction.getContact().getFirstName() + " " + interaction.getContact().getLastName(),
            interaction.getLead() == null ? null : interaction.getLead().getId(), interaction.getLead() == null ? null : interaction.getLead().getFirstName() + " " + interaction.getLead().getLastName(),
            interaction.getOpportunity() == null ? null : interaction.getOpportunity().getId(), interaction.getOpportunity() == null ? null : interaction.getOpportunity().getName(),
            interaction.getProject() == null ? null : interaction.getProject().getId(), interaction.getProject() == null ? null : interaction.getProject().getName(),
            interaction.getProjectVendor() == null ? null : interaction.getProjectVendor().getId(), interaction.getProjectVendor() == null ? null : interaction.getProjectVendor().getVendor().getName(),
            interaction.getQuotation() == null ? null : interaction.getQuotation().getId(), interaction.getCreatedAt(), interaction.getUpdatedAt()
        );
    }
}
