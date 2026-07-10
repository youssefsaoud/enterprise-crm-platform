package com.example.crm.leads;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadConversionRepository extends JpaRepository<LeadConversion, UUID> {
    boolean existsByLeadId(UUID leadId);

    @EntityGraph(attributePaths = {"lead", "company", "contact", "opportunity", "convertedBy"})
    Optional<LeadConversion> findByLeadId(UUID leadId);
}
