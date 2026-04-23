package com.example.crm.leads;

import jakarta.validation.constraints.NotNull;

public record LeadStatusRequest(@NotNull LeadStatus status) {}
