package com.example.crm.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {"spring.profiles.active=prod", "spring.config.import=optional:file:../.env.local[.properties]",
    "crm.demo.seed.enabled=false", "crm.bootstrap.admin.enabled=false"})
@AutoConfigureMockMvc
class ProductionOpenApiSecurityTest {
    @Autowired MockMvc mvc;

    @Test void productionDoesNotExposeDocsOrWeakenApiSecurity() throws Exception {
        for (String path : new String[]{"/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config", "/swagger-ui.html", "/swagger-ui/index.html", "/api/v1/projects"}) {
            mvc.perform(get(path)).andExpect(status().isForbidden());
        }
    }
}
