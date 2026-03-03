package com.example.crm.contacts;

import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    public ContactService(ContactRepository contactRepository, CompanyRepository companyRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public Page<ContactResponse> search(String search, UUID companyId, Pageable pageable) {
        return contactRepository.search(normalize(search), companyId, pageable).map(ContactResponse::from);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ContactResponse get(UUID id) {
        return ContactResponse.from(findActive(id));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public Page<ContactResponse> forCompany(UUID companyId, Pageable pageable) {
        Company company = findCompany(companyId);
        return contactRepository.findByCompanyAndDeletedAtIsNull(company, pageable).map(ContactResponse::from);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ContactResponse create(ContactCreateRequest request) {
        Company company = findCompany(request.companyId());
        Contact contact = new Contact(company, cleanRequired(request.firstName()), cleanRequired(request.lastName()));
        apply(contact, request.email(), request.phone(), request.jobTitle());
        return ContactResponse.from(contactRepository.save(contact));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public ContactResponse update(UUID id, ContactUpdateRequest request) {
        Contact contact = findActive(id);
        contact.setCompany(findCompany(request.companyId()));
        contact.setFirstName(cleanRequired(request.firstName()));
        contact.setLastName(cleanRequired(request.lastName()));
        apply(contact, request.email(), request.phone(), request.jobTitle());
        return ContactResponse.from(contact);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(UUID id) {
        Contact contact = findActive(id);
        contactRepository.delete(contact);
    }

    private Contact findActive(UUID id) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        if (contact.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Contact not found");
        }
        return contact;
    }

    private Company findCompany(UUID id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Company not found");
        }
        return company;
    }

    private void apply(Contact contact, String email, String phone, String jobTitle) {
        contact.setEmail(normalize(email));
        contact.setPhone(normalize(phone));
        contact.setJobTitle(normalize(jobTitle));
    }

    private String cleanRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
