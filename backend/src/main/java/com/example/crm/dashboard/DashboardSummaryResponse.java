package com.example.crm.dashboard;

import java.util.List;

public record DashboardSummaryResponse(
    long totalLeads,
    List<DashboardLeadStatusResponse> leadsByStatus,
    long openOpportunities,
    List<DashboardCurrencyTotalResponse> openPipelineValue,
    long recentInteractionCount
) {}
