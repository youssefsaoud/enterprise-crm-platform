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
