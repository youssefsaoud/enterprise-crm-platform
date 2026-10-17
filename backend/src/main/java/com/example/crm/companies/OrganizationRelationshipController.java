package com.example.crm.companies;

import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/companies/relationships")
public class OrganizationRelationshipController {
    private final CompanyRepository companies;
    public OrganizationRelationshipController(CompanyRepository companies) { this.companies = companies; }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public List<OrganizationRelationships> relationships(@RequestParam List<UUID> ids, @AuthenticationPrincipal CrmUserPrincipal principal) {
        if (ids.isEmpty() || ids.size() > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request between 1 and 100 organizations");
        boolean all = principal.role() == Role.ADMIN || principal.role() == Role.MANAGER;
        Map<UUID, CompanyRepository.RelationshipCount> clients = companies.clientRelationships(ids, principal.id(), all).stream()
            .collect(Collectors.toMap(CompanyRepository.RelationshipCount::getCompanyId, Function.identity()));
        Map<UUID, CompanyRepository.RelationshipCount> vendors = companies.vendorRelationships(ids, principal.id(), all).stream()
            .collect(Collectors.toMap(CompanyRepository.RelationshipCount::getCompanyId, Function.identity()));
        return ids.stream().distinct().map(id -> new OrganizationRelationships(id,
            clients.containsKey(id) ? clients.get(id).getCount() : 0,
            vendors.containsKey(id) ? vendors.get(id).getCount() : 0)).toList();
    }
}
