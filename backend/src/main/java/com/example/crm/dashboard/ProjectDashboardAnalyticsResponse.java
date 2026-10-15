package com.example.crm.dashboard;

import com.example.crm.users.Role;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectDashboardAnalyticsResponse(
    Role role,
    DashboardDateRange dateRange,
    LocalDate trendStartDate,
    LocalDate trendEndDate,
    String trendGranularity,
    UUID selectedAssociateId,
    String selectedAssociateName,
    long totalProjects,
    long activeProjects,
    long completedProjects,
    long nearingDeadlineProjects,
    long overdueProjects,
    long totalVendorAssignments,
    long vendorsAwaitingResponse,
    long quotationsReceived,
    long vendorAssignmentsWithQuotes,
    double quoteResponseRate,
    long followUpsDueToday,
    long overdueFollowUps,
    long upcomingFollowUps,
    List<ProjectStatusCountResponse> projectsByStatus,
    List<OutreachStatusCountResponse> vendorOutreachByStatus,
    List<QuotationTrendPointResponse> quotationTrends,
    List<AssociateWorkloadResponse> associateWorkloads,
    List<DashboardCurrencyTotalResponse> quotationValueByCurrency
) {}
