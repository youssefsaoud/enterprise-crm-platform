package com.example.crm.invitations;

import com.example.crm.invitations.InvitationDtos.*;
import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class InvitationController {
    private final InvitationService service;
    public InvitationController(InvitationService service) { this.service = service; }
    @GetMapping("/employees") public List<EmployeeView> employees() { return service.employees(); }
    @GetMapping("/invitations") public List<InvitationView> invitations() { return service.list(); }
    @PostMapping("/invitations")
    public ResponseEntity<Delivery> create(@Valid @RequestBody Create request, @AuthenticationPrincipal CrmUserPrincipal admin) {
        return delivery(service.create(request, admin), true);
    }
    @PostMapping("/invitations/{id}/resend")
    public ResponseEntity<Delivery> resend(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal admin) {
        return delivery(service.resend(id, admin), false);
    }
    @PostMapping("/invitations/{id}/revoke") public InvitationView revoke(@PathVariable UUID id) { return service.revoke(id); }
    private ResponseEntity<Delivery> delivery(Delivery result, boolean created) {
        int status = "FAILED".equals(result.invitation().deliveryStatus()) ? 502 : created ? 201 : 200;
        return ResponseEntity.status(status).body(result);
    }
}
