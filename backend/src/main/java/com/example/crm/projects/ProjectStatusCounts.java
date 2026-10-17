package com.example.crm.projects;

import java.util.List;

public record ProjectStatusCounts(long total, List<ProjectStatusSummary> statuses) {}
