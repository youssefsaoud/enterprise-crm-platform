package com.example.crm.leads;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadEventRepository extends JpaRepository<LeadEvent, UUID> {
    @EntityGraph(attributePaths = "performedBy")
    Page<LeadEvent> findByLeadOrderByCreatedAtDesc(Lead lead, Pageable pageable);
}
