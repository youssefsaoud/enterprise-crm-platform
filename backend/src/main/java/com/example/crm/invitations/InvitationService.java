package com.example.crm.invitations;

import com.example.crm.invitations.InvitationDtos.*;
import com.example.crm.security.CrmUserPrincipal;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {
    private final InvitationTransactions transactions;
    private final InvitationMail mail;
    private final InvitationLimits limits;
    private final String frontendUrl;

    public InvitationService(InvitationTransactions transactions, InvitationMail mail, InvitationLimits limits,
        @Value("${crm.invitations.frontend-base-url:http://localhost:4200}") String frontendUrl) {
        URI uri = URI.create(frontendUrl);
        boolean local = "localhost".equals(uri.getHost()) || "127.0.0.1".equals(uri.getHost());
        if (uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null
            || !("https".equals(uri.getScheme()) || (local && "http".equals(uri.getScheme()))))
            throw new IllegalArgumentException("FRONTEND_BASE_URL must be HTTPS (HTTP allowed only on localhost)");
        this.transactions = transactions; this.mail = mail; this.limits = limits;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<EmployeeView> employees() { return transactions.employees(); }
    @PreAuthorize("hasRole('ADMIN')")
    public List<InvitationView> list() { return transactions.list(); }

    @PreAuthorize("hasRole('ADMIN')")
    public Delivery create(Create request, CrmUserPrincipal admin) {
        limitSend(admin);
        String token = InvitationTokens.generate();
        EmployeeInvitation i;
        try { i = transactions.create(request, admin.id(), InvitationTokens.hash(token)); }
        catch (DataIntegrityViolationException conflict) { throw new InvitationException(409, "An account or pending invitation already exists for this email."); }
        return deliver(i, token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Delivery resend(UUID id, CrmUserPrincipal admin) {
        limitSend(admin);
        String token = InvitationTokens.generate();
        return deliver(transactions.resend(id, InvitationTokens.hash(token)), token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public InvitationView revoke(UUID id) { return transactions.revoke(id); }

    public Welcome validate(String token) { return transactions.validate(token); }
    public void accept(Accept request) {
        try { transactions.accept(request); }
        catch (DataIntegrityViolationException conflict) { throw InvitationException.invalid(); }
    }

    private void limitSend(CrmUserPrincipal admin) {
        limits.check("send:" + admin.id(), 10, Duration.ofHours(1));
    }

    private Delivery deliver(EmployeeInvitation i, String token) {
        String result;
        try {
            result = mail.send(i.id, i.email, i.fullName, i.role == com.example.crm.users.Role.MANAGER ? "Manager" : "Associate",
                frontendUrl + "/activate-account#token=" + token, i.expiresAt);
        } catch (RuntimeException ignored) { result = "FAILED"; }
        InvitationView view = transactions.delivery(i.id, InvitationTokens.hash(token), result);
        return new Delivery(view, "SENT".equals(result), switch (result) {
            case "SENT" -> "Invitation submitted to the mail server. Inbox delivery is not guaranteed.";
            case "PREVIEW" -> "Invitation prepared in local preview mode. No email was sent.";
            default -> "Email could not be submitted. The invitation is saved; retry using Resend.";
        });
    }
}
