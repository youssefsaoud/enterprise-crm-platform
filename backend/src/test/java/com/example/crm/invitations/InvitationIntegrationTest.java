package com.example.crm.invitations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.crm.auth.JwtService;
import com.example.crm.invitations.InvitationDtos.Accept;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.*;
import java.net.URI;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"crm.demo.seed.enabled=false", "crm.invitations.delivery-mode=preview"})
class InvitationIntegrationTest {
    @Autowired TestRestTemplate http;
    @Autowired InvitationRepository invitations;
    @Autowired UserRepository users;
    @Autowired InvitationTransactions transactions;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Autowired JdbcTemplate jdbc;
    @MockBean InvitationMail mail;
    @MockBean InvitationLimits limits;
    private String token;
    private String adminBearer;
    private final String marker = "onboarding-test-" + UUID.randomUUID();
    private final String password = "Test-Only-Password42!";

    @BeforeEach void setup() {
        assertThat(jdbc.queryForObject("select current_database()", String.class)).isEqualTo("crm_dev");
        User admin = users.findByRoleAndActiveTrueOrderByFullNameAsc(Role.ADMIN).get(0);
        adminBearer = "Bearer " + jwt.createToken(new CrmUserPrincipal(admin));
        when(mail.send(any(), anyString(), anyString(), anyString(), anyString(), any())).thenAnswer(call -> {
            String url = call.getArgument(4);
            token = URI.create(url).getFragment().substring("token=".length());
            return "SENT";
        });
    }

    @AfterEach void cleanup() {
        // Delete only records owned by this uniquely identified test case.
        jdbc.update("delete from employee_invitations where normalized_email like ?", marker + "%");
        jdbc.update("delete from app_users where normalized_email like ?", marker + "%");
    }

    @Test void adminInvitesAndEmployeeAcceptsThenSignsInAndCannotReuseToken() {
        var created = invite("employee");
        assertThat(created.getStatusCode().value()).isEqualTo(201);
        assertThat(created.getHeaders().getCacheControl()).isEqualTo("no-store");
        EmployeeInvitation stored = ownInvitation();
        assertThat(stored.tokenHash.length()).isEqualTo(64);
        assertThat(stored.tokenHash.equals(token)).isFalse();
        assertThat(Duration.between(stored.lastSentAt, stored.expiresAt)).isEqualTo(Duration.ofHours(24));
        assertThat(created.getBody().toString().contains(token)).isFalse();
        assertThat(publicPost("validate", Map.of("token", token)).getStatusCode().value()).isEqualTo(200);
        assertThat(publicPost("accept", Map.of("token", token, "password", password, "role", "ADMIN", "email", "other@example.test")).getStatusCode().value()).isEqualTo(200);
        User employee = users.findByNormalizedEmail(email("employee")).orElseThrow();
        assertThat(employee.getRole()).isEqualTo(Role.SALES_REP);
        assertThat(employee.isActive()).isTrue();
        assertThat(encoder.matches(password, employee.getPasswordHash())).isTrue();
        assertThat(employee.getPasswordHash().equals(password)).isFalse();
        assertThat(http.postForEntity("/api/v1/auth/login", Map.of("email", employee.getEmail(), "password", password), Map.class).getStatusCode().value()).isEqualTo(200);
        assertThat(publicPost("accept", Map.of("token", token, "password", password)).getStatusCode().value()).isEqualTo(400);
    }

    @Test void managersAssociatesAndAnonymousCannotAdministerInvitations() {
        for (Role role : List.of(Role.MANAGER, Role.SALES_REP)) {
            User user = users.saveAndFlush(new User("Permission test", email(role.name()), encoder.encode(password), role));
            String bearer = "Bearer " + jwt.createToken(new CrmUserPrincipal(user));
            for (String path : List.of("/employees", "/invitations"))
                assertThat(request(HttpMethod.GET, "/api/v1/admin" + path, null, bearer).getStatusCode().value()).isEqualTo(403);
            for (String path : List.of("", "/" + UUID.randomUUID() + "/resend", "/" + UUID.randomUUID() + "/revoke"))
                assertThat(request(HttpMethod.POST, "/api/v1/admin/invitations" + path, payload("denied"), bearer).getStatusCode().value()).isEqualTo(403);
        }
        assertThat(request(HttpMethod.POST, "/api/v1/admin/invitations", payload("anonymous"), null).getStatusCode().value()).isEqualTo(403);
        assertThat(invitations.findAll().stream().noneMatch(i -> i.normalizedEmail.startsWith(marker))).isTrue();
    }

