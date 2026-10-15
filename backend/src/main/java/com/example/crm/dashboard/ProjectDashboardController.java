package com.example.crm.dashboard;

import com.example.crm.security.CrmUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project-dashboard")
public class ProjectDashboardController {
    private final ProjectDashboardService service;
    private final ProjectDashboardAnalyticsService analyticsService;
    public ProjectDashboardController(ProjectDashboardService service, ProjectDashboardAnalyticsService analyticsService) {
        this.service = service;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    ProjectDashboardSummaryResponse summary(@AuthenticationPrincipal CrmUserPrincipal principal) { return service.summary(principal); }

    @GetMapping("/analytics")
    ProjectDashboardAnalyticsResponse analytics(
        @AuthenticationPrincipal CrmUserPrincipal principal,
        @RequestParam(defaultValue = "LAST_90_DAYS") DashboardDateRange range,
        @RequestParam(required = false) UUID associateId
    ) {
        return analyticsService.analytics(principal, range, associateId);
    }
}
