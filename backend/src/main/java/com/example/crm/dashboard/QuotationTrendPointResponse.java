package com.example.crm.dashboard;

import java.time.LocalDate;

public record QuotationTrendPointResponse(LocalDate periodStart, long quotationCount) {}
