package com.example.crm.projectvendors;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProjectVendorController {
    private final ProjectVendorService service;
    public ProjectVendorController(ProjectVendorService service) { this.service = service; }

    @GetMapping("/projects/{projectId}/vendors")
    Page<ProjectVendorResponse> search(@PathVariable UUID projectId, @RequestParam(required = false) OutreachStatus status, @RequestParam(required = false) String search, Pageable pageable, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.search(projectId, status, search, pageable, principal); }

    @PostMapping("/projects/{projectId}/vendors")
    ResponseEntity<ProjectVendorResponse> add(@PathVariable UUID projectId, @Valid @RequestBody ProjectVendorCreateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { ProjectVendorResponse response = service.add(projectId, request, principal); return ResponseEntity.created(URI.create("/api/v1/project-vendors/" + response.id())).body(response); }

    @PatchMapping("/project-vendors/{id}")
    ProjectVendorResponse update(@PathVariable UUID id, @Valid @RequestBody ProjectVendorUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.update(id, request, principal); }

    @DeleteMapping("/project-vendors/{id}")
    ResponseEntity<Void> remove(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) { service.remove(id, principal); return ResponseEntity.noContent().build(); }

    @GetMapping("/project-vendors/follow-ups")
    java.util.List<ProjectVendorResponse> followUps(@RequestParam(required = false) LocalDate throughDate, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.followUps(principal, throughDate); }
}
