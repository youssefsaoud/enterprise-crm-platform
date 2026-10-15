package com.example.crm.invitations;

import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("dev")
@ConditionalOnProperty(prefix = "crm.invitations", name = "preview-enabled", havingValue = "true")
@PreAuthorize("hasRole('ADMIN')")
class InvitationPreviewController {
    private final InvitationPreview preview;
    InvitationPreviewController(InvitationPreview preview) { this.preview = preview; }
    @GetMapping(value = "/api/v1/admin/invitations/{id}/preview", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> preview(@PathVariable UUID id) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").header("Referrer-Policy", "no-referrer")
            .header("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; base-uri 'none'; frame-ancestors 'none'")
            .body(preview.get(id));
    }
}
