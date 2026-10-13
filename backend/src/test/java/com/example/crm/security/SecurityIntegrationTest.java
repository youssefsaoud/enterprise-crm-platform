package com.example.crm.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    private static final String DEV_ORIGIN = "http://localhost:4200";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${crm.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${crm.bootstrap.admin.password}")
    private String adminPassword;

    @Test
    void authenticatedDashboardAndNullSearchEndpointsReturnOk() throws Exception {
        String bearer = bearerToken();

        mockMvc.perform(get("/api/v1/dashboard/summary").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/contacts").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/companies").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/companies").param("role", "CLIENT").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/projects").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/projects/e81a1a03-3ce2-4a23-ad81-29131fcbf5f2/vendors").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/opportunities").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
    }

    @Test
    void corsPreflightFromAngularDevOriginIsAllowed() throws Exception {
        mockMvc.perform(options("/api/v1/dashboard/summary")
                .header(HttpHeaders.ORIGIN, DEV_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, DEV_ORIGIN));
    }

    @Test
    void authenticatedMissingEndpointReturnsNotFoundInsteadOfForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/missing-route").header(HttpHeaders.AUTHORIZATION, bearerToken()))
            .andExpect(status().isNotFound());
    }

    private String bearerToken() throws Exception {
        String loginBody = objectMapper.writeValueAsString(new LoginPayload(adminEmail, adminPassword));
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return "Bearer " + json.get("accessToken").asText();
    }

    private record LoginPayload(String email, String password) {
    }
}