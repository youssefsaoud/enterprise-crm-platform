package com.example.crm.dashboard;

import com.example.crm.interactions.InteractionService;
import com.example.crm.leads.LeadRepository;
import com.example.crm.opportunities.OpportunityRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final InteractionService interactionService;
    public DashboardService(LeadRepository leadRepository, OpportunityRepository opportunityRepository, InteractionService interactionService) { this.leadRepository = leadRepository; this.opportunityRepository = opportunityRepository; this.interactionService = interactionService; }
}

