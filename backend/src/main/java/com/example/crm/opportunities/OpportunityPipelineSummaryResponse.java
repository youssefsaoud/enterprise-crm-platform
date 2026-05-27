package com.example.crm.opportunities;

import java.math.BigDecimal;

public record OpportunityPipelineSummaryResponse(OpportunityStage stage, String currencyCode, long count, BigDecimal totalAmount) {
    static OpportunityPipelineSummaryResponse from(OpportunityPipelineRow row) {
        return new OpportunityPipelineSummaryResponse(row.getStage(), row.getCurrencyCode(), row.getCount(), row.getTotalAmount());
    }
}
