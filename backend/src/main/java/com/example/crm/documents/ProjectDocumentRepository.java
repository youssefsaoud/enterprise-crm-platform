package com.example.crm.documents;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, UUID> {
    @EntityGraph(attributePaths = {"project", "quotation", "uploadedBy"})
    List<ProjectDocument> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    @EntityGraph(attributePaths = {"project", "quotation", "uploadedBy"})
    java.util.Optional<ProjectDocument> findWithDetailsById(UUID id);
}
