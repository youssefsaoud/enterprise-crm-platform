package com.example.crm.projects;

import static org.assertj.core.api.Assertions.*;
import com.example.crm.companies.*;
import com.example.crm.projectvendors.*;
import com.example.crm.quotations.*;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProjectContextIntegrationTest {
    @Autowired ProjectService service;
    @Autowired OrganizationRelationshipController relationships;
    @Autowired ProjectRepository projects;
    @Autowired CompanyRepository companies;
    @Autowired UserRepository users;
    @Autowired ProjectVendorRepository vendors;
    @Autowired QuotationRepository quotations;
    @Autowired jakarta.servlet.MultipartConfigElement multipart;
    @Autowired org.springframework.boot.autoconfigure.web.ServerProperties serverProperties;
    private String marker;
    private User associate;
    private User other;
    private User admin;
    private Company company;

    @BeforeEach void setup() {
        marker = "context-" + UUID.randomUUID();
        associate = users.save(new User(marker, marker + "@example.test", "not-a-login-password", Role.SALES_REP));
        other = users.save(new User(marker + " other", marker + "-other@example.test", "not-a-login-password", Role.SALES_REP));
        admin = users.save(new User(marker + " admin", marker + "-admin@example.test", "not-a-login-password", Role.ADMIN));
        company = new Company(marker, admin); company.setRoles(Set.of(CompanyRole.CLIENT, CompanyRole.VENDOR));
        company = companies.save(company);
        for (int i = 0; i < 5; i++) {
            Project project = new Project(marker + " " + i, "CTX-" + UUID.randomUUID().toString().substring(0, 20), company);
            project.setAssignedAssociate(i < 2 ? associate : i == 3 ? null : other);
            project.setStatus(i == 0 ? ProjectStatus.COMPLETED : ProjectStatus.IN_PROGRESS);
            if (i == 4) project.setDeletedAt(OffsetDateTime.now());
            project = projects.save(project);
            ProjectVendor vendor = new ProjectVendor(project, company);
            if (i == 3) vendor.setDeletedAt(OffsetDateTime.now());
            vendor = vendors.save(vendor);
            if (i == 0) {
                quotations.save(new Quotation(vendor, BigDecimal.TEN, "USD", LocalDate.now(), 1));
                quotations.save(new Quotation(vendor, BigDecimal.ONE, "USD", LocalDate.now(), 2));
            }
        }
        projects.flush();
    }
    @AfterEach void clearSecurity() { SecurityContextHolder.clearContext(); }
    private CrmUserPrincipal as(User user) {
        var principal = new CrmUserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        return principal;
    }

    @Test void countersRespectSearchOwnershipAndNullableFilters() {
        var all = service.statusCounts(as(admin), marker, null, null);
        assertThat(all.total()).isEqualTo(4);
        assertThat(all.statuses()).hasSize(ProjectStatus.values().length);
        assertThat(all.statuses().stream().mapToLong(ProjectStatusSummary::count).sum()).isEqualTo(4);
        var mine = service.statusCounts(as(associate), marker, null, other.getId());
        assertThat(mine.total()).isEqualTo(2);
        assertThat(mine.statuses()).filteredOn(row -> row.status() == ProjectStatus.COMPLETED).extracting(ProjectStatusSummary::count).containsExactly(1L);
        assertThat(service.statusCounts(as(admin), marker, UUID.randomUUID(), null).total()).isZero();
        assertThat(service.statusCounts(as(admin), marker, null, other.getId()).total()).isEqualTo(1);
        assertThat(service.statusCounts(as(associate), marker + " missing", null, null).total()).isZero();
    }

    @Test void multipartConfigurationMatchesTheTenMegabyteDocumentRule() {
        assertThat(multipart.getMaxFileSize()).isEqualTo(10L * 1024 * 1024);
        assertThat(multipart.getMaxRequestSize()).isEqualTo(11L * 1024 * 1024);
        assertThat(serverProperties.getTomcat().getMaxSwallowSize().toBytes()).isEqualTo(12L * 1024 * 1024);
    }

    @Test void relationshipsCountDistinctVisibleProjectsNotQuoteRevisionsOrDeletedRows() {
        var all = relationships.relationships(List.of(company.getId()), as(admin)).get(0);
        assertThat(all.clientProjects()).isEqualTo(4);
        assertThat(all.vendorProjects()).isEqualTo(3);
        var mine = relationships.relationships(List.of(company.getId()), as(associate)).get(0);
        assertThat(mine.clientProjects()).isEqualTo(2);
        assertThat(mine.vendorProjects()).isEqualTo(2);
        assertThat(relationships.relationships(List.of(UUID.randomUUID()), as(associate)).get(0).vendorProjects()).isZero();
        assertThatThrownBy(() -> relationships.relationships(Collections.nCopies(101, company.getId()), as(admin)))
            .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
    }
}
