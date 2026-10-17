package com.example.crm.projects;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }
    @GetMapping Page<ProjectResponse> search(@AuthenticationPrincipal CrmUserPrincipal principal, @RequestParam(required = false) String search, @RequestParam(required = false) ProjectStatus status, @RequestParam(required = false) UUID clientId, @RequestParam(required = false) UUID assignedAssociateId, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return projectService.search(principal, search, status, clientId, assignedAssociateId, pageable); }
    @GetMapping("/{id}") ProjectResponse get(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) { return projectService.get(id, principal); }
    @PostMapping ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) { ProjectResponse response = projectService.create(request); return ResponseEntity.created(URI.create("/api/v1/projects/" + response.id())).body(response); }
    @PutMapping("/{id}") ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody ProjectUpdateRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) { return projectService.update(id, request, principal); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { projectService.delete(id); return ResponseEntity.noContent().build(); }
}
