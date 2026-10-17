package com.example.crm.projects;

import com.example.crm.companies.*;
import com.example.crm.common.web.*;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.*;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    public ProjectService(ProjectRepository projectRepository, CompanyRepository companyRepository, UserRepository userRepository) { this.projectRepository = projectRepository; this.companyRepository = companyRepository; this.userRepository = userRepository; }
    @Transactional(readOnly = true) @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ProjectStatusCounts statusCounts(CrmUserPrincipal principal, String search, UUID clientId, UUID associateId) {
        boolean all = canSeeAll(principal);
        Map<ProjectStatus, Long> counts = new EnumMap<>(ProjectStatus.class);
        projectRepository.statusCounts(principal.id(), all, normalize(search), clientId, all ? associateId : principal.id())
            .forEach(row -> counts.put(row.getStatus(), row.getCount()));
        List<ProjectStatusSummary> statuses = Arrays.stream(ProjectStatus.values()).map(status -> {
            String label = status.name().replace('_', ' ').toLowerCase(Locale.ROOT);
            return new ProjectStatusSummary(status, Character.toUpperCase(label.charAt(0)) + label.substring(1), counts.getOrDefault(status, 0L));
        }).toList();
        return new ProjectStatusCounts(statuses.stream().mapToLong(ProjectStatusSummary::count).sum(), statuses);
    }
    @Transactional(readOnly = true) @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public Page<ProjectResponse> search(CrmUserPrincipal principal, String search, ProjectStatus status, UUID clientId, UUID assignedAssociateId, Pageable pageable) { boolean all = canSeeAll(principal); UUID visibleAssociate = all ? assignedAssociateId : principal.id(); return projectRepository.searchVisible(principal.id(), all, normalize(search), status, clientId, visibleAssociate, pageable).map(ProjectResponse::from); }
    @Transactional(readOnly = true) @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ProjectResponse get(UUID id, CrmUserPrincipal principal) { return ProjectResponse.from(ensureVisible(findActive(id), principal)); }
    @Transactional @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ProjectResponse create(ProjectCreateRequest request) { Company client = findClient(request.clientId()); Project project = new Project(cleanRequired(request.name()), cleanRequired(request.reference()), client); apply(project, request.description(), request.category(), request.location(), request.deadline(), request.estimatedBudget(), request.currencyCode(), request.assignedAssociateId(), request.status(), request.scopeOfWork(), request.notes()); return ProjectResponse.from(projectRepository.save(project)); }
    @Transactional @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ProjectResponse update(UUID id, ProjectUpdateRequest request, CrmUserPrincipal principal) { Project project = ensureEditable(findActive(id), principal); project.setName(cleanRequired(request.name())); project.setReference(cleanRequired(request.reference())); project.setClient(findClient(request.clientId())); apply(project, request.description(), request.category(), request.location(), request.deadline(), request.estimatedBudget(), request.currencyCode(), request.assignedAssociateId(), request.status(), request.scopeOfWork(), request.notes()); return ProjectResponse.from(project); }
    @Transactional @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(UUID id) { findActive(id).setDeletedAt(OffsetDateTime.now()); }
    private Project findActive(UUID id) { Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found")); if (project.getDeletedAt() != null) throw new ResourceNotFoundException("Project not found"); return project; }
    private Project ensureVisible(Project project, CrmUserPrincipal principal) { if (!canSeeAll(principal) && (project.getAssignedAssociate() == null || !project.getAssignedAssociate().getId().equals(principal.id()))) throw new ResourceNotFoundException("Project not found"); return project; }
    private Project ensureEditable(Project project, CrmUserPrincipal principal) { if (!canSeeAll(principal) && (project.getAssignedAssociate() == null || !project.getAssignedAssociate().getId().equals(principal.id()))) throw new AccessDeniedException("Only the assigned associate can update this project"); return project; }
    private boolean canSeeAll(CrmUserPrincipal principal) { return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER; }
    private Company findClient(UUID id) { Company company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client company not found")); if (company.getDeletedAt() != null || !company.getRoles().contains(CompanyRole.CLIENT)) throw new ConflictException("Selected company is not marked as a client"); return company; }
    private User findAssociate(UUID id) { if (id == null) return null; User associate = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Associate not found")); if (!associate.isActive() || associate.getRole() != Role.SALES_REP) throw new ConflictException("Assigned user must be an active associate"); return associate; }
    private void apply(Project project, String description, String category, String location, java.time.LocalDate deadline, java.math.BigDecimal budget, String currency, UUID associateId, ProjectStatus status, String scope, String notes) { project.setDescription(normalize(description)); project.setCategory(normalize(category)); project.setLocation(normalize(location)); project.setDeadline(deadline); project.setEstimatedBudget(budget); project.setCurrencyCode(StringUtils.hasText(currency) ? currency.trim().toUpperCase() : "USD"); project.setAssignedAssociate(findAssociate(associateId)); project.setStatus(status == null ? ProjectStatus.PLANNED : status); project.setScopeOfWork(normalize(scope)); project.setNotes(normalize(notes)); }
    private String cleanRequired(String value) { return value == null ? null : value.trim(); }
    private String normalize(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
