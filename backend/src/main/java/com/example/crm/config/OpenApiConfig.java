package com.example.crm.config;

import com.example.crm.common.web.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class OpenApiConfig {
    @Bean
    OpenAPI orqestraApi() {
        Components components = new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .description("Use the accessToken returned by POST /api/v1/auth/login. Enter the token without the Bearer prefix."));
        return new OpenAPI().info(new Info().title("Orqestra API")
            .description("Project & Vendor Management Platform REST API").version("v1"))
            .components(components).addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    OpenApiCustomizer documentOperations() {
        return api -> {
            ModelConverters.getInstance().read(ApiError.class).forEach(api.getComponents()::addSchemas);
            // Preview HTML contains activation links and is intentionally not part of the API catalog.
            api.getPaths().keySet().removeIf(path -> path.endsWith("/preview"));
            api.getPaths().forEach((path, item) -> item.readOperationsMap().forEach((method, operation) -> {
                String group = group(path);
                operation.setTags(List.of(group));
                if (operation.getSummary() == null) {
                    String action = operation.getOperationId().replaceFirst("_\\d+$", "")
                        .replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase(java.util.Locale.ROOT);
                    operation.setSummary(Character.toUpperCase(action.charAt(0)) + action.substring(1) + " - " + group);
                }
                if (operation.getDescription() == null) operation.setDescription(switch (group) {
                    case "Employee Invitations" -> path.startsWith("/api/v1/admin/")
                        ? "Admin-only employee onboarding. Email submission does not guarantee inbox delivery."
                        : "Public activation operation. A valid, unexpired invitation token is required; request limits apply.";
                    case "Projects", "Vendors", "Quotations", "Documents", "Analytics", "Activities" ->
                        "Requires an active authenticated account. Existing role and record-ownership rules are enforced server-side; Associates are scoped to assigned projects where applicable.";
                    default -> "Existing server-side role and ownership checks apply. See the request and response schemas for field constraints.";
                });
                boolean publicOperation = path.equals("/api/v1/health") || path.equals("/api/v1/auth/login")
                    || path.equals("/api/v1/invitations/validate") || path.equals("/api/v1/invitations/accept");
                operation.setSecurity(publicOperation ? List.of() : List.of(new SecurityRequirement().addList("bearerAuth")));
                if (!publicOperation) {
                    operation.getResponses().addApiResponse("403", new ApiResponse()
                        .description("Missing or invalid JWT, insufficient role, or record outside the caller's authorized scope."));
                }
                if (!path.equals("/api/v1/health")) operation.getResponses().addApiResponse("400", error("Invalid input or failed validation."));
                if (method.name().equals("DELETE")) {
                    operation.getResponses().remove("200");
                    operation.getResponses().addApiResponse("204", new ApiResponse().description("Record removed; no response body."));
                }
                if (method.name().equals("POST") && (List.of("/api/v1/projects", "/api/v1/companies", "/api/v1/contacts",
                    "/api/v1/leads", "/api/v1/opportunities", "/api/v1/interactions").contains(path)
                    || path.matches("/api/v1/projects/[^/]+/vendors") || path.matches("/api/v1/project-vendors/[^/]+/quotations"))) {
                    ApiResponse created = operation.getResponses().remove("200");
                    if (created != null) operation.getResponses().addApiResponse("201", created.description("Record created. Location header identifies the new record."));
                }
                if (path.contains("{id}") || path.contains("{projectId}") || path.contains("{projectVendorId}")) {
                    operation.getResponses().addApiResponse("404", error("Requested record not found."));
                }
                if (method.name().matches("POST|PUT|PATCH") && !publicOperation) {
                    operation.getResponses().addApiResponse("409", error("Conflicting state or duplicate record."));
                }
                if (group.equals("Employee Invitations")) {
                    operation.getResponses().addApiResponse("429", error("Request limit or resend cooldown exceeded."));
                    if (publicOperation) operation.getResponses().addApiResponse("410", error("Invitation has expired."));
                    if (path.endsWith("/invitations") && method.name().equals("POST")) {
                        operation.getResponses().remove("200");
                        operation.getResponses().addApiResponse("201", new ApiResponse().description("Invitation created; inspect submitted and invitation.deliveryStatus.")
                            .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("#/components/schemas/Delivery")))));
                    }
                    if (method.name().equals("POST") && !publicOperation && !path.endsWith("/revoke")) {
                        operation.getResponses().addApiResponse("502", new ApiResponse().description("Email submission failed; invitation delivery state is retained for retry.")
                            .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("#/components/schemas/Delivery")))));
                    }
                }
                if (path.equals("/api/v1/auth/login")) {
                    operation.getResponses().addApiResponse("403", new ApiResponse().description("Invalid credentials or inactive account."));
                }
            }));
        };
    }

    private static ApiResponse error(String description) {
        return new ApiResponse().description(description).content(new Content().addMediaType("application/json",
            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError"))));
    }

    private static String group(String path) {
        if (path.contains("/invitations") || path.endsWith("/employees")) return "Employee Invitations";
        if (path.contains("/auth/") || path.contains("/rbac/")) return "Authentication";
        if (path.contains("/documents")) return "Documents";
        if (path.contains("/interactions")) return "Activities";
        if (path.contains("/quotations")) return "Quotations";
        if (path.contains("/dashboard") || path.contains("/project-dashboard")) return "Analytics";
        if (path.contains("/vendors") || path.contains("/project-vendors")) return "Vendors";
        if (path.contains("/companies")) return "Companies";
        if (path.contains("/projects")) return "Projects";
        if (path.contains("/contacts")) return "Contacts";
        if (path.contains("/leads")) return "Leads";
        if (path.contains("/opportunities")) return "Opportunities";
        if (path.contains("/users")) return "Employees";
        return "Health";
    }
}
