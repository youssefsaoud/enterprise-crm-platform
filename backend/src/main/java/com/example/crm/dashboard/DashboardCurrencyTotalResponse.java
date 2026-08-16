package com.example.crm.dashboard;

import java.math.BigDecimal;

public record DashboardCurrencyTotalResponse(
    String currencyCode,
    BigDecimal amount
) {}
