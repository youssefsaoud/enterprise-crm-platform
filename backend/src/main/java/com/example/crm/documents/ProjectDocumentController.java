package com.example.crm.documents;

import com.example.crm.security.CrmUserPrincipal;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class ProjectDocumentController {
    private final ProjectDocumentService service;
    public ProjectDocumentController(ProjectDocumentService service) { this.service = service; }

    @GetMapping("/projects/{projectId}/documents")
    List<ProjectDocumentResponse> list(@PathVariable UUID projectId, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.list(projectId, principal); }

    @PostMapping(value = "/projects/{projectId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ProjectDocumentResponse upload(@PathVariable UUID projectId, @RequestParam(required = false) UUID quotationId, @RequestPart("file") MultipartFile file, @AuthenticationPrincipal CrmUserPrincipal principal) { return service.upload(projectId, quotationId, file, principal); }

    @GetMapping("/documents/{id}/download")
    ResponseEntity<Resource> download(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        ProjectDocumentService.StoredDocument document = service.download(id, principal);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(document.contentType())).header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(document.filename()).build().toString()).body(document.resource());
    }
}
