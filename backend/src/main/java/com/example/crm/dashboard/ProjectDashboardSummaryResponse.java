package com.example.crm.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record ProjectDashboardSummaryResponse(
    long totalProjects,
    long activeProjects,
    long nearingDeadlineProjects,
    long overdueProjects,
    long vendorsContacted,
    long vendorsAwaitingResponse,
    long quotationsReceived,
    long quotationsPending,
    double quoteResponseRate,
    long upcomingFollowUps,
    long overdueFollowUps,
    List<ProjectStatusCountResponse> projectsByStatus,
    List<DashboardCurrencyTotalResponse> quotationValueByCurrency,
    List<ProjectDashboardActivityResponse> recentActivities
) {}
