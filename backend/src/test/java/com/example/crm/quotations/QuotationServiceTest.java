package com.example.crm.quotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.crm.companies.Company;
import com.example.crm.projectvendors.OutreachStatus;
import com.example.crm.projectvendors.ProjectVendor;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.projects.Project;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class QuotationServiceTest {
    @Test
    void receivedQuotationMovesProjectVendorToQuoteReceived() {
        QuotationRepository quotationRepository = mock(QuotationRepository.class);
        ProjectVendorRepository projectVendorRepository = mock(ProjectVendorRepository.class);
        QuotationService service = new QuotationService(quotationRepository, projectVendorRepository);

        User user = new User("Admin", "admin@example.test", "hash", Role.ADMIN);
        Project project = new Project("Test project", "TEST-QUOTE-001", new Company("Test client", user));
        Company vendor = new Company("Test vendor", user);
        ProjectVendor assignment = new ProjectVendor(project, vendor);
        UUID assignmentId = UUID.randomUUID();
        when(projectVendorRepository.findActive(assignmentId)).thenReturn(Optional.of(assignment));
        when(quotationRepository.maxRevision(assignmentId)).thenReturn(0);
        when(quotationRepository.findByProjectVendorIdOrderByRevisionNumberDesc(assignmentId)).thenReturn(List.of());
        when(quotationRepository.save(any(Quotation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(
            assignmentId,
            new QuotationCreateRequest(
                new BigDecimal("24500.00"),
                "USD",
                LocalDate.of(2026, 10, 14),
                LocalDate.of(2026, 11, 14),
                null,
                QuotationStatus.RECEIVED,
                "Initial quote"
            ),
            new CrmUserPrincipal(user)
        );

        assertThat(assignment.getStatus()).isEqualTo(OutreachStatus.QUOTE_RECEIVED);
        verify(quotationRepository).save(any(Quotation.class));
    }
}