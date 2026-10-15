package com.example.crm.invitations;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class InvitationResponseFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/invitations") || path.startsWith("/api/v1/admin/invitations")
            || path.startsWith("/api/v1/admin/employees")) {
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Referrer-Policy", "no-referrer");
        }
        chain.doFilter(request, response);
    }
}
