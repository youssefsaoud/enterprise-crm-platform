package com.example.crm.projectvendors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.companies.CompanyRole;
import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.projects.Project;
import com.example.crm.projects.ProjectRepository;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

class ProjectVendorServiceTest {
    private ProjectVendorRepository projectVendorRepository;
    private ProjectRepository projectRepository;
    private CompanyRepository companyRepository;
    private UserRepository userRepository;
    private ProjectVendorService service;
    private CrmUserPrincipal associate;

    @BeforeEach
    void setUp() {
        projectVendorRepository = mock(ProjectVendorRepository.class);
        projectRepository = mock(ProjectRepository.class);
        companyRepository = mock(CompanyRepository.class);
        userRepository = mock(UserRepository.class);
        service = new ProjectVendorService(projectVendorRepository, projectRepository, companyRepository, userRepository);
        User user = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(user.getId()).thenReturn(userId);
        when(user.getRole()).thenReturn(Role.SALES_REP);
        when(user.getFullName()).thenReturn("Associate");
        when(user.isActive()).thenReturn(true);
        associate = new CrmUserPrincipal(user);
    }

    @Test
    void associateCannotReadAnUnassignedProject() {
        UUID projectId = UUID.randomUUID();
        Project project = mock(Project.class);
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());
        when(project.getDeletedAt()).thenReturn(null);
        when(project.getAssignedAssociate()).thenReturn(owner);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.search(projectId, null, null, PageRequest.of(0, 10), associate))
            .isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(projectVendorRepository);
    }

    @Test
    void clientCompanyCannotBeAssignedAsVendor() {
        Project project = assignedProject(associate.id());
        UUID projectId = UUID.randomUUID();
        when(project.getId()).thenReturn(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        Company company = mock(Company.class);
        when(company.getDeletedAt()).thenReturn(null);
        when(company.getRoles()).thenReturn(Set.of(CompanyRole.CLIENT));
        UUID vendorId = UUID.randomUUID();
        when(companyRepository.findById(vendorId)).thenReturn(Optional.of(company));

        assertThatThrownBy(() -> service.add(projectId, new ProjectVendorCreateRequest(vendorId, null, null, null, null, null, null), associate))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("vendor");
    }

    @Test
    void duplicateActiveAssignmentIsRejected() {
        Project project = assignedProject(associate.id());
        UUID projectId = UUID.randomUUID();
        when(project.getId()).thenReturn(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        Company company = mock(Company.class);
        UUID vendorId = UUID.randomUUID();
        when(company.getDeletedAt()).thenReturn(null);
        when(company.getRoles()).thenReturn(Set.of(CompanyRole.VENDOR));
        when(companyRepository.findById(vendorId)).thenReturn(Optional.of(company));
        when(projectVendorRepository.existsByProjectIdAndVendorIdAndDeletedAtIsNull(projectId, vendorId)).thenReturn(true);

        assertThatThrownBy(() -> service.add(projectId, new ProjectVendorCreateRequest(vendorId, null, null, null, null, null, null), associate))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("already assigned");
    }

    private Project assignedProject(UUID userId) {
        Project project = mock(Project.class);
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(userId);
        when(project.getDeletedAt()).thenReturn(null);
        when(project.getAssignedAssociate()).thenReturn(owner);
        return project;
    }
}
