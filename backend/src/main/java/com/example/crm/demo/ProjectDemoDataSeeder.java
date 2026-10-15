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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "crm.demo.seed", name = "enabled", havingValue = "true")
public class ProjectDemoDataSeeder {
    static final String MARKER = "orqestra project demo";
    static final int ASSIGNMENTS_PER_PROJECT = 4;
    static final int ACTIVITIES_PER_PROJECT = 6;

    private static final Logger log = LoggerFactory.getLogger(ProjectDemoDataSeeder.class);
    private static final String DEMO_DOMAIN = "demo-crm.local";
    private static final List<OutreachStatus> OPEN_OUTREACH_STATUSES = List.of(
        OutreachStatus.NOT_CONTACTED,
        OutreachStatus.NO_CONTACT_REACHED,
        OutreachStatus.SOW_SENT,
        OutreachStatus.QUOTE_IN_PROCESS,
        OutreachStatus.QUOTE_DECLINED
    );
    private static final List<ActivitySeed> ACTIVITY_SEEDS = List.of(
        new ActivitySeed(InteractionType.MEETING, "Project kickoff", "Aligned delivery milestones, ownership, and acceptance criteria."),
        new ActivitySeed(InteractionType.NOTE, "Requirements review", "Confirmed site constraints and the final scope of work."),
        new ActivitySeed(InteractionType.CALL, "Vendor shortlist review", "Reviewed vendor capability, availability, and commercial fit."),
        new ActivitySeed(InteractionType.EMAIL, "Scope of work issued", "Sent the approved scope and response timeline to shortlisted vendors."),
        new ActivitySeed(InteractionType.MEETING, "Quotation evaluation", "Compared commercial terms, validity dates, and delivery assumptions."),
        new ActivitySeed(InteractionType.FOLLOW_UP, "Delivery coordination", "Recorded the next operational checkpoint and open actions.")
    );

    private final ProjectRepository projectRepository;
    private final ProjectVendorRepository projectVendorRepository;
    private final QuotationRepository quotationRepository;
    private final InteractionRepository interactionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DataSource dataSource;

