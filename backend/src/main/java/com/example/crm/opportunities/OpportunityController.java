package com.example.crm.opportunities;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
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
@RequestMapping("/api/v1/opportunities")
public class OpportunityController {
    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    Page<OpportunityResponse> search(@AuthenticationPrincipal CrmUserPrincipal principal, @RequestParam(required = false) String search, @RequestParam(required = false) OpportunityStage stage, @RequestParam(required = false) UUID ownerId, @RequestParam(required = false) UUID companyId, @RequestParam(required = false) LocalDate closeDateFrom, @RequestParam(required = false) LocalDate closeDateTo, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return opportunityService.search(principal, search, stage, ownerId, companyId, closeDateFrom, closeDateTo, pageable);
    }

    @GetMapping("/{id}")
    OpportunityResponse get(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return opportunityService.get(id, principal);
    }

    @PostMapping
    ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityCreateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        OpportunityResponse response = opportunityService.create(request, principal);
        return ResponseEntity.created(URI.create("/api/v1/opportunities/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    OpportunityResponse update(@PathVariable UUID id, @Valid @RequestBody OpportunityUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return opportunityService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        opportunityService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stage")
    OpportunityResponse changeStage(@PathVariable UUID id, @Valid @RequestBody OpportunityStageRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return opportunityService.changeStage(id, request, principal);
    }

    @PatchMapping("/{id}/owner")
    OpportunityResponse changeOwner(@PathVariable UUID id, @Valid @RequestBody OpportunityOwnerRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return opportunityService.changeOwner(id, request, principal);
    }

    @GetMapping("/pipeline/summary")
    List<OpportunityPipelineSummaryResponse> pipelineSummary(@AuthenticationPrincipal CrmUserPrincipal principal) {
        return opportunityService.pipelineSummary(principal);
    }
}
