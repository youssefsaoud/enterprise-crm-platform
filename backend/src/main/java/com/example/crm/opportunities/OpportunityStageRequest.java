package com.example.crm.opportunities;

import jakarta.validation.constraints.NotNull;

public record OpportunityStageRequest(@NotNull OpportunityStage stage) {}
