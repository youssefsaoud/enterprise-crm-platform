package com.example.crm.demo;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class RecruiterDemoGuard {
    public static boolean enabled(Environment env) {
        return env.getProperty("crm.recruiter-demo.enabled", Boolean.class, false)
            && env.acceptsProfiles(Profiles.of("dev & docker-demo & !prod"));
    }

    static void validate(Environment env) {
        boolean requested = env.getProperty("crm.recruiter-demo.enabled", Boolean.class, false);
        boolean profile = env.acceptsProfiles(Profiles.of("docker-demo"));
        if (env.acceptsProfiles(Profiles.of("prod")) && (requested || profile
            || env.getProperty("crm.demo.seed.enabled", Boolean.class, false))) {
            throw new IllegalStateException("Demo seeding cannot run in production.");
        }
        if (!requested && !profile) return;
        if (!enabled(env)
            || !"jdbc:postgresql://postgres:5432/orqestra_demo".equals(env.getProperty("spring.datasource.url"))
            || !"orqestra_demo".equals(env.getProperty("spring.datasource.username"))
            || env.getProperty("crm.bootstrap.admin.enabled", Boolean.class, false)
            || env.getProperty("crm.demo.seed.enabled", Boolean.class, false)
            || !env.getProperty("spring.mail.username", "").isBlank()
            || !env.getProperty("spring.mail.password", "").isBlank()
            || !"127.0.0.1".equals(env.getProperty("spring.mail.host"))) {
            throw new IllegalStateException("Recruiter demo requires explicit demo profiles, its isolated Docker datasource, no bootstrap, and no SMTP credentials.");
        }
    }

    // Validate profiles and datasource settings before Flyway or any datasource bean is created.
    @Bean static BeanFactoryPostProcessor rejectUnsafeDemoConfiguration(Environment environment) {
        return beanFactory -> validate(environment);
    }
}
