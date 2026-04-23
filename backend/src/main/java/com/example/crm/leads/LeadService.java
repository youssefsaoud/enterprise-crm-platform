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
