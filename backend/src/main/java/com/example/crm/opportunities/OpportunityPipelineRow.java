package com.example.crm.opportunities;

import java.math.BigDecimal;

public interface OpportunityPipelineRow {
    OpportunityStage getStage();
    String getCurrencyCode();
    long getCount();
    BigDecimal getTotalAmount();
}
