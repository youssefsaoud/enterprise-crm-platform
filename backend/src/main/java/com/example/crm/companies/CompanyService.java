package com.example.crm.companies;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.contacts.ContactRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;

    public CompanyService(CompanyRepository companyRepository, ContactRepository contactRepository) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public Page<CompanyResponse> search(String search, String industry, Pageable pageable) {
        return companyRepository.search(normalize(search), normalize(industry), pageable).map(CompanyResponse::from);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')")
    public CompanyResponse get(UUID id) {
        return CompanyResponse.from(findActive(id));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public CompanyResponse create(CompanyCreateRequest request) {
        Company company = new Company(cleanRequired(request.name()), null);
        apply(company, request.industry(), request.website(), request.phone(), request.email(), request.address());
        return CompanyResponse.from(companyRepository.save(company));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public CompanyResponse update(UUID id, CompanyUpdateRequest request) {
        Company company = findActive(id);
        company.setName(cleanRequired(request.name()));
        apply(company, request.industry(), request.website(), request.phone(), request.email(), request.address());
        return CompanyResponse.from(company);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        Company company = findActive(id);
        if (contactRepository.existsByCompanyAndDeletedAtIsNull(company)) {
            throw new ConflictException("Company cannot be deleted while contacts are attached");
        }
        companyRepository.delete(company);
    }

    Company findActive(UUID id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (company.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Company not found");
        }
        return company;
    }

    private void apply(Company company, String industry, String website, String phone, String email, String address) {
        company.setIndustry(normalize(industry));
        company.setWebsite(normalize(website));
        company.setPhone(normalize(phone));
        company.setEmail(normalize(email));
        company.setAddress(normalize(address));
    }

    private String cleanRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
