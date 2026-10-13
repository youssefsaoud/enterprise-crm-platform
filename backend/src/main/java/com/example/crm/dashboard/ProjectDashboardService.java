package com.example.crm.dashboard;

import com.example.crm.interactions.InteractionResponse;
import com.example.crm.interactions.InteractionService;
import com.example.crm.projectvendors.OutreachStatus;
import com.example.crm.projectvendors.ProjectVendor;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.projects.ProjectRepository;
import com.example.crm.projects.ProjectStatus;
import com.example.crm.quotations.QuotationRepository;
import com.example.crm.quotations.QuotationStatus;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectDashboardService {
    private final ProjectRepository projectRepository;
    private final ProjectVendorRepository projectVendorRepository;
    private final QuotationRepository quotationRepository;
    private final InteractionService interactionService;

    public ProjectDashboardService(ProjectRepository projectRepository, ProjectVendorRepository projectVendorRepository, QuotationRepository quotationRepository, InteractionService interactionService) {
        this.projectRepository = projectRepository;
        this.projectVendorRepository = projectVendorRepository;
        this.quotationRepository = quotationRepository;
        this.interactionService = interactionService;
    }

    @Transactional(readOnly = true)
    public ProjectDashboardSummaryResponse summary(CrmUserPrincipal principal) {
        boolean all = canSeeAll(principal);
        var today = LocalDate.now();
        long total = projectRepository.countVisible(principal.id(), all);
        long active = 0;
        List<ProjectStatusCountResponse> byStatus = new java.util.ArrayList<>();
        for (ProjectStatus status : ProjectStatus.values()) {
            long count = projectRepository.countVisibleByStatus(principal.id(), all, status);
            byStatus.add(new ProjectStatusCountResponse(status, count));
            if (status == ProjectStatus.PLANNED || status == ProjectStatus.IN_PROGRESS || status == ProjectStatus.ON_HOLD) active += count;
        }
        long nearing = projectRepository.countVisibleWithDeadlineBetween(principal.id(), all, today, today.plusDays(14));
        long overdue = projectRepository.countVisibleWithDeadlineBefore(principal.id(), all, today);
        long totalVendors = projectVendorRepository.countVisible(principal.id(), all);
        long contacted = projectVendorRepository.countVisibleByStatusIn(principal.id(), all, EnumSet.of(OutreachStatus.SOW_SENT, OutreachStatus.QUOTE_IN_PROCESS, OutreachStatus.QUOTE_RECEIVED, OutreachStatus.QUOTE_DECLINED));
        long awaiting = projectVendorRepository.countVisibleByStatusIn(principal.id(), all, EnumSet.of(OutreachStatus.SOW_SENT, OutreachStatus.QUOTE_IN_PROCESS));
        long received = quotationRepository.countVisibleByStatusIn(principal.id(), all, EnumSet.of(QuotationStatus.RECEIVED, QuotationStatus.UNDER_REVIEW, QuotationStatus.REVISED, QuotationStatus.SELECTED));
        long pending = quotationRepository.countVisibleByStatusIn(principal.id(), all, EnumSet.of(QuotationStatus.RECEIVED, QuotationStatus.UNDER_REVIEW, QuotationStatus.REVISED));
        double responseRate = totalVendors == 0 ? 0 : Math.round((contacted * 1000.0 / totalVendors)) / 10.0;
        List<ProjectVendor> followUps = projectVendorRepository.findFollowUps(principal.id(), all, today.plusDays(14));
        long overdueFollowUps = followUps.stream().filter(item -> item.getNextFollowUpDate().isBefore(today)).count();
        Map<String, BigDecimal> quoteTotals = new LinkedHashMap<>();
        quotationRepository.findVisible(principal.id(), all).stream()
            .filter(item -> item.getStatus() != QuotationStatus.REJECTED && item.getStatus() != QuotationStatus.WITHDRAWN && item.getStatus() != QuotationStatus.EXPIRED)
            .forEach(item -> quoteTotals.merge(item.getCurrencyCode(), item.getAmount(), BigDecimal::add));
        List<ProjectDashboardActivityResponse> activities = interactionService.search(principal, null, null, null, null, null, null, null, null, OffsetDateTime.now().minusDays(30), null, PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "occurredAt")))
            .getContent().stream().filter(item -> item.projectId() != null).map(this::activity).toList();
        return new ProjectDashboardSummaryResponse(total, active, nearing, overdue, contacted, awaiting, received, pending, responseRate, followUps.size() - overdueFollowUps, overdueFollowUps, byStatus,
            quoteTotals.entrySet().stream().map(item -> new DashboardCurrencyTotalResponse(item.getKey(), item.getValue())).toList(), activities);
    }

    private ProjectDashboardActivityResponse activity(InteractionResponse item) {
        return new ProjectDashboardActivityResponse(item.id(), item.type(), item.subject(), item.occurredAt(), item.authorName(), item.projectId(), item.projectName(), item.projectVendorId(), item.projectVendorName());
    }

    private boolean canSeeAll(CrmUserPrincipal principal) { return principal.role() == Role.ADMIN || principal.role() == Role.MANAGER; }
}