    public ProjectDemoDataSeeder(
        ProjectRepository projectRepository,
        ProjectVendorRepository projectVendorRepository,
        QuotationRepository quotationRepository,
        InteractionRepository interactionRepository,
        CompanyRepository companyRepository,
        UserRepository userRepository,
        DataSource dataSource
    ) {
        this.projectRepository = projectRepository;
        this.projectVendorRepository = projectVendorRepository;
        this.quotationRepository = quotationRepository;
        this.interactionRepository = interactionRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    @Bean
    @Order(100)
    CommandLineRunner seedProjectData(TransactionTemplate transactionTemplate) {
        return args -> transactionTemplate.executeWithoutResult(status -> seed());
    }

    public void seed() {
        assertLocalDevelopmentDatabase();

        List<Company> demoCompanies = companyRepository.findAll().stream()
            .filter(company -> company.getDeletedAt() == null)
            .filter(company -> company.getEmail() != null && company.getEmail().toLowerCase(Locale.ROOT).contains(DEMO_DOMAIN))
            .sorted(Comparator.comparing(Company::getName))
            .toList();
        List<User> associates = userRepository.findByRoleAndActiveTrueOrderByFullNameAsc(Role.SALES_REP);
        if (demoCompanies.size() < 20 || associates.isEmpty()) {
            throw new IllegalStateException("The core CRM demo dataset must exist before project demo data can be seeded.");
        }

        List<Company> clients = demoCompanies.subList(0, 15);
        List<Company> vendors = new ArrayList<>(demoCompanies);
        vendors.forEach(company -> company.getRoles().add(CompanyRole.VENDOR));
        companyRepository.saveAll(vendors);

        LocalDate today = LocalDate.now();
        OffsetDateTime now = OffsetDateTime.now();
        int createdProjects = 0;
        int createdAssignments = 0;
        int createdQuotations = 0;
        int createdActivities = 0;

        List<ProjectSeed> seeds = projectSeeds();
        for (int projectIndex = 0; projectIndex < seeds.size(); projectIndex++) {
            ProjectSeed seed = seeds.get(projectIndex);
            Project project = projectRepository.findByReference(seed.reference()).orElse(null);
            if (project == null) {
                project = createProject(seed, clients.get(seed.clientIndex() % clients.size()), associates.get(projectIndex % associates.size()), today);
                createdProjects++;
            }

            List<ProjectVendor> assignments = new ArrayList<>();
            List<List<Quotation>> projectQuotations = new ArrayList<>();
            boolean projectHasSelection = quotationRepository.existsByProjectVendorProjectIdAndSelectedTrue(project.getId());
            for (int vendorIndex = 0; vendorIndex < ASSIGNMENTS_PER_PROJECT; vendorIndex++) {
                Company vendor = vendors.get((projectIndex * 3 + vendorIndex * 7) % vendors.size());
                ProjectVendor assignment = projectVendorRepository
                    .findByProjectIdAndVendorIdAndDeletedAtIsNull(project.getId(), vendor.getId())
                    .orElse(null);
                if (assignment == null) {
                    assignment = createAssignment(project, vendor, associates, projectIndex, vendorIndex, today);
                    createdAssignments++;
                }
                assignments.add(assignment);

                List<Quotation> quotations = new ArrayList<>();
                if (vendorIndex < 2) {
                    Map<Integer, Quotation> existing = quotationRepository
                        .findByProjectVendorIdOrderByRevisionNumberDesc(assignment.getId())
                        .stream()
                        .collect(Collectors.toMap(Quotation::getRevisionNumber, Function.identity()));
                    boolean selectForProject = projectIndex % 3 == 0;
                    int selectedVendorIndex = (projectIndex / 3) % 2;
                    if (vendorIndex == 0) {
                        boolean missingRevisionOne = !existing.containsKey(1);
                        boolean missingRevisionTwo = !existing.containsKey(2);
                        quotations.add(ensureQuotation(existing, assignment, project, projectIndex, vendorIndex, 1, false, today));
                        boolean selected = !projectHasSelection && selectForProject && selectedVendorIndex == 0;
                        quotations.add(ensureQuotation(existing, assignment, project, projectIndex, vendorIndex, 2, selected, today));
                        createdQuotations += (missingRevisionOne ? 1 : 0) + (missingRevisionTwo ? 1 : 0);
                        projectHasSelection = projectHasSelection || selected;
                    } else {
                        boolean missingRevisionOne = !existing.containsKey(1);
                        boolean selected = !projectHasSelection && selectForProject && selectedVendorIndex == 1;
                        quotations.add(ensureQuotation(existing, assignment, project, projectIndex, vendorIndex, 1, selected, today));
                        createdQuotations += missingRevisionOne ? 1 : 0;
                        projectHasSelection = projectHasSelection || selected;
                    }
                }
                projectQuotations.add(quotations);
            }

            for (int activityIndex = 0; activityIndex < ACTIVITY_SEEDS.size(); activityIndex++) {
                ActivitySeed activitySeed = ACTIVITY_SEEDS.get(activityIndex);
                if (interactionRepository.existsByProjectIdAndSubjectAndDeletedAtIsNull(project.getId(), activitySeed.subject())) continue;
                ProjectVendor assignment = activityIndex >= 2 ? assignments.get((activityIndex - 2) % assignments.size()) : null;
                Quotation quotation = activityIndex == 4 && !projectQuotations.get(0).isEmpty()
                    ? projectQuotations.get(0).get(projectQuotations.get(0).size() - 1)
                    : null;
                createActivity(project, assignment, quotation, activitySeed, projectIndex, activityIndex, now);
                createdActivities++;
            }
        }

        log.info(
            "Orqestra project demo seed completed: projectsCreated={}, assignmentsCreated={}, quotationsCreated={}, activitiesCreated={}",
            createdProjects,
            createdAssignments,
            createdQuotations,
            createdActivities
        );
    }

    private Project createProject(ProjectSeed seed, Company client, User associate, LocalDate today) {
        Project project = new Project(seed.name(), seed.reference(), client);
        project.setDescription(seed.description());
        project.setCategory(seed.category());
        project.setLocation(seed.location());
        project.setDeadline(today.plusDays(seed.deadlineOffset()));
        project.setEstimatedBudget(seed.budget());
        project.setCurrencyCode(seed.currency());
        project.setAssignedAssociate(associate);
        project.setStatus(seed.status());
        project.setScopeOfWork(seed.scopeOfWork());
        project.setNotes(MARKER);
        return projectRepository.save(project);
    }

    private ProjectVendor createAssignment(Project project, Company vendor, List<User> associates, int projectIndex, int vendorIndex, LocalDate today) {
        ProjectVendor assignment = new ProjectVendor(project, vendor);
        assignment.setResponsibleAssociate(associates.get((projectIndex + vendorIndex) % associates.size()));
        OutreachStatus status = vendorIndex < 2
            ? OutreachStatus.QUOTE_RECEIVED
            : OPEN_OUTREACH_STATUSES.get((projectIndex + vendorIndex) % OPEN_OUTREACH_STATUSES.size());
        assignment.setStatus(status);
        if (status != OutreachStatus.NOT_CONTACTED) {
            assignment.setFirstContactDate(today.minusDays(28L + (projectIndex % 10)));
            assignment.setLastContactDate(today.minusDays(2L + ((projectIndex + vendorIndex) % 9)));
        }
        int followUpOffset = (projectIndex + vendorIndex) % 4 == 0
            ? -(1 + ((projectIndex + vendorIndex) % 6))
            : 2 + ((projectIndex * 2 + vendorIndex) % 13);
        assignment.setNextFollowUpDate(today.plusDays(followUpOffset));
        assignment.setNotes(MARKER + ": " + outreachNote(status));
        return projectVendorRepository.save(assignment);
    }

    private Quotation ensureQuotation(Map<Integer, Quotation> existing, ProjectVendor assignment, Project project, int projectIndex, int vendorIndex, int revision, boolean selected, LocalDate today) {
        Quotation quotation = existing.get(revision);
        if (quotation != null) return quotation;

        BigDecimal factor = revision == 1
            ? new BigDecimal(vendorIndex == 0 ? "0.84" : "0.91")
            : new BigDecimal("0.79");
        BigDecimal amount = project.getEstimatedBudget().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        LocalDate receivedDate = today.minusDays(22L - ((projectIndex + vendorIndex + revision) % 16));
        quotation = new Quotation(assignment, amount, project.getCurrencyCode(), receivedDate, revision);
        quotation.setValidUntil(receivedDate.plusDays(revision == 1 ? 30 : 45));
        quotation.setSelected(selected);
        quotation.setStatus(selected
            ? QuotationStatus.SELECTED
            : revision == 1 && vendorIndex == 0 ? QuotationStatus.REVISED
            : revision == 2 ? QuotationStatus.UNDER_REVIEW : QuotationStatus.RECEIVED);
        quotation.setNotes(MARKER + ": commercial response from " + assignment.getVendor().getName() + ", revision " + revision + ".");
        return quotationRepository.save(quotation);
    }

    private void createActivity(Project project, ProjectVendor assignment, Quotation quotation, ActivitySeed seed, int projectIndex, int activityIndex, OffsetDateTime now) {
        long daysAgo = (projectIndex * 4L + activityIndex * 3L) % 75;
        OffsetDateTime occurredAt = now.minusDays(daysAgo).withHour(9 + activityIndex).withMinute((projectIndex * 7) % 60).withSecond(0).withNano(0);
        Interaction activity = new Interaction(project.getAssignedAssociate(), seed.type(), seed.subject(), occurredAt);
        activity.setProject(project);
        activity.setCompany(assignment == null ? project.getClient() : assignment.getVendor());
        activity.setProjectVendor(assignment);
        activity.setQuotation(quotation);
        activity.setDescription(MARKER + ": " + seed.description());
        if (seed.type() == InteractionType.FOLLOW_UP) {
            long followUpOffset = projectIndex % 4 == 0 ? -(2 + projectIndex % 5) : 3 + projectIndex % 12;
            activity.setFollowUpAt(now.plusDays(followUpOffset).withHour(10).withMinute(0).withSecond(0).withNano(0));
        }
        interactionRepository.save(activity);
    }

    private void assertLocalDevelopmentDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            String database = connection.getCatalog();
            String url = connection.getMetaData().getURL();
            boolean localUrl = url != null && (
                url.startsWith("jdbc:postgresql://localhost:")
                    || url.startsWith("jdbc:postgresql://localhost/")
                    || url.startsWith("jdbc:postgresql://127.0.0.1:")
                    || url.startsWith("jdbc:postgresql://127.0.0.1/")
            );
            if (!"crm_dev".equals(database) || !localUrl) {
                throw new IllegalStateException("Project demo seeding is restricted to the local crm_dev database.");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not verify the development database before seeding.", exception);
        }
    }