    @Test void expiredInvitationsAreRejected() {
        invite("expired"); EmployeeInvitation i = ownInvitation();
        jdbc.update("update employee_invitations set created_at=?, expires_at=? where id=?",
            java.sql.Timestamp.from(Instant.now().minus(Duration.ofDays(2))), java.sql.Timestamp.from(Instant.now().minusSeconds(1)), i.id);
        assertThat(publicPost("validate", Map.of("token", token)).getStatusCode().value()).isEqualTo(410);
        assertThat(publicPost("accept", Map.of("token", token, "password", password)).getStatusCode().value()).isEqualTo(410);
    }

    @Test void revokedInvitationsAreRejected() {
        invite("revoked");
        assertThat(adminPost("/" + ownInvitation().id + "/revoke", Map.of()).getStatusCode().value()).isEqualTo(200);
        assertThat(publicPost("accept", Map.of("token", token, "password", password)).getStatusCode().value()).isEqualTo(400);
    }

    @Test void invalidTokensAndWeakPasswordsAreRejectedWithoutCreatingUsers() {
        assertThat(publicPost("validate", Map.of("token", InvitationTokens.generate())).getStatusCode().value()).isEqualTo(400);
        invite("weak");
        assertThat(publicPost("accept", Map.of("token", token, "password", "weak")).getStatusCode().value()).isEqualTo(400);
        assertThat(users.findByNormalizedEmail(email("weak"))).isEmpty();
    }

    @Test void duplicatesAndExistingAccountsAreNotOverwritten() {
        invite("duplicate");
        assertThat(invite("duplicate").getStatusCode().value()).isEqualTo(409);
        users.saveAndFlush(new User("Existing", email("existing"), encoder.encode(password), Role.MANAGER));
        assertThat(invite("existing").getStatusCode().value()).isEqualTo(409);
        assertThat(users.findByNormalizedEmail(email("existing")).orElseThrow().getRole()).isEqualTo(Role.MANAGER);
    }

    @Test void adminRoleInvitationsAreRejected() {
        Map<String, Object> body = new HashMap<>(payload("admin")); body.put("role", "ADMIN");
        assertThat(adminPost("", body).getStatusCode().value()).isEqualTo(400);
    }

    @Test void managerInvitationKeepsItsStoredRole() {
        Map<String, Object> body = new HashMap<>(payload("manager")); body.put("role", "MANAGER");
        assertThat(adminPost("", body).getStatusCode().value()).isEqualTo(201);
        assertThat(publicPost("accept", Map.of("token", token, "password", password)).getStatusCode().value()).isEqualTo(200);
        assertThat(users.findByNormalizedEmail(email("manager")).orElseThrow().getRole()).isEqualTo(Role.MANAGER);
    }

    @Test void publicRateLimitReturnsSafeErrorAndNoStoreHeaders() {
        doThrow(new InvitationException(429, "Too many requests. Please try again later."))
            .when(limits).check(anyString(), anyInt(), any(Duration.class));
        var result = publicPost("validate", Map.of("token", InvitationTokens.generate()));
        assertThat(result.getStatusCode().value()).isEqualTo(429);
        assertThat(result.getHeaders().getCacheControl()).isEqualTo("no-store");
    }

