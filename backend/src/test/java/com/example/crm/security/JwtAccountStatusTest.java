package com.example.crm.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import com.example.crm.auth.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

class JwtAccountStatusTest {
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }

    @Test void issuedTokensStopAuthenticatingWhenAnAccountIsDisabled() throws Exception {
        var tokens = mock(JwtService.class); var users = mock(CrmUserDetailsService.class);
        when(tokens.subject("test-token")).thenReturn("test@example.test");
        var filter = new JwtAuthenticationFilter(tokens, users);
        for (boolean disabled : new boolean[]{false, true}) {
            SecurityContextHolder.clearContext();
            when(users.loadUserByUsername("test@example.test")).thenReturn(User.withUsername("test@example.test")
                .password("unused").roles("SALES_REP").disabled(disabled).build());
            var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer test-token");
            var chain = mock(FilterChain.class);
            filter.doFilter(request, new MockHttpServletResponse(), chain);
            assertThat(SecurityContextHolder.getContext().getAuthentication() != null).isEqualTo(!disabled);
            verify(chain).doFilter(eq(request), any());
        }
    }
}