    private String outreachNote(OutreachStatus status) {
        return switch (status) {
            case NOT_CONTACTED -> "Vendor identified for the next outreach round.";
            case NO_CONTACT_REACHED -> "Initial call was unanswered; retry by email.";
            case SOW_SENT -> "Scope issued with a requested response date.";
            case QUOTE_IN_PROCESS -> "Vendor confirmed that pricing is in preparation.";
            case QUOTE_RECEIVED -> "Commercial response received for review.";
            case QUOTE_DECLINED -> "Vendor declined because of schedule constraints.";
        };
    }

    static List<ProjectSeed> projectSeeds() {
        return List.of(
            seed("ORQ-2026-001", "Regional branch network refresh", "Network Infrastructure", "Casablanca, Morocco", 96000, "USD", ProjectStatus.PLANNED, 12, 0, "Upgrade switching, wireless coverage, and branch connectivity.", "Survey four branches, replace edge hardware, validate segmentation, and hand over network diagrams."),
            seed("ORQ-2026-002", "Hotel HVAC preventive maintenance", "Facility Maintenance", "Agadir, Morocco", 780000, "MAD", ProjectStatus.IN_PROGRESS, 6, 1, "Annual HVAC maintenance for guest rooms and shared spaces.", "Inspect air handlers, complete seasonal servicing, replace worn components, and document test results."),
            seed("ORQ-2026-003", "Warehouse electrical capacity upgrade", "Electrical Services", "Tangier, Morocco", 185000, "EUR", ProjectStatus.IN_PROGRESS, -8, 2, "Increase warehouse electrical capacity for new automation equipment.", "Survey panels, install distribution upgrades, test protection systems, and certify the completed work."),
            seed("ORQ-2026-004", "Multi-site commercial cleaning contract", "Commercial Services", "Rabat, Morocco", 420000, "MAD", ProjectStatus.COMPLETED, -32, 3, "Consolidated cleaning services across offices and customer areas.", "Provide staffing, consumables, quality inspections, and monthly service reporting for five locations."),
            seed("ORQ-2026-005", "Corporate access-control installation", "Physical Security", "Marrakech, Morocco", 114000, "USD", ProjectStatus.ON_HOLD, 28, 4, "Badge access and visitor control for a mixed-use office campus.", "Install controllers, readers, credentials, visitor workflows, training, and as-built documentation."),
            seed("ORQ-2026-006", "Diagnostic imaging equipment procurement", "Healthcare Equipment", "Fes, Morocco", 310000, "EUR", ProjectStatus.PLANNED, 44, 5, "Procure and commission diagnostic imaging equipment for a regional clinic.", "Confirm technical compliance, delivery conditions, installation support, calibration, and user training."),
            seed("ORQ-2026-007", "Cold-chain logistics service renewal", "Logistics", "Dakhla, Morocco", 132000, "USD", ProjectStatus.IN_PROGRESS, 18, 6, "Renew temperature-controlled transport coverage for regional distribution.", "Define routes, temperature controls, incident reporting, service levels, and monthly performance reviews."),
            seed("ORQ-2026-008", "Private cloud platform modernization", "Cloud Infrastructure", "Casablanca, Morocco", 225000, "EUR", ProjectStatus.IN_PROGRESS, 35, 7, "Modernize compute, storage, backup, and observability for core systems.", "Design target architecture, migrate workloads in waves, test recovery, and transfer operational knowledge."),
            seed("ORQ-2026-009", "Solar plant maintenance program", "Renewable Energy", "Ouarzazate, Morocco", 680000, "MAD", ProjectStatus.COMPLETED, -18, 8, "Scheduled maintenance for photovoltaic field and inverter systems.", "Inspect arrays, service inverters, perform thermal scans, resolve defects, and issue maintenance reports."),
            seed("ORQ-2026-010", "Industrial fire-safety compliance review", "Safety Services", "Kenitra, Morocco", 86000, "USD", ProjectStatus.ON_HOLD, 21, 9, "Review fire detection, suppression, evacuation, and inspection readiness.", "Audit systems, test devices, update evacuation plans, train wardens, and close compliance findings."),
            seed("ORQ-2026-011", "Annual conference infrastructure services", "Event Services", "Marrakech, Morocco", 124000, "EUR", ProjectStatus.PLANNED, 55, 10, "Provide temporary network, power, staging, and technical support for a conference.", "Design venue infrastructure, install and test systems, staff live operations, and complete teardown."),
            seed("ORQ-2026-012", "Headquarters workspace furniture supply", "Equipment Supply", "Rabat, Morocco", 920000, "MAD", ProjectStatus.IN_PROGRESS, 24, 11, "Supply ergonomic workstations and meeting-room furniture for a new headquarters.", "Finalize schedules, provide samples, deliver in phases, install furniture, and resolve punch-list items."),
            seed("ORQ-2026-013", "Cold-storage monitoring deployment", "Monitoring Systems", "Agadir, Morocco", 148000, "USD", ProjectStatus.IN_PROGRESS, -4, 12, "Deploy continuous temperature and humidity monitoring across cold rooms.", "Install sensors and gateways, configure alerts, validate calibration, and train operations staff."),
            seed("ORQ-2026-014", "Elevator preventive maintenance agreement", "Facility Maintenance", "Casablanca, Morocco", 98000, "EUR", ProjectStatus.COMPLETED, -40, 13, "Preventive and corrective maintenance for six passenger elevators.", "Complete scheduled inspections, emergency response coverage, parts reporting, and compliance certification."),
            seed("ORQ-2026-015", "Laboratory equipment supply package", "Scientific Equipment", "Rabat, Morocco", 1260000, "MAD", ProjectStatus.ON_HOLD, 63, 14, "Supply core analytical instruments for an expanded quality-control laboratory.", "Validate specifications, supply equipment, commission instruments, provide training, and deliver warranties."),
            seed("ORQ-2026-016", "Fleet telematics implementation", "Transportation Technology", "Tangier, Morocco", 168000, "USD", ProjectStatus.PLANNED, 38, 0, "Implement vehicle tracking, driver safety, and maintenance telemetry.", "Install devices, integrate reporting, configure policies, pilot the service, and train fleet supervisors."),
            seed("ORQ-2026-017", "Water treatment pump replacement", "Industrial Equipment", "Safi, Morocco", 138000, "EUR", ProjectStatus.IN_PROGRESS, 9, 1, "Replace aging process pumps while maintaining plant continuity.", "Confirm hydraulic requirements, stage equipment, execute shutdown work, test performance, and supply spares."),
            seed("ORQ-2026-018", "Business hotel guest-floor renovation", "Renovation", "Casablanca, Morocco", 1750000, "MAD", ProjectStatus.IN_PROGRESS, 72, 2, "Renovate guest rooms and corridors across two occupied hotel floors.", "Sequence demolition and finishes, protect occupied areas, coordinate inspections, and complete staged handover."),
            seed("ORQ-2026-019", "Municipal LED lighting upgrade", "Public Infrastructure", "Sale, Morocco", 245000, "USD", ProjectStatus.COMPLETED, -26, 3, "Replace high-consumption public-area lighting with managed LED fixtures.", "Survey circuits, provide fixtures and controls, install by zone, test illumination, and document energy savings."),
            seed("ORQ-2026-020", "Data-center cooling resilience upgrade", "Critical Facilities", "Casablanca, Morocco", 214000, "EUR", ProjectStatus.CANCELLED, 90, 4, "Increase cooling resilience for a growing data-center footprint.", "Assess thermal load, design redundant cooling, price implementation options, and provide a migration plan.")
        );
    }

    private static ProjectSeed seed(String reference, String name, String category, String location, long budget, String currency, ProjectStatus status, int deadlineOffset, int clientIndex, String description, String scopeOfWork) {
        return new ProjectSeed(reference, name, category, location, BigDecimal.valueOf(budget), currency, status, deadlineOffset, clientIndex, description, scopeOfWork);
    }

    record ProjectSeed(String reference, String name, String category, String location, BigDecimal budget, String currency, ProjectStatus status, int deadlineOffset, int clientIndex, String description, String scopeOfWork) {}

    private record ActivitySeed(InteractionType type, String subject, String description) {}
}
