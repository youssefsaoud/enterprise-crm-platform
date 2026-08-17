package com.example.crm.dashboard;

import com.example.crm.interactions.InteractionResponse;
import com.example.crm.interactions.InteractionService;
import com.example.crm.leads.LeadRepository;
import com.example.crm.leads.LeadStatus;
import com.example.crm.leads.LeadSource;
import com.example.crm.opportunities.OpportunityRepository;
import com.example.crm.opportunities.OpportunityStage;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private static final List<OpportunityStage> OPEN_STAGES = List.of(
        OpportunityStage.PROSPECTING,
        OpportunityStage.QUALIFICATION,
        OpportunityStage.PROPOSAL,
        OpportunityStage.NEGOTIATION
    );

    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final InteractionService interactionService;

    public DashboardService(LeadRepository leadRepository, OpportunityRepository opportunityRepository, InteractionService interactionService) {
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.interactionService = interactionService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(CrmUserPrincipal principal) {
        boolean canSeeAll = canSeeAll(principal);
        UUID userId = principal.id();
        long totalLeads = leadRepository.searchVisible(userId, canSeeAll, null, null, null, null, null, PageRequest.of(0, 1)).getTotalElements();
        List<DashboardLeadStatusResponse> leadStatuses = leadStatusCounts(userId, canSeeAll);
        List<PipelineStageSummaryResponse> pipeline = pipeline(principal);
        long openOpportunityCount = pipeline.stream()
            .filter(row -> OPEN_STAGES.contains(row.stage()))
            .mapToLong(PipelineStageSummaryResponse::count)
            .sum();
        List<DashboardCurrencyTotalResponse> openPipelineValue = openPipelineValue(pipeline);
        long recentInteractionCount = interactionService.search(principal, null, null, null, null, null, null, OffsetDateTime.now().minusDays(7), null, PageRequest.of(0, 1)).getTotalElements();
        return new DashboardSummaryResponse(totalLeads, leadStatuses, openOpportunityCount, openPipelineValue, recentInteractionCount);
    }

    @Transactional(readOnly = true)
    public List<PipelineStageSummaryResponse> pipeline(CrmUserPrincipal principal) {
        return opportunityRepository.pipelineSummary(principal.id(), canSeeAll(principal)).stream()
            .map(row -> new PipelineStageSummaryResponse(row.getStage(), row.getCurrencyCode(), row.getCount(), row.getTotalAmount()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RecentActivityResponse> recentActivities(CrmUserPrincipal principal, int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 25));
        return interactionService.search(principal, null, null, null, null, null, null, null, null, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "occurredAt")))
            .getContent()
            .stream()
            .map(this::activityFrom)
            .toList();
    }

    private List<DashboardLeadStatusResponse> leadStatusCounts(UUID userId, boolean canSeeAll) {
        Map<LeadStatus, Long> counts = new EnumMap<>(LeadStatus.class);
        for (LeadStatus status : LeadStatus.values()) {
            long count = leadRepository.searchVisible(userId, canSeeAll, status, (LeadSource) null, null, null, null, PageRequest.of(0, 1)).getTotalElements();
            counts.put(status, count);
        }
        return counts.entrySet().stream().map(entry -> new DashboardLeadStatusResponse(entry.getKey(), entry.getValue())).toList();
    }

    private List<DashboardCurrencyTotalResponse> openPipelineValue(List<PipelineStageSummaryResponse> pipeline) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        pipeline.stream()
            .filter(row -> OPEN_STAGES.contains(row.stage()))
            .forEach(row -> totals.merge(row.currencyCode(), row.totalAmount(), BigDecimal::add));
        return totals.entrySet().stream()
            .map(entry -> new DashboardCurrencyTotalResponse(entry.getKey(), entry.getValue()))
            .toList();
    }

    private RecentActivityResponse activityFrom(InteractionResponse interaction) {
        return new RecentActivityResponse(
            interaction.id(),
            interaction.type(),
            interaction.subject(),
            interaction.occurredAt(),
            interaction.authorName(),
            interaction.companyId(),
            interaction.companyName(),
            interaction.contactId(),
            interaction.contactName(),
            interaction.leadId(),
            interaction.leadName(),
            interaction.opportunityId(),
            interaction.opportunityName()
        );
    }

    private boolean canSeeAll(CrmUserPrincipal principal) {
        return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER;
    }
}

