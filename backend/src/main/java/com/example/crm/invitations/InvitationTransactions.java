package com.example.crm.invitations;

import com.example.crm.invitations.InvitationDtos.*;
import com.example.crm.users.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationTransactions {
    private final InvitationRepository invitations;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final long ttlHours;

    public InvitationTransactions(InvitationRepository invitations, UserRepository users, PasswordEncoder encoder,
                                  @Value("${crm.invitations.ttl-hours:24}") long ttlHours) {
        if (ttlHours < 1 || ttlHours > 168) throw new IllegalArgumentException("Invitation TTL must be 1 to 168 hours");
        this.invitations = invitations; this.users = users; this.encoder = encoder; this.ttlHours = ttlHours;
    }

    @Transactional
    public EmployeeInvitation create(Create request, UUID adminId, String hash) {
        if (request.role() != Role.MANAGER && request.role() != Role.SALES_REP)
            throw new InvitationException(400, "Only Manager or Associate invitations are permitted.");
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        ensureNoAccount(email);
        if (invitations.existsByNormalizedEmailAndStatus(email, "PENDING"))
            throw new InvitationException(409, "A pending invitation already exists. Resend or revoke it first.");
        EmployeeInvitation i = new EmployeeInvitation();
        i.id = UUID.randomUUID(); i.fullName = request.fullName().trim(); i.email = request.email().trim();
        i.normalizedEmail = email; i.role = request.role(); i.invitedBy = adminId;
        i.createdAt = Instant.now(); i.status = "PENDING"; i.deliveryAttempts = 0;
        prepare(i, hash);
        return invitations.saveAndFlush(i);
    }

    @Transactional
    public EmployeeInvitation resend(UUID id, String hash) {
        EmployeeInvitation i = locked(id);
        if (!"PENDING".equals(i.status)) throw new InvitationException(409, "Only pending or expired invitations can be resent.");
        ensureNoAccount(i.normalizedEmail);
        if (i.lastSentAt.plusSeconds(60).isAfter(Instant.now()))
            throw new InvitationException(429, "Wait at least one minute before resending this invitation.");
        prepare(i, hash);
        return i;
    }

    private void prepare(EmployeeInvitation i, String hash) {
        i.tokenHash = hash;
        i.lastSentAt = Instant.now(); i.expiresAt = i.lastSentAt.plus(Duration.ofHours(ttlHours));
        i.deliveryAttempts++; i.deliveryStatus = "SENDING";
    }

    @Transactional
    public InvitationView delivery(UUID id, String hash, String result) {
        EmployeeInvitation i = locked(id);
        // A slow delivery must never overwrite a newer resend's result.
        if (hash.equals(i.tokenHash)) i.deliveryStatus = result;
        return InvitationView.from(i);
    }

    @Transactional
    public InvitationView revoke(UUID id) {
        EmployeeInvitation i = locked(id);
        if (!"PENDING".equals(i.status)) throw new InvitationException(409, "This invitation is no longer pending.");
        i.status = "REVOKED"; i.revokedAt = Instant.now();
        return InvitationView.from(i);
    }

    @Transactional(readOnly = true)
    public List<InvitationView> list() {
        return invitations.findAllByOrderByCreatedAtDesc().stream().map(InvitationView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeView> employees() {
        return users.findAll().stream().sorted(Comparator.comparing(User::getFullName))
            .map(u -> new EmployeeView(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isActive(), u.getCreatedAt().toInstant())).toList();
    }

    @Transactional
    public Welcome validate(String token) {
        EmployeeInvitation i = usable(token);
        return new Welcome(i.fullName, i.role, i.expiresAt);
    }

    @Transactional
    public void accept(Accept request) {
        EmployeeInvitation i = usable(request.token());
        ensureNoAccount(i.normalizedEmail);
        // BCrypt uses at most 72 bytes; reject longer UTF-8 input rather than truncating it.
        if (request.password() == null || request.password().length() < 12 || request.password().length() > 64
            || request.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72
            || !request.password().matches("(?s)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*"))
            throw new InvitationException(400, "Use 12-64 characters with uppercase, lowercase, and a number (maximum 72 UTF-8 bytes).");
        users.saveAndFlush(new User(i.fullName, i.email, encoder.encode(request.password()), i.role));
        i.status = "ACCEPTED"; i.acceptedAt = Instant.now();
    }

    private EmployeeInvitation usable(String token) {
        EmployeeInvitation i = invitations.lockByHash(InvitationTokens.hash(token)).orElseThrow(InvitationException::invalid);
        if (!"PENDING".equals(i.status)) throw InvitationException.invalid();
        if (!i.expiresAt.isAfter(Instant.now()))
            throw new InvitationException(410, "This invitation has expired. Contact your Admin for a new invitation.");
        return i;
    }

    private EmployeeInvitation locked(UUID id) {
        return invitations.lockById(id).orElseThrow(() -> new InvitationException(404, "Invitation not found."));
    }

    private void ensureNoAccount(String email) {
        // Never take over or reset an existing account, including an inactive one.
        if (users.findByNormalizedEmail(email).isPresent())
            throw new InvitationException(409, "An account already exists for this email. Contact your Admin.");
    }
}
