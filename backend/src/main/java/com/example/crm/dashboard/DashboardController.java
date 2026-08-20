package com.example.crm.dashboard;

import com.example.crm.security.CrmUserPrincipal;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    DashboardSummaryResponse summary(@AuthenticationPrincipal CrmUserPrincipal principal) {
        return dashboardService.summary(principal);
    }

    @GetMapping("/pipeline")
    List<PipelineStageSummaryResponse> pipeline(@AuthenticationPrincipal CrmUserPrincipal principal) {
        return dashboardService.pipeline(principal);
    }

    @GetMapping("/recent-activities")
    List<RecentActivityResponse> recentActivities(@AuthenticationPrincipal CrmUserPrincipal principal, @RequestParam(defaultValue = "10") int limit) {
        return dashboardService.recentActivities(principal, limit);
    }
}
