package com.example.crm.leads;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final LeadEventRepository leadEventRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public LeadService(
        LeadRepository leadRepository,
        LeadEventRepository leadEventRepository,
        CompanyRepository companyRepository,
        UserRepository userRepository
    ) {
        this.leadRepository = leadRepository;
        this.leadEventRepository = leadEventRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<LeadResponse> search(
        CrmUserPrincipal principal,
        LeadStatus status,
        LeadSource source,
        UUID assigneeId,
        UUID companyId,
        String keyword,
        Pageable pageable
    ) {
        return leadRepository.searchVisible(
            principal.id(),
            canSeeAll(principal),
            status,
            source,
            assigneeId,
            companyId,
            normalize(keyword),
            pageable
        ).map(LeadResponse::from);
    }

    @Transactional(readOnly = true)
    public LeadResponse get(UUID id, CrmUserPrincipal principal) {
        Lead lead = findActive(id);
        assertReadable(lead, principal);
        return LeadResponse.from(lead);
    }

    @Transactional
    public LeadResponse create(LeadCreateRequest request, CrmUserPrincipal principal) {
        User creator = currentUser(principal);
        Lead lead = new Lead(cleanRequired(request.firstName()), cleanRequired(request.lastName()), request.source(), creator);
        apply(lead, request.email(), request.phone(), request.companyName(), request.companyId());
        Lead saved = leadRepository.save(lead);
        record(saved, LeadEventType.CREATED, null, saved.getStatus().name(), creator);
        return LeadResponse.from(saved);
    }

    @Transactional
    public LeadResponse update(UUID id, LeadUpdateRequest request, CrmUserPrincipal principal) {
        Lead lead = findActive(id);
        assertEditable(lead, principal);
        lead.setFirstName(cleanRequired(request.firstName()));
        lead.setLastName(cleanRequired(request.lastName()));
        lead.setSource(request.source());
        apply(lead, request.email(), request.phone(), request.companyName(), request.companyId());
        record(lead, LeadEventType.UPDATED, null, lead.getSummary(), currentUser(principal));
        return LeadResponse.from(lead);
    }

    @Transactional
    public void delete(UUID id, CrmUserPrincipal principal) {
        requireRole(principal, Role.ADMIN);
        Lead lead = findActive(id);
        lead.setDeletedAt(OffsetDateTime.now());
        record(lead, LeadEventType.DELETED, null, "deleted", currentUser(principal));
    }

    @Transactional
    public LeadResponse assign(UUID id, LeadAssignmentRequest request, CrmUserPrincipal principal) {
        requireManager(principal);
        Lead lead = findActive(id);
        User actor = currentUser(principal);
        User previous = lead.getAssignedTo();
        if (request.assigneeUserId() == null) {
            lead.setAssignedTo(null);
            lead.setAssignedAt(null);
            record(lead, LeadEventType.UNASSIGNED, userLabel(previous), null, actor);
            return LeadResponse.from(lead);
        }
        User assignee = userRepository.findById(request.assigneeUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        if (!assignee.isActive() || assignee.getRole() != Role.SALES_REP) {
            throw new ConflictException("Lead can only be assigned to an active sales representative");
        }
        lead.setAssignedTo(assignee);
        lead.setAssignedAt(OffsetDateTime.now());
        record(lead, LeadEventType.ASSIGNED, userLabel(previous), userLabel(assignee), actor);
        return LeadResponse.from(lead);
    }

    @Transactional
    public LeadResponse changeStatus(UUID id, LeadStatusRequest request, CrmUserPrincipal principal) {
        Lead lead = findActive(id);
        assertStatusChangeAllowed(lead, principal);
        LeadStatus previous = lead.getStatus();
        LeadStatus next = request.status();
        if (!isAllowedTransition(previous, next)) {
            throw new ConflictException("Lead status transition is not allowed");
        }
        lead.setStatus(next);
        record(lead, LeadEventType.STATUS_CHANGED, previous.name(), next.name(), currentUser(principal));
        return LeadResponse.from(lead);
    }

    @Transactional(readOnly = true)
    public Page<LeadEventResponse> history(UUID id, CrmUserPrincipal principal, Pageable pageable) {
        Lead lead = findActive(id);
        assertReadable(lead, principal);
        return leadEventRepository.findByLeadOrderByCreatedAtDesc(lead, pageable).map(LeadEventResponse::from);
    }

    private void apply(Lead lead, String email, String phone, String companyName, UUID companyId) {
        lead.setEmail(normalize(email));
        lead.setPhone(normalize(phone));
        lead.setCompanyName(normalize(companyName));
        lead.setCompany(companyId == null ? null : findCompany(companyId));
    }

    private Company findCompany(UUID id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Company not found");
        }
        return company;
    }

    private Lead findActive(UUID id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        if (lead.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Lead not found");
        }
        return lead;
    }

    private User currentUser(CrmUserPrincipal principal) {
        return userRepository.findById(principal.id())
            .orElseThrow(() -> new AccessDeniedException("Current user is not available"));
    }

    private void assertReadable(Lead lead, CrmUserPrincipal principal) {
        if (!canSeeAll(principal) && !isOwner(lead, principal)) {
            throw new AccessDeniedException("Lead is not visible to the current user");
        }
