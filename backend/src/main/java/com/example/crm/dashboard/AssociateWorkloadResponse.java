package com.example.crm.dashboard;

import java.util.UUID;

public record AssociateWorkloadResponse(
    UUID associateId,
    String associateName,
    long activeProjects,
    long dueFollowUps
) {}
