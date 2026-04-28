package com.example.crm.leads;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    Page<LeadResponse> search(
        @AuthenticationPrincipal CrmUserPrincipal principal,
        @RequestParam(required = false) LeadStatus status,
        @RequestParam(required = false) LeadSource source,
        @RequestParam(required = false) UUID assigneeId,
        @RequestParam(required = false) UUID companyId,
        @RequestParam(required = false) String keyword,
        Pageable pageable
    ) {
        return leadService.search(principal, status, source, assigneeId, companyId, keyword, pageable);
    }

    @GetMapping("/{id}")
    LeadResponse get(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadService.get(id, principal);
    }

    @PostMapping
    ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadCreateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        LeadResponse response = leadService.create(request, principal);
        return ResponseEntity.created(URI.create("/api/v1/leads/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    LeadResponse update(@PathVariable UUID id, @Valid @RequestBody LeadUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        leadService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assignment")
    LeadResponse assign(@PathVariable UUID id, @RequestBody LeadAssignmentRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadService.assign(id, request, principal);
    }

    @PatchMapping("/{id}/status")
    LeadResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody LeadStatusRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadService.changeStatus(id, request, principal);
    }

    @GetMapping("/{id}/history")
    Page<LeadEventResponse> history(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, Pageable pageable) {
        return leadService.history(id, principal, pageable);
    }
}
