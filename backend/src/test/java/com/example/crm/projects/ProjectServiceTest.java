package com.example.crm.projects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.companies.CompanyRole;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.UserRepository;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock ProjectRepository projectRepository;
    @Mock CompanyRepository companyRepository;
    @Mock UserRepository userRepository;
    @Mock CrmUserPrincipal principal;

    @Test
    void associateSearchIsScopedToAssignedProjects() {
        UUID associateId = UUID.randomUUID();
        when(principal.id()).thenReturn(associateId);
        when(principal.role()).thenReturn(com.example.crm.users.Role.SALES_REP);
        when(projectRepository.searchVisible(eq(associateId), eq(false), eq("camera"), eq(ProjectStatus.IN_PROGRESS), eq(null), eq(associateId), any()))
            .thenReturn(new PageImpl<>(java.util.List.of()));

        new ProjectService(projectRepository, companyRepository, userRepository)
            .search(principal, " camera ", ProjectStatus.IN_PROGRESS, null, null, PageRequest.of(0, 10));

        verify(projectRepository).searchVisible(eq(associateId), eq(false), eq("camera"), eq(ProjectStatus.IN_PROGRESS), eq(null), eq(associateId), any());
    }

    @Test
    void createRejectsACompanyThatIsNotMarkedAsClient() {
        UUID companyId = UUID.randomUUID();
        Company vendor = new Company("Vendor only", null);
        vendor.setRoles(EnumSet.of(CompanyRole.VENDOR));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(vendor));

        ProjectCreateRequest request = new ProjectCreateRequest("Warehouse refresh", "PRJ-001", companyId, null, null, null, null, null, "USD", null, null, null, null);

        assertThatThrownBy(() -> new ProjectService(projectRepository, companyRepository, userRepository).create(request))
            .isInstanceOf(com.example.crm.common.web.ConflictException.class)
            .hasMessageContaining("not marked as a client");
    }
}