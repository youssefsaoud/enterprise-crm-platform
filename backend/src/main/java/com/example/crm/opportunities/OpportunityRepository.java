package com.example.crm.opportunities;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    List<Opportunity> findByStage(OpportunityStage stage);
}
