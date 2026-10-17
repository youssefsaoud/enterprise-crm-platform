package com.example.crm.interactions;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class InteractionController {
    private final InteractionService interactionService;
    public InteractionController(InteractionService interactionService) { this.interactionService = interactionService; }

    @PostMapping("/interactions")
    ResponseEntity<InteractionResponse> create(@Valid @RequestBody InteractionCreateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { InteractionResponse response = interactionService.create(request, principal); return ResponseEntity.created(URI.create("/api/v1/interactions/" + response.id())).body(response); }

    @GetMapping("/interactions/{id}")
    InteractionResponse get(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) { return interactionService.get(id, principal); }

    @GetMapping("/interactions")
    Page<InteractionResponse> search(@AuthenticationPrincipal CrmUserPrincipal principal, @RequestParam(required = false) InteractionType type, @RequestParam(required = false) UUID creatorId, @RequestParam(required = false) UUID companyId, @RequestParam(required = false) UUID contactId, @RequestParam(required = false) UUID leadId, @RequestParam(required = false) UUID opportunityId, @RequestParam(required = false) UUID projectId, @RequestParam(required = false) UUID projectVendorId, @RequestParam(required = false) OffsetDateTime fromDate, @RequestParam(required = false) OffsetDateTime toDate, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return interactionService.search(principal, type, creatorId, companyId, contactId, leadId, opportunityId, projectId, projectVendorId, fromDate, toDate, pageable);
    }

    @PatchMapping("/interactions/{id}")
    InteractionResponse update(@PathVariable UUID id, @Valid @RequestBody InteractionUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { return interactionService.update(id, request, principal); }
    @DeleteMapping("/interactions/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) { interactionService.delete(id, principal); return ResponseEntity.noContent().build(); }

    @GetMapping("/leads/{id}/interactions")
    Page<InteractionResponse> leadTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, null, null, id, null, null, null, null, null, pageable); }
    @GetMapping("/companies/{id}/interactions")
    Page<InteractionResponse> companyTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, id, null, null, null, null, null, pageable); }
    @GetMapping("/contacts/{id}/interactions")
    Page<InteractionResponse> contactTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, null, id, null, null, null, null, pageable); }
    @GetMapping("/opportunities/{id}/interactions")
    Page<InteractionResponse> opportunityTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, null, null, id, null, null, null, pageable); }
    @GetMapping("/projects/{id}/interactions")
    Page<InteractionResponse> projectTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, null, null, null, null, id, null, null, null, pageable); }
    @GetMapping("/project-vendors/{id}/interactions")
    Page<InteractionResponse> projectVendorTimeline(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return interactionService.search(principal, null, null, null, null, null, null, null, id, null, null, pageable); }
}
