package com.example.crm.projectvendors;

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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProjectVendorService {
    private final ProjectVendorRepository repository;
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public ProjectVendorService(ProjectVendorRepository repository, ProjectRepository projectRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProjectVendorResponse> search(UUID projectId, OutreachStatus status, String search, Pageable pageable, CrmUserPrincipal principal) {
        Project project = visibleProject(projectId, principal);
        return repository.searchByProject(project.getId(), status, normalize(search), pageable).map(ProjectVendorResponse::from);
    }

    @Transactional
    public ProjectVendorResponse add(UUID projectId, ProjectVendorCreateRequest request, CrmUserPrincipal principal) {
        assertCanManageAssignments(principal);
        Project project = editableProject(projectId, principal);
        Company vendor = companyRepository.findById(request.vendorId()).orElseThrow(() -> new ResourceNotFoundException("Vendor company not found"));
        if (vendor.getDeletedAt() != null || !vendor.getRoles().contains(CompanyRole.VENDOR)) throw new ConflictException("Selected company is not marked as a vendor");
        if (repository.existsByProjectIdAndVendorIdAndDeletedAtIsNull(projectId, request.vendorId())) throw new ConflictException("Vendor is already assigned to this project");
        ProjectVendor assignment = new ProjectVendor(project, vendor);
        apply(assignment, request.responsibleAssociateId(), request.status(), request.firstContactDate(), request.lastContactDate(), request.nextFollowUpDate(), request.notes(), principal);
        return ProjectVendorResponse.from(repository.save(assignment));
    }

    @Transactional
    public ProjectVendorResponse update(UUID id, ProjectVendorUpdateRequest request, CrmUserPrincipal principal) {
        ProjectVendor assignment = findActive(id);
        editableProject(assignment.getProject().getId(), principal);
        apply(assignment, request.responsibleAssociateId(), request.status(), request.firstContactDate(), request.lastContactDate(), request.nextFollowUpDate(), request.notes(), principal);
        return ProjectVendorResponse.from(assignment);
    }

    @Transactional
    public void remove(UUID id, CrmUserPrincipal principal) {
        assertCanManageAssignments(principal);
        ProjectVendor assignment = findActive(id);
        editableProject(assignment.getProject().getId(), principal);
        if (!canManageAll(principal) && assignment.getResponsibleAssociate() != null && !assignment.getResponsibleAssociate().getId().equals(principal.id())) {
            throw new AccessDeniedException("Only the responsible associate can remove this assignment");
        }
        assignment.setDeletedAt(OffsetDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<ProjectVendorResponse> followUps(CrmUserPrincipal principal, LocalDate throughDate) {
        return repository.findFollowUps(principal.id(), canManageAll(principal), throughDate == null ? LocalDate.now().plusDays(14) : throughDate)
            .stream().map(ProjectVendorResponse::from).toList();
    }

    private void apply(ProjectVendor assignment, UUID responsibleId, OutreachStatus status, LocalDate firstContact, LocalDate lastContact, LocalDate nextFollowUp, String notes, CrmUserPrincipal principal) {
        if (lastContact != null && firstContact != null && lastContact.isBefore(firstContact)) throw new ConflictException("Last contact cannot be before first contact");
        if (nextFollowUp != null && lastContact != null && nextFollowUp.isBefore(lastContact)) throw new ConflictException("Next follow-up cannot be before last contact");
        if (canManageAll(principal)) {
            assignment.setResponsibleAssociate(findAssociate(responsibleId));
        } else if (responsibleId != null && (assignment.getResponsibleAssociate() == null || !responsibleId.equals(assignment.getResponsibleAssociate().getId()))) {
            throw new AccessDeniedException("Associates cannot reassign vendor work");
        }
        assignment.setStatus(status == null ? (assignment.getStatus() == null ? OutreachStatus.NOT_CONTACTED : assignment.getStatus()) : status);
        assignment.setFirstContactDate(firstContact);
        assignment.setLastContactDate(lastContact);
        assignment.setNextFollowUpDate(nextFollowUp);
        assignment.setNotes(normalize(notes));
    }

    private Project visibleProject(UUID id, CrmUserPrincipal principal) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (project.getDeletedAt() != null || (!canManageAll(principal) && (project.getAssignedAssociate() == null || !project.getAssignedAssociate().getId().equals(principal.id())))) throw new ResourceNotFoundException("Project not found");
        return project;
    }

    private Project editableProject(UUID id, CrmUserPrincipal principal) {
        return visibleProject(id, principal);
    }

    private ProjectVendor findActive(UUID id) { return repository.findActive(id).orElseThrow(() -> new ResourceNotFoundException("Project vendor assignment not found")); }
    private User findAssociate(UUID id) { if (id == null) return null; User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Associate not found")); if (!user.isActive() || user.getRole() != Role.SALES_REP) throw new ConflictException("Responsible user must be an active associate"); return user; }
    private void assertCanManageAssignments(CrmUserPrincipal principal) {
        if (!canManageAll(principal)) throw new AccessDeniedException("Only administrators and managers can add or remove vendor assignments");
    }

    private boolean canManageAll(CrmUserPrincipal principal) { return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER; }
    private String normalize(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
