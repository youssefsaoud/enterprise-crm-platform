package com.example.crm.leads;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.contacts.Contact;
import com.example.crm.contacts.ContactRepository;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.opportunities.OpportunityRepository;
import com.example.crm.opportunities.OpportunityStage;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LeadConversionService {
    private final LeadRepository leadRepository;
    private final LeadConversionRepository leadConversionRepository;
    private final LeadEventRepository leadEventRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public LeadConversionService(
        LeadRepository leadRepository,
        LeadConversionRepository leadConversionRepository,
        LeadEventRepository leadEventRepository,
        CompanyRepository companyRepository,
        ContactRepository contactRepository,
        OpportunityRepository opportunityRepository,
        UserRepository userRepository
    ) {
        this.leadRepository = leadRepository;
        this.leadConversionRepository = leadConversionRepository;
        this.leadEventRepository = leadEventRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LeadConversionResponse convert(UUID leadId, LeadConversionRequest request, CrmUserPrincipal principal) {
        Lead lead = findLead(leadId);
        assertConvertible(lead, principal);
        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new ConflictException("Only qualified leads can be converted");
        }
        if (leadConversionRepository.existsByLeadId(leadId)) {
            throw new ConflictException("Lead has already been converted");
        }
        User actor = currentUser(principal);
        Company company = resolveCompany(request, lead, actor);
        Contact contact = resolveContact(request, company, lead);
        Opportunity opportunity = request.opportunity() == null ? null : createOpportunity(request.opportunity(), company, contact, lead, ownerFor(lead, actor));
        lead.setCompany(company);
        lead.setStatus(LeadStatus.CONVERTED);
        LeadConversion conversion = new LeadConversion(lead, company, contact, opportunity, actor, OffsetDateTime.now());
        conversion.setNotes(normalize(request.notes()));
        LeadConversion saved = leadConversionRepository.save(conversion);
        leadEventRepository.save(new LeadEvent(lead, LeadEventType.STATUS_CHANGED, LeadStatus.QUALIFIED.name(), LeadStatus.CONVERTED.name(), actor));
        return LeadConversionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public LeadConversionResponse get(UUID leadId, CrmUserPrincipal principal) {
        Lead lead = findLead(leadId);
        assertReadable(lead, principal);
        LeadConversion conversion = leadConversionRepository.findByLeadId(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead conversion not found"));
        return LeadConversionResponse.from(conversion);
    }

    private Company resolveCompany(LeadConversionRequest request, Lead lead, User owner) {
        if (request.existingCompanyId() != null && request.newCompany() != null) {
            throw new ConflictException("Choose an existing company or create a new company, not both");
        }
        if (request.existingCompanyId() != null) {
            return findCompany(request.existingCompanyId());
        }
        LeadConversionRequest.NewCompany newCompany = request.newCompany();
        String name = newCompany == null ? lead.getCompanyName() : newCompany.name();
        if (!StringUtils.hasText(name)) {
            throw new ConflictException("A company is required for conversion");
        }
        Company company = new Company(name.trim(), owner);
        if (newCompany != null) {
            company.setIndustry(normalize(newCompany.industry()));
            company.setWebsite(normalize(newCompany.website()));
            company.setPhone(normalize(newCompany.phone()));
            company.setEmail(normalize(newCompany.email()));
            company.setAddress(normalize(newCompany.address()));
        }
        return companyRepository.save(company);
    }

    private Contact resolveContact(LeadConversionRequest request, Company company, Lead lead) {
        if (request.existingContactId() != null && request.newContact() != null) {
            throw new ConflictException("Choose an existing contact or create a new contact, not both");
        }
        if (request.existingContactId() != null) {
            Contact contact = findContact(request.existingContactId());
            if (!contact.getCompany().getId().equals(company.getId())) {
                throw new ConflictException("Selected contact must belong to the conversion company");
            }
            return contact;
        }
        LeadConversionRequest.NewContact newContact = request.newContact();
        Contact contact = new Contact(
            company,
            cleanRequired(newContact == null ? lead.getFirstName() : newContact.firstName()),
            cleanRequired(newContact == null ? lead.getLastName() : newContact.lastName())
        );
        contact.setEmail(normalize(newContact == null ? lead.getEmail() : newContact.email()));
        contact.setPhone(normalize(newContact == null ? lead.getPhone() : newContact.phone()));
        if (newContact != null) {
            contact.setJobTitle(normalize(newContact.jobTitle()));
        }
        return contactRepository.save(contact);
    }

    private Opportunity createOpportunity(LeadConversionRequest.NewOpportunity request, Company company, Contact contact, Lead lead, User owner) {
        Opportunity opportunity = new Opportunity(company, cleanRequired(request.name()), owner);
        opportunity.setContact(contact);
        opportunity.setLead(lead);
        opportunity.setAmount(request.amount() == null ? BigDecimal.ZERO : request.amount());
        opportunity.setCurrencyCode(StringUtils.hasText(request.currencyCode()) ? request.currencyCode().trim().toUpperCase() : "USD");
        opportunity.setExpectedCloseDate(request.expectedCloseDate());
        opportunity.setStage(request.stage() == null ? OpportunityStage.PROSPECTING : request.stage());
        opportunity.setDescription(normalize(request.description()));
        return opportunityRepository.save(opportunity);
    }

    private User ownerFor(Lead lead, User actor) {
        return lead.getAssignedTo() == null ? actor : lead.getAssignedTo();
    }

    private Lead findLead(UUID id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        if (lead.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Lead not found");
        }
        return lead;
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

    private void assertConvertible(Lead lead, CrmUserPrincipal principal) {
        if (canSeeAll(principal) || isLeadOwner(lead, principal)) {
            return;
        }
        throw new AccessDeniedException("Lead cannot be converted by the current user");
    }

    private void assertReadable(Lead lead, CrmUserPrincipal principal) {
        if (canSeeAll(principal) || isLeadOwner(lead, principal)) {
            return;
        }
        throw new AccessDeniedException("Lead conversion is not visible to the current user");
    }

    private boolean isLeadOwner(Lead lead, CrmUserPrincipal principal) {
        return (lead.getAssignedTo() != null && lead.getAssignedTo().getId().equals(principal.id()))
            || (lead.getCreatedBy() != null && lead.getCreatedBy().getId().equals(principal.id()));
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
