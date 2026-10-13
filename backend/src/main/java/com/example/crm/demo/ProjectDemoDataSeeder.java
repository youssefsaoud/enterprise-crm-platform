package com.example.crm.demo;

import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.companies.CompanyRole;
import com.example.crm.interactions.Interaction;
import com.example.crm.interactions.InteractionRepository;
import com.example.crm.interactions.InteractionType;
import com.example.crm.projectvendors.OutreachStatus;
import com.example.crm.projectvendors.ProjectVendor;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.projects.Project;
import com.example.crm.projects.ProjectRepository;
import com.example.crm.projects.ProjectStatus;
import com.example.crm.quotations.Quotation;
import com.example.crm.quotations.QuotationRepository;
import com.example.crm.quotations.QuotationStatus;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "crm.demo.seed", name = "enabled", havingValue = "true")
public class ProjectDemoDataSeeder {
    private static final String MARKER = "portfolio project demo";

    private final ProjectRepository projectRepository;
    private final ProjectVendorRepository projectVendorRepository;
    private final QuotationRepository quotationRepository;
    private final InteractionRepository interactionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public ProjectDemoDataSeeder(ProjectRepository projectRepository, ProjectVendorRepository projectVendorRepository, QuotationRepository quotationRepository, InteractionRepository interactionRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectVendorRepository = projectVendorRepository;
        this.quotationRepository = quotationRepository;
        this.interactionRepository = interactionRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Bean
    @Order(100)
    CommandLineRunner seedProjectData() {
        return args -> seed();
    }

    @Transactional
    public void seed() {
        List<Company> companies = companyRepository.findAll().stream()
            .filter(item -> item.getEmail() != null && item.getEmail().toLowerCase(Locale.ROOT).contains("demo-crm.local"))
            .toList();
        List<User> associates = userRepository.findByRoleAndActiveTrueOrderByFullNameAsc(Role.SALES_REP);
        if (companies.size() < 6 || associates.isEmpty()) return;

        List<Company> clients = new ArrayList<>(companies.subList(0, Math.min(12, companies.size())));
        List<Company> vendors = new ArrayList<>(companies.subList(Math.max(0, companies.size() / 2), companies.size()));
        vendors.forEach(company -> company.getRoles().add(CompanyRole.VENDOR));
        companyRepository.saveAll(vendors);

        for (int i = 0; i < 12; i++) {
            String reference = String.format(Locale.ROOT, "DEMO-P-%03d", i + 1);
            Project project = projectRepository.findByReference(reference).orElse(null);
            if (project == null) {
                Company client = clients.get(i % clients.size());
                project = new Project("Portfolio project " + (i + 1), reference, client);
                project.setDescription("Fictional " + MARKER + " for an industry-independent vendor workflow.");
                project.setCategory(client.getIndustry());
                project.setLocation(client.getAddress());
                project.setDeadline(LocalDate.now().plusDays(i * 5L - 10));
                project.setEstimatedBudget(BigDecimal.valueOf(25000L + i * 7250L));
                project.setCurrencyCode(new String[] {"MAD", "EUR", "USD"}[i % 3]);
                project.setAssignedAssociate(associates.get(i % associates.size()));
                project.setStatus(i % 5 == 0 ? ProjectStatus.COMPLETED : ProjectStatus.IN_PROGRESS);
                project.setScopeOfWork("Deliverables, acceptance criteria, and vendor response timeline for " + project.getName() + ".");
                project.setNotes(MARKER);
                project = projectRepository.save(project);
            }

            for (int vendorIndex = 0; vendorIndex < 3; vendorIndex++) {
                Company vendor = vendors.get((i + vendorIndex) % vendors.size());
                ProjectVendor assignment = projectVendorRepository.findByProjectIdAndVendorIdAndDeletedAtIsNull(project.getId(), vendor.getId()).orElse(null);
                if (assignment == null) assignment = new ProjectVendor(project, vendor);
                assignment.setResponsibleAssociate(associates.get((i + vendorIndex) % associates.size()));
                assignment.setStatus(OutreachStatus.values()[(i + vendorIndex) % OutreachStatus.values().length]);
                assignment.setNextFollowUpDate(LocalDate.now().plusDays((i + vendorIndex) % 12 - 3));
                assignment = projectVendorRepository.save(assignment);

                if (quotationRepository.findByProjectVendorIdOrderByRevisionNumberDesc(assignment.getId()).isEmpty()) {
                    Quotation quote = new Quotation(assignment, BigDecimal.valueOf(18000L + i * 2200L + vendorIndex * 900L), new String[] {"MAD", "EUR", "USD"}[(i + vendorIndex) % 3], LocalDate.now().minusDays((i + vendorIndex) % 30), 1);
                    quote.setStatus(vendorIndex == 2 ? QuotationStatus.UNDER_REVIEW : QuotationStatus.RECEIVED);
                    quote.setNotes(MARKER);
                    quotationRepository.save(quote);
                }

                boolean activityExists = interactionRepository.findAll().stream()
                    .anyMatch(item -> item.getProjectVendor() != null && item.getProjectVendor().getId().equals(assignment.getId()) && item.getDescription() != null && item.getDescription().contains(MARKER));
                if (!activityExists) {
                    Interaction activity = new Interaction(project.getAssignedAssociate(), InteractionType.FOLLOW_UP, "Vendor follow-up", OffsetDateTime.now().minusDays(i % 10));
                    activity.setCompany(vendor);
                    activity.setProject(project);
                    activity.setProjectVendor(assignment);
                    activity.setDescription(MARKER + " vendor outreach activity.");
                    activity.setFollowUpAt(OffsetDateTime.now().plusDays(3));
                    interactionRepository.save(activity);
                }
            }
        }
    }
}