    @Test void failedDeliveryCanBeRetriedWithoutDuplicateInvitations() {
        doThrow(new IllegalStateException("simulated failure")).when(mail).send(any(), anyString(), anyString(), anyString(), anyString(), any());
        assertThat(invite("retry").getStatusCode().value()).isEqualTo(502);
        UUID id = ownInvitation().id;
        jdbc.update("update employee_invitations set last_sent_at=? where id=?", java.sql.Timestamp.from(Instant.now().minusSeconds(61)), id);
        doReturn("SENT").when(mail).send(any(), anyString(), anyString(), anyString(), anyString(), any());
        assertThat(adminPost("/" + id + "/resend", Map.of()).getStatusCode().value()).isEqualTo(200);
        assertThat(ownInvitation().deliveryStatus).isEqualTo("SENT");
        assertThat(jdbc.queryForObject("select count(*) from employee_invitations where normalized_email=?", Integer.class, email("retry"))).isEqualTo(1);
    }

    @Test void resendRotatesTokenAndEnforcesCooldown() {
        invite("resend"); String oldToken = token; UUID id = ownInvitation().id;
        assertThat(adminPost("/" + id + "/resend", Map.of()).getStatusCode().value()).isEqualTo(429);
        jdbc.update("update employee_invitations set last_sent_at=? where id=?", java.sql.Timestamp.from(Instant.now().minusSeconds(61)), id);
        assertThat(adminPost("/" + id + "/resend", Map.of()).getStatusCode().value()).isEqualTo(200);
        assertThat(token.equals(oldToken)).isFalse();
        assertThat(publicPost("validate", Map.of("token", oldToken)).getStatusCode().value()).isEqualTo(400);
        assertThat(publicPost("validate", Map.of("token", token)).getStatusCode().value()).isEqualTo(200);
    }

    @Test void deliveryFailureIsPersistedAndReportedAsFailure() {
        doThrow(new IllegalStateException("simulated failure")).when(mail).send(any(), anyString(), anyString(), anyString(), anyString(), any());
        assertThat(invite("failure").getStatusCode().value()).isEqualTo(502);
        assertThat(ownInvitation().deliveryStatus).isEqualTo("FAILED");
        assertThat(invite("failure").getStatusCode().value()).isEqualTo(409);
    }

    @Test void concurrentAcceptanceCreatesExactlyOneUser() throws Exception {
        invite("concurrent"); String invitationToken = token;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Boolean> attempt = () -> {
            start.await();
            try { transactions.accept(new Accept(invitationToken, password)); return true; }
            catch (InvitationException rejected) { return false; }
        };
        try {
            Future<Boolean> first = executor.submit(attempt), second = executor.submit(attempt); start.countDown();
            int accepted = (first.get(10, TimeUnit.SECONDS) ? 1 : 0) + (second.get(10, TimeUnit.SECONDS) ? 1 : 0);
            assertThat(accepted).isEqualTo(1);
            assertThat(jdbc.queryForObject("select count(*) from app_users where normalized_email=?", Integer.class, email("concurrent"))).isEqualTo(1);
        } finally { executor.shutdownNow(); }
    }

    private String email(String suffix) { return marker + "-" + suffix + "@example.test"; }
    private Map<String, Object> payload(String suffix) { return Map.of("fullName", "Invitation verification", "email", email(suffix), "role", "SALES_REP"); }
    private ResponseEntity<Map> invite(String suffix) { return adminPost("", payload(suffix)); }
    private EmployeeInvitation ownInvitation() { return invitations.findAll().stream().filter(i -> i.normalizedEmail.startsWith(marker)).findFirst().orElseThrow(); }
    private ResponseEntity<Map> adminPost(String path, Object body) { return request(HttpMethod.POST, "/api/v1/admin/invitations" + path, body, adminBearer); }
    private ResponseEntity<Map> publicPost(String path, Object body) { return request(HttpMethod.POST, "/api/v1/invitations/" + path, body, null); }
    private ResponseEntity<Map> request(HttpMethod method, String path, Object body, String bearer) {
        HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearer != null) headers.set(HttpHeaders.AUTHORIZATION, bearer);
        return http.exchange(path, method, new HttpEntity<>(body, headers), Map.class);
    }
}
