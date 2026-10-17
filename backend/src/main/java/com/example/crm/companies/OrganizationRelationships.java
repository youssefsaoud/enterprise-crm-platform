package com.example.crm.companies;

import java.util.UUID;

public record OrganizationRelationships(UUID companyId, long clientProjects, long vendorProjects) {}
