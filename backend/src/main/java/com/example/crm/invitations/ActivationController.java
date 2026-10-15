package com.example.crm.invitations;

import com.example.crm.invitations.InvitationDtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
public class ActivationController {
    private final InvitationService service;
    private final InvitationLimits limits;
    public ActivationController(InvitationService service, InvitationLimits limits) { this.service = service; this.limits = limits; }
    @PostMapping("/validate") public Welcome validate(@Valid @RequestBody Token request, HttpServletRequest http) {
        limit(http); return service.validate(request.token());
    }
    @PostMapping("/accept") public void accept(@Valid @RequestBody Accept request, HttpServletRequest http) {
        limit(http); service.accept(request);
    }
    private void limit(HttpServletRequest http) {
        // Do not trust client-supplied forwarding headers for the abuse-limit key.
        limits.check("activation:" + http.getRemoteAddr(), 20, Duration.ofMinutes(1));
    }
}
