package com.example.crm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class CrmApplicationTests {

    @Test
    void applicationClassIsBootstrappable() {
        assertThat(CrmApplication.class).hasAnnotation(SpringBootApplication.class);
    }
}