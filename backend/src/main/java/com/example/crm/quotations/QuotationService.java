package com.example.crm.quotations;

import com.example.crm.common.web.ConflictException;
import com.example.crm.common.web.ResourceNotFoundException;
import com.example.crm.projectvendors.ProjectVendor;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class QuotationService {
    private final QuotationRepository quotationRepository;
    private final ProjectVendorRepository projectVendorRepository;

    public QuotationService(QuotationRepository quotationRepository, ProjectVendorRepository projectVendorRepository) {
        this.quotationRepository = quotationRepository;
        this.projectVendorRepository = projectVendorRepository;
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> list(UUID projectVendorId, CrmUserPrincipal principal) {
        visibleAssignment(projectVendorId, principal);
        return quotationRepository.findByProjectVendorIdOrderByRevisionNumberDesc(projectVendorId).stream().map(QuotationResponse::from).toList();
    }

    @Transactional
    public QuotationResponse create(UUID projectVendorId, QuotationCreateRequest request, CrmUserPrincipal principal) {
        ProjectVendor assignment = editableAssignment(projectVendorId, principal);
        validateDates(request.receivedDate(), request.validUntil());
        int revision = request.revisionNumber() == null ? quotationRepository.maxRevision(projectVendorId) + 1 : request.revisionNumber();
        if (revision < 1 || quotationRepository.findByProjectVendorIdOrderByRevisionNumberDesc(projectVendorId).stream().anyMatch(value -> value.getRevisionNumber() == revision)) throw new ConflictException("Quotation revision already exists for this vendor");
        Quotation quotation = new Quotation(assignment, request.amount(), normalizeCurrency(request.currencyCode()), request.receivedDate(), revision);
        quotation.setValidUntil(request.validUntil());
        quotation.setStatus(request.status() == null ? QuotationStatus.RECEIVED : request.status());
        quotation.setNotes(normalize(request.notes()));
        return QuotationResponse.from(quotationRepository.save(quotation));
    }

    @Transactional
    public QuotationResponse update(UUID id, QuotationUpdateRequest request, CrmUserPrincipal principal) {
        Quotation quotation = find(id);
        editableAssignment(quotation.getProjectVendor().getId(), principal);
        if (quotation.isSelected() && request.status() != QuotationStatus.SELECTED) throw new ConflictException("Selected quotation must be replaced through the selection workflow");
        validateDates(request.receivedDate(), request.validUntil());
        quotation.setAmount(request.amount());
        quotation.setCurrencyCode(normalizeCurrency(request.currencyCode()));
        quotation.setReceivedDate(request.receivedDate());
        quotation.setValidUntil(request.validUntil());
        quotation.setStatus(request.status());
        quotation.setNotes(normalize(request.notes()));
        return QuotationResponse.from(quotation);
    }

    @Transactional
    public QuotationResponse select(UUID id, CrmUserPrincipal principal) {
        if (!canManageAll(principal)) throw new AccessDeniedException("Only administrators and managers can select a quotation");
        Quotation quotation = find(id);
        ProjectVendor assignment = quotation.getProjectVendor();
        visibleAssignment(assignment.getId(), principal);
        quotationRepository.clearSelectionForProject(assignment.getProject().getId());
        quotation.setSelected(true);
        quotation.setStatus(QuotationStatus.SELECTED);
        return QuotationResponse.from(quotation);
    }

    private ProjectVendor visibleAssignment(UUID id, CrmUserPrincipal principal) {
        ProjectVendor assignment = projectVendorRepository.findActive(id).orElseThrow(() -> new ResourceNotFoundException("Project vendor assignment not found"));
        if (canManageAll(principal)) return assignment;
        if (assignment.getProject().getAssignedAssociate() == null || !assignment.getProject().getAssignedAssociate().getId().equals(principal.id())) throw new ResourceNotFoundException("Project vendor assignment not found");
        return assignment;
    }

    private ProjectVendor editableAssignment(UUID id, CrmUserPrincipal principal) {
        return visibleAssignment(id, principal);
    }

    private Quotation find(UUID id) { return quotationRepository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Quotation not found")); }
    private boolean canManageAll(CrmUserPrincipal principal) { return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER; }
    private void validateDates(LocalDate received, LocalDate validUntil) { if (validUntil != null && validUntil.isBefore(received)) throw new ConflictException("Validity date cannot be before received date"); }
    private String normalizeCurrency(String value) { return value.trim().toUpperCase(); }
    private String normalize(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
}
