package com.example.crm.invitations;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Component;

/** Ephemeral, bounded local mailbox; never written to logs, files, or the database. */
@Component
public class InvitationPreview {
    private record Entry(String html, Instant expiresAt) {}
    private final Map<UUID, Entry> entries = new LinkedHashMap<>();
    synchronized void put(UUID id, String html, Instant expiry) {
        entries.entrySet().removeIf(e -> !e.getValue().expiresAt.isAfter(Instant.now()));
        if (entries.size() >= 100) entries.remove(entries.keySet().iterator().next());
        entries.put(id, new Entry(html, expiry));
    }
    synchronized String get(UUID id) {
        Entry e = entries.get(id);
        if (e == null || !e.expiresAt.isAfter(Instant.now())) throw new InvitationException(404, "Preview not available.");
        return e.html;
    }
}
