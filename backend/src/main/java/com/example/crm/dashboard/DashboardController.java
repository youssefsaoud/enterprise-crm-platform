package com.example.crm.dashboard;

import com.example.crm.security.CrmUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController { @GetMapping("/summary") DashboardSummaryResponse summary(@AuthenticationPrincipal CrmUserPrincipal principal) { return null; } }

