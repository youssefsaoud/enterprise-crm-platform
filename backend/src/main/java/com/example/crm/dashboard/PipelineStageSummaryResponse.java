package com.example.crm.dashboard;

import com.example.crm.opportunities.OpportunityStage;
import java.math.BigDecimal;

public record PipelineStageSummaryResponse(
    OpportunityStage stage,
    String currencyCode,
    long count,
    BigDecimal totalAmount
) {}
