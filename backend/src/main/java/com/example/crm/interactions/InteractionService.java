package com.example.crm.interactions;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.contacts.Contact;
import com.example.crm.contacts.ContactRepository;
import com.example.crm.leads.Lead;
import com.example.crm.leads.LeadRepository;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.opportunities.OpportunityRepository;
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
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public InteractionService(
        InteractionRepository interactionRepository,
        CompanyRepository companyRepository,
        ContactRepository contactRepository,
        LeadRepository leadRepository,
        OpportunityRepository opportunityRepository,
        UserRepository userRepository
    ) {
        this.interactionRepository = interactionRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InteractionResponse create(InteractionCreateRequest request, CrmUserPrincipal principal) {
        User author = currentUser(principal);
        Interaction interaction = new Interaction(author, request.type(), cleanRequired(request.subject()), request.occurredAt());
        applyLinks(interaction, request.companyId(), request.contactId(), request.leadId(), request.opportunityId());
        applyFields(interaction, request.type(), request.subject(), request.description(), request.occurredAt(), request.followUpAt());
        assertReadable(interaction, principal);
        return InteractionResponse.from(interactionRepository.save(interaction));
    }

    @Transactional(readOnly = true)
    public InteractionResponse get(UUID id, CrmUserPrincipal principal) {
        Interaction interaction = findActive(id);
        assertReadable(interaction, principal);
        return InteractionResponse.from(interaction);
    }

    @Transactional(readOnly = true)
    public Page<InteractionResponse> search(
        CrmUserPrincipal principal,
        InteractionType type,
        UUID creatorId,
        UUID companyId,
        UUID contactId,
        UUID leadId,
        UUID opportunityId,
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        Pageable pageable
    ) {
        return interactionRepository.searchVisible(principal.id(), canSeeAll(principal), type, creatorId, companyId, contactId, leadId, opportunityId, fromDate, toDate, pageable)
            .map(InteractionResponse::from);
    }

    @Transactional
    public InteractionResponse update(UUID id, InteractionUpdateRequest request, CrmUserPrincipal principal) {
        Interaction interaction = findActive(id);
        assertEditable(interaction, principal);
        applyFields(interaction, request.type(), request.subject(), request.description(), request.occurredAt(), request.followUpAt());
        return InteractionResponse.from(interaction);
    }

    @Transactional
    public void delete(UUID id, CrmUserPrincipal principal) {
        Interaction interaction = findActive(id);
        assertEditable(interaction, principal);
        interaction.setDeletedAt(OffsetDateTime.now());
    }

    private void applyFields(Interaction interaction, InteractionType type, String subject, String description, OffsetDateTime occurredAt, OffsetDateTime followUpAt) {
        interaction.setType(type);
        interaction.setSubject(cleanRequired(subject));
        interaction.setDescription(normalize(description));
        interaction.setOccurredAt(occurredAt);
        interaction.setFollowUpAt(followUpAt);
    }

    private void applyLinks(Interaction interaction, UUID companyId, UUID contactId, UUID leadId, UUID opportunityId) {
        Company company = companyId == null ? null : findCompany(companyId);
        Contact contact = contactId == null ? null : findContact(contactId);
        Lead lead = leadId == null ? null : findLead(leadId);
        Opportunity opportunity = opportunityId == null ? null : findOpportunity(opportunityId);
        if (company == null && contact == null && lead == null && opportunity == null) {
            throw new ConflictException("Interaction must be linked to at least one CRM record");
        }
        Company expectedCompany = company;
        if (contact != null) {
            expectedCompany = requireSameCompany(expectedCompany, contact.getCompany());
        }
        if (lead != null && lead.getCompany() != null) {
            expectedCompany = requireSameCompany(expectedCompany, lead.getCompany());
        }
        if (opportunity != null) {
            expectedCompany = requireSameCompany(expectedCompany, opportunity.getCompany());
            if (opportunity.getContact() != null && contact != null && !opportunity.getContact().getId().equals(contact.getId())) {
                throw new ConflictException("Interaction contact must match the opportunity contact");
            }
        }
        interaction.setCompany(expectedCompany);
        interaction.setContact(contact);
        interaction.setLead(lead);
        interaction.setOpportunity(opportunity);
    }

    private Company requireSameCompany(Company current, Company candidate) {
        if (candidate == null) {
            return current;
        }
        if (current != null && !current.getId().equals(candidate.getId())) {
            throw new ConflictException("Linked CRM records must belong to the same company");
        }
        return candidate;
    }

    private Company findCompany(UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Company not found");
        }
        return company;
    }

    private Contact findContact(UUID id) {
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        if (contact.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Contact not found");
        }
        return contact;
    }

    private Lead findLead(UUID id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        if (lead.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Lead not found");
        }
        return lead;
    }

    private Opportunity findOpportunity(UUID id) {
        Opportunity opportunity = opportunityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opportunity.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Opportunity not found");
        }
        return opportunity;
    }

    private Interaction findActive(UUID id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Interaction not found"));
        if (interaction.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Interaction not found");
        }
        return interaction;
    }

    private void assertReadable(Interaction interaction, CrmUserPrincipal principal) {
        if (canSeeAll(principal) || ownsInteractionScope(interaction, principal)) {
            return;
        }
        throw new AccessDeniedException("Interaction is not visible to the current user");
    }

    private void assertEditable(Interaction interaction, CrmUserPrincipal principal) {
        if (canSeeAll(principal) || interaction.getAuthor().getId().equals(principal.id())) {
            return;
        }
        throw new AccessDeniedException("Interaction cannot be changed by the current user");
    }

    private boolean ownsInteractionScope(Interaction interaction, CrmUserPrincipal principal) {
        return interaction.getAuthor().getId().equals(principal.id())
            || (interaction.getCompany() != null && interaction.getCompany().getOwner() != null && interaction.getCompany().getOwner().getId().equals(principal.id()))
            || (interaction.getLead() != null && interaction.getLead().getAssignedTo() != null && interaction.getLead().getAssignedTo().getId().equals(principal.id()))
            || (interaction.getLead() != null && interaction.getLead().getCreatedBy() != null && interaction.getLead().getCreatedBy().getId().equals(principal.id()))
            || (interaction.getOpportunity() != null && interaction.getOpportunity().getOwner() != null && interaction.getOpportunity().getOwner().getId().equals(principal.id()));
    }

    private boolean canSeeAll(CrmUserPrincipal principal) {
        return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER;
    }

    private User currentUser(CrmUserPrincipal principal) {
        return userRepository.findById(principal.id()).orElseThrow(() -> new AccessDeniedException("Current user is not available"));
    }

    private String cleanRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
