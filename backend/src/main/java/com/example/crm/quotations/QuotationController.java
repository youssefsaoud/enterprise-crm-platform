package com.example.crm.quotations;

import com.example.crm.security.CrmUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class QuotationController {
    private final QuotationService service;
    public QuotationController(QuotationService service) { this.service = service; }

    @GetMapping("/project-vendors/{projectVendorId}/quotations")
    List<QuotationResponse> list(@PathVariable UUID projectVendorId, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.list(projectVendorId, principal); }

    @PostMapping("/project-vendors/{projectVendorId}/quotations")
    ResponseEntity<QuotationResponse> create(@PathVariable UUID projectVendorId, @Valid @RequestBody QuotationCreateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { QuotationResponse response = service.create(projectVendorId, request, principal); return ResponseEntity.created(URI.create("/api/v1/quotations/" + response.id())).body(response); }

    @PutMapping("/quotations/{id}")
    QuotationResponse update(@PathVariable UUID id, @Valid @RequestBody QuotationUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.update(id, request, principal); }

    @PostMapping("/quotations/{id}/select")
    @Operation(summary = "Select the winning project quotation", description = "Selects this quotation transactionally and replaces any previous selection. Existing role and project-ownership checks apply.")
    QuotationResponse select(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.select(id, principal); }
}
