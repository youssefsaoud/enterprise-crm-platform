package com.example.crm.dashboard;

import com.example.crm.projects.ProjectStatus;

public record ProjectStatusCountResponse(ProjectStatus status, long count) {}
