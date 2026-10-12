package com.example.crm.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.example.crm.interactions.InteractionResponse;
import com.example.crm.interactions.InteractionService;
import com.example.crm.interactions.InteractionType;
import com.example.crm.leads.LeadRepository;
import com.example.crm.leads.LeadStatus;
import com.example.crm.opportunities.OpportunityPipelineRow;
import com.example.crm.opportunities.OpportunityRepository;
import com.example.crm.opportunities.OpportunityStage;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock LeadRepository leadRepository;
    @Mock OpportunityRepository opportunityRepository;
    @Mock InteractionService interactionService;
    @Mock CrmUserPrincipal principal;
    DashboardService service;

    @BeforeEach
    void setUp() {
        service = new DashboardService(leadRepository, opportunityRepository, interactionService);
        lenient().when(principal.id()).thenReturn(UUID.randomUUID());
        lenient().when(principal.role()).thenReturn(Role.SALES_REP);
    }

    @Test
    void summaryAggregatesScopedLeadPipelineAndRecentActivityCounts() {
        when(leadRepository.searchVisible(eq(principal.id()), eq(false), any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of()))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 5))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 2))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 1))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 1))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 0))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 1));
        when(opportunityRepository.pipelineSummary(eq(principal.id()), eq(false))).thenReturn(List.of(
            row(OpportunityStage.PROSPECTING, "USD", 2, "1200.00"),
            row(OpportunityStage.CLOSED_WON, "USD", 1, "700.00")
        ));
        when(interactionService.search(eq(principal), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 3));

        DashboardSummaryResponse response = service.summary(principal);

        assertThat(response.openOpportunities()).isEqualTo(2);
        assertThat(response.openPipelineValue()).singleElement().satisfies(total -> {
            assertThat(total.currencyCode()).isEqualTo("USD");
            assertThat(total.amount()).isEqualByComparingTo("1200.00");
        });
        assertThat(response.recentInteractionCount()).isEqualTo(3);
    }

    @Test
    void recentActivitiesCapsRequestedLimit() {
        InteractionResponse activity = new InteractionResponse(UUID.randomUUID(), InteractionType.CALL, "Call", null, OffsetDateTime.now(), null, UUID.randomUUID(), "Rep", null, null, null, null, null, null, null, null, OffsetDateTime.now(), OffsetDateTime.now());
        when(interactionService.search(eq(principal), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(activity)));

        List<RecentActivityResponse> activities = service.recentActivities(principal, 100);

        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).subject()).isEqualTo("Call");
    }

    private OpportunityPipelineRow row(OpportunityStage stage, String currency, long count, String amount) {
        return new OpportunityPipelineRow() {
            public OpportunityStage getStage() { return stage; }
            public String getCurrencyCode() { return currency; }
            public long getCount() { return count; }
            public BigDecimal getTotalAmount() { return new BigDecimal(amount); }
        };
    }
}
