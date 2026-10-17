package com.example.crm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {"crm.demo.seed.enabled=false", "crm.bootstrap.admin.enabled=false"})
@AutoConfigureMockMvc
class OpenApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void developmentDocsDescribeAuthenticationSchemasAndAllWorkspaceGroups() throws Exception {
        String json = mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        JsonNode api = mapper.readTree(json);
        assertThat(api.at("/info/title").asText()).isEqualTo("Orqestra API");
        assertThat(api.at("/info/version").asText()).isEqualTo("v1");
        assertThat(api.at("/components/securitySchemes/bearerAuth/scheme").asText()).isEqualTo("bearer");
        JsonNode paths = api.path("paths");
        for (String path : Set.of("/api/v1/auth/login", "/api/v1/health", "/api/v1/invitations/validate", "/api/v1/invitations/accept")) {
            paths.get(path).elements().forEachRemaining(op -> assertThat(op.path("security").isEmpty()).isTrue());
        }
        assertThat(paths.get("/api/v1/auth/me").path("get").path("security").get(0).has("bearerAuth")).isTrue();
        assertThat(paths.get("/api/v1/projects").path("post").path("responses").has("201")).isTrue();
        assertThat(paths.get("/api/v1/projects/{id}").path("delete").path("responses").has("204")).isTrue();
        assertThat(paths.get("/api/v1/projects").path("get").path("responses").has("403")).isTrue();
        Set<String> tags = new HashSet<>();
        paths.elements().forEachRemaining(item -> item.elements().forEachRemaining(op -> op.path("tags").forEach(tag -> tags.add(tag.asText()))));
        assertThat(tags).contains("Authentication", "Projects", "Companies", "Vendors", "Quotations", "Documents", "Analytics", "Employee Invitations");
        JsonNode schemas = api.path("components").path("schemas");
        assertThat(schemas.has("ApiError")).isTrue();
        Set<String> parameters = new HashSet<>();
        paths.get("/api/v1/projects").path("get").path("parameters").forEach(p -> parameters.add(p.path("name").asText()));
        assertThat(parameters).contains("page", "size", "sort").doesNotContain("pageable");
        assertThat(schemas.path("ProjectCreateRequest").path("properties").path("name").path("maxLength").asInt()).isEqualTo(180);
        assertThat(schemas.path("LoginRequest").path("properties").path("password").path("writeOnly").asBoolean()).isTrue();
        assertThat(schemas.path("InvitationAcceptRequest").path("properties").path("password").path("minLength").asInt()).isEqualTo(12);
        assertThat(json).doesNotContain("passwordHash", "tokenHash", "CrmUserPrincipal", "/preview", "SMTP_PASSWORD");
        assertReferencesResolve(api, api);
    }

    private static void assertReferencesResolve(JsonNode root, JsonNode node) {
        if (node.isObject() && node.has("$ref")) {
            String ref = node.get("$ref").asText();
            assertThat(ref).startsWith("#/");
            assertThat(root.at(ref.substring(1)).isMissingNode()).as("Reference %s resolves", ref).isFalse();
        }
        if (node.isContainerNode()) node.elements().forEachRemaining(child -> assertReferencesResolve(root, child));
    }

    @Test void uiIsPublicButApplicationSecurityIsUnchanged() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs/swagger-config")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/projects")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/employees")).andExpect(status().isForbidden());
    }
}
