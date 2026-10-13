package com.example.crm.dashboard;

import com.example.crm.interactions.InteractionType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectDashboardActivityResponse(UUID id, InteractionType type, String subject, OffsetDateTime occurredAt, String authorName, UUID projectId, String projectName, UUID projectVendorId, String vendorName) {}
