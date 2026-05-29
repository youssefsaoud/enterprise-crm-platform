package com.example.crm.opportunities;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OpportunityOwnerRequest(@NotNull UUID ownerUserId) {}
