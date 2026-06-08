package com.example.crm.opportunities;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.contacts.Contact;
import com.example.crm.contacts.ContactRepository;
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
public class OpportunityService {
    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public OpportunityService(OpportunityRepository opportunityRepository, CompanyRepository companyRepository, ContactRepository contactRepository, UserRepository userRepository) {
        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<OpportunityResponse> search(CrmUserPrincipal principal, String search, OpportunityStage stage, UUID ownerId, UUID companyId, LocalDate closeDateFrom, LocalDate closeDateTo, Pageable pageable) {
        return opportunityRepository.searchVisible(principal.id(), canSeeAll(principal), normalize(search), stage, ownerId, companyId, closeDateFrom, closeDateTo, pageable).map(OpportunityResponse::from);
    }

    @Transactional(readOnly = true)
    public OpportunityResponse get(UUID id, CrmUserPrincipal principal) {
        Opportunity opportunity = findActive(id);
        assertReadable(opportunity, principal);
        return OpportunityResponse.from(opportunity);
    }

    @Transactional
    public OpportunityResponse create(OpportunityCreateRequest request, CrmUserPrincipal principal) {
        User owner = resolveInitialOwner(request.ownerUserId(), principal);
        Company company = findCompany(request.companyId());
        Opportunity opportunity = new Opportunity(company, cleanRequired(request.name()), owner);
        apply(opportunity, request.contactId(), request.amount(), request.currencyCode(), request.expectedCloseDate(), request.description());
        return OpportunityResponse.from(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponse update(UUID id, OpportunityUpdateRequest request, CrmUserPrincipal principal) {
        Opportunity opportunity = findActive(id);
        assertEditable(opportunity, principal);
        opportunity.setCompany(findCompany(request.companyId()));
        opportunity.setName(cleanRequired(request.name()));
        apply(opportunity, request.contactId(), request.amount(), request.currencyCode(), request.expectedCloseDate(), request.description());
        return OpportunityResponse.from(opportunity);
    }

    @Transactional
    public void delete(UUID id, CrmUserPrincipal principal) {
        requireManager(principal);
        Opportunity opportunity = findActive(id);
        opportunity.setDeletedAt(OffsetDateTime.now());
    }

    @Transactional
    public OpportunityResponse changeStage(UUID id, OpportunityStageRequest request, CrmUserPrincipal principal) {
        Opportunity opportunity = findActive(id);
        assertEditable(opportunity, principal);
        if (!isAllowedTransition(opportunity.getStage(), request.stage(), principal)) {
            throw new ConflictException("Opportunity stage transition is not allowed");
        }
        opportunity.setStage(request.stage());
        return OpportunityResponse.from(opportunity);
    }

    @Transactional
    public OpportunityResponse changeOwner(UUID id, OpportunityOwnerRequest request, CrmUserPrincipal principal) {
        requireManager(principal);
        Opportunity opportunity = findActive(id);
        opportunity.setOwner(findEligibleOwner(request.ownerUserId()));
        return OpportunityResponse.from(opportunity);
    }

    @Transactional(readOnly = true)
    public List<OpportunityPipelineSummaryResponse> pipelineSummary(CrmUserPrincipal principal) {
        return opportunityRepository.pipelineSummary(principal.id(), canSeeAll(principal)).stream()
            .map(OpportunityPipelineSummaryResponse::from)
            .toList();
    }

    private void apply(Opportunity opportunity, UUID contactId, java.math.BigDecimal amount, String currencyCode, LocalDate expectedCloseDate, String description) {
        opportunity.setAmount(amount);
        opportunity.setCurrencyCode(StringUtils.hasText(currencyCode) ? currencyCode.trim().toUpperCase() : "USD");
        opportunity.setExpectedCloseDate(expectedCloseDate);
        opportunity.setDescription(normalize(description));
        opportunity.setContact(contactId == null ? null : findContactForCompany(contactId, opportunity.getCompany()));
    }

    private User resolveInitialOwner(UUID requestedOwnerId, CrmUserPrincipal principal) {
        if (principal.role() == Role.SALES_REP) {
            return currentUser(principal);
        }
        return requestedOwnerId == null ? currentUser(principal) : findEligibleOwner(requestedOwnerId);
    }

    private User findEligibleOwner(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        if (!user.isActive()) {
            throw new ConflictException("Opportunity owner must be active");
        }
        return user;
    }

    private Company findCompany(UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Company not found");
        }
        return company;
    }

    private Contact findContactForCompany(UUID id, Company company) {
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        if (contact.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Contact not found");
        }
        if (!contact.getCompany().getId().equals(company.getId())) {
            throw new ConflictException("Contact must belong to the selected company");
        }
        return contact;
    }

    private Opportunity findActive(UUID id) {
        Opportunity opportunity = opportunityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opportunity.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Opportunity not found");
        }
        return opportunity;
    }

    private User currentUser(CrmUserPrincipal principal) {
        return userRepository.findById(principal.id()).orElseThrow(() -> new AccessDeniedException("Current user is not available"));
    }

    private void assertReadable(Opportunity opportunity, CrmUserPrincipal principal) {
        if (!canSeeAll(principal) && !isOwner(opportunity, principal)) {
            throw new AccessDeniedException("Opportunity is not visible to the current user");
        }
    }

    private void assertEditable(Opportunity opportunity, CrmUserPrincipal principal) {
        if (!canSeeAll(principal) && !isOwner(opportunity, principal)) {
            throw new AccessDeniedException("Opportunity cannot be edited by the current user");
        }
    }

    private boolean canSeeAll(CrmUserPrincipal principal) {
        return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER;
    }

    private boolean isOwner(Opportunity opportunity, CrmUserPrincipal principal) {
        return opportunity.getOwner() != null && opportunity.getOwner().getId().equals(principal.id());
    }

    private void requireManager(CrmUserPrincipal principal) {
        if (principal.role() != Role.ADMIN && principal.role() != Role.MANAGER) {
            throw new AccessDeniedException("Only admin and manager users can perform this action");
        }
    }

    private boolean isAllowedTransition(OpportunityStage current, OpportunityStage next, CrmUserPrincipal principal) {
        if (current == next) {
            return true;
        }
        if ((current == OpportunityStage.CLOSED_WON || current == OpportunityStage.CLOSED_LOST) && !canSeeAll(principal)) {
            return false;
        }
        return switch (current) {
            case PROSPECTING -> next == OpportunityStage.QUALIFICATION || next == OpportunityStage.CLOSED_LOST;
            case QUALIFICATION -> next == OpportunityStage.PROPOSAL || next == OpportunityStage.CLOSED_LOST;
            case PROPOSAL -> next == OpportunityStage.NEGOTIATION || next == OpportunityStage.CLOSED_LOST;
            case NEGOTIATION -> next == OpportunityStage.CLOSED_WON || next == OpportunityStage.CLOSED_LOST;
            case CLOSED_WON, CLOSED_LOST -> canSeeAll(principal) && next == OpportunityStage.PROSPECTING;
        };
    }

    private String cleanRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
