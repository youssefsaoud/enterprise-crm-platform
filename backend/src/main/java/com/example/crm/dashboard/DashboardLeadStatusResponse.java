package com.example.crm.dashboard;

import com.example.crm.leads.LeadStatus;

public record DashboardLeadStatusResponse(
    LeadStatus status,
    long count
) {}
