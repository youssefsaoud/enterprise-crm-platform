package com.example.crm.dashboard;

import com.example.crm.security.CrmUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project-dashboard")
public class ProjectDashboardController {
    private final ProjectDashboardService service;
    public ProjectDashboardController(ProjectDashboardService service) { this.service = service; }

    @GetMapping("/summary")
    ProjectDashboardSummaryResponse summary(@AuthenticationPrincipal CrmUserPrincipal principal) { return service.summary(principal); }
}
