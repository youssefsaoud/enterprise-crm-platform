package com.example.crm.documents;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectDocumentResponse(UUID id, UUID projectId, UUID quotationId, String originalFilename, String contentType, long fileSize, UUID uploadedById, String uploadedByName, OffsetDateTime uploadedAt) {
    static ProjectDocumentResponse from(ProjectDocument value) {
        return new ProjectDocumentResponse(value.getId(), value.getProject().getId(), value.getQuotation() == null ? null : value.getQuotation().getId(), value.getOriginalFilename(), value.getContentType(), value.getFileSize(), value.getUploadedBy().getId(), value.getUploadedBy().getFullName(), value.getCreatedAt());
    }
}
