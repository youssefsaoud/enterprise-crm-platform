package com.example.crm.opportunities;

import org.springframework.data.jpa.repository.JpaRepository;

interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
}

