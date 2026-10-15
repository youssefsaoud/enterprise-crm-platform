package com.example.crm.invitations;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Component;

/** Per-process limits; deployments with multiple replicas should enforce shared limits at the gateway too. */
@Component
public class InvitationLimits {
    private record Window(Instant start, int count) {}
    private final Map<String, Window> windows = new HashMap<>();

    public synchronized void check(String key, int maximum, Duration period) {
        Instant now = Instant.now();
        windows.entrySet().removeIf(e -> e.getValue().start.plus(Duration.ofHours(1)).isBefore(now));
        Window w = windows.get(key);
        if (w == null || !w.start.plus(period).isAfter(now)) w = new Window(now, 0);
        if (w.count >= maximum || (!windows.containsKey(key) && windows.size() >= 10000))
            throw new InvitationException(429, "Too many requests. Please try again later.");
        windows.put(key, new Window(w.start, w.count + 1));
    }
}
