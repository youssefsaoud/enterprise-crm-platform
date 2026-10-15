package com.example.crm.demo;

import com.example.crm.companies.Company;
import com.example.crm.companies.CompanyRepository;
import com.example.crm.contacts.Contact;
import com.example.crm.contacts.ContactRepository;
import com.example.crm.interactions.Interaction;
import com.example.crm.interactions.InteractionRepository;
import com.example.crm.interactions.InteractionType;
import com.example.crm.leads.Lead;
import com.example.crm.leads.LeadEvent;
import com.example.crm.leads.LeadEventRepository;
import com.example.crm.leads.LeadEventType;
import com.example.crm.leads.LeadRepository;
import com.example.crm.leads.LeadSource;
import com.example.crm.leads.LeadStatus;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.opportunities.OpportunityRepository;
import com.example.crm.opportunities.OpportunityStage;
import com.example.crm.users.Role;
import com.example.crm.users.User;
import com.example.crm.users.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "crm.demo.seed", name = "enabled", havingValue = "true")
public class DemoDataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String DEMO_DOMAIN = "demo-crm.local";
    private static final String DEMO_MARKER = "portfolio demo data";
    private static final String DEMO_PASSWORD = "DemoPass2026!";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final LeadEventRepository leadEventRepository;
    private final OpportunityRepository opportunityRepository;
    private final InteractionRepository interactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(
        UserRepository userRepository,
        CompanyRepository companyRepository,
        ContactRepository contactRepository,
        LeadRepository leadRepository,
        LeadEventRepository leadEventRepository,
        OpportunityRepository opportunityRepository,
        InteractionRepository interactionRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.leadRepository = leadRepository;
        this.leadEventRepository = leadEventRepository;
        this.opportunityRepository = opportunityRepository;
        this.interactionRepository = interactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Order(10)
    CommandLineRunner seedDemoData() {
        return args -> seed();
    }

    @Transactional
    public void seed() {
        DemoCounts existing = demoCounts();
        if (existing.isComplete()) {
            log.info("CRM demo dataset already exists; skipping seed.");
            return;
        }
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Partial CRM demo dataset detected. Remove demo records before running the seeder again.");
        }

        User admin = userRepository.findByRoleAndActiveTrueOrderByFullNameAsc(Role.ADMIN).stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("An active admin user is required before seeding demo data."));

        List<User> demoUsers = createUsers();
        List<User> staff = new ArrayList<>();
        staff.add(admin);
        staff.addAll(demoUsers);

        List<Company> companies = createCompanies(staff);
        List<Contact> contacts = createContacts(companies);
        List<Lead> leads = createLeads(companies, staff, admin);
        createLeadEvents(leads, staff);
        List<Opportunity> opportunities = createOpportunities(companies, contacts, leads, staff);
        createInteractions(companies, contacts, leads, opportunities, staff);

        log.info("CRM demo dataset inserted: users={}, companies={}, contacts={}, leads={}, opportunities={}, interactions={}",
            demoUsers.size(), companies.size(), contacts.size(), leads.size(), opportunities.size(), 150);
    }

    private DemoCounts demoCounts() {
        long users = userRepository.findAll().stream().filter(user -> isDemoEmail(user.getEmail())).count();
        long companies = companyRepository.findAll().stream().filter(company -> isDemoEmail(company.getEmail())).count();
        long contacts = contactRepository.findAll().stream().filter(contact -> isDemoEmail(contact.getEmail())).count();
        long leads = leadRepository.findAll().stream().filter(lead -> isDemoEmail(lead.getEmail())).count();
        long opportunities = opportunityRepository.findAll().stream()
            .filter(opportunity -> containsMarker(opportunity.getDescription())).count();
        long interactions = interactionRepository.findAll().stream()
            .filter(interaction -> containsMarker(interaction.getDescription())).count();
        return new DemoCounts(users, companies, contacts, leads, opportunities, interactions);
    }

    private List<User> createUsers() {
        List<User> users = List.of(
            new User("Sara Bennani", "sara.bennani@" + DEMO_DOMAIN, passwordEncoder.encode(DEMO_PASSWORD), Role.MANAGER),
            new User("Yassine El Amrani", "yassine.elamrani@" + DEMO_DOMAIN, passwordEncoder.encode(DEMO_PASSWORD), Role.SALES_REP),
            new User("Amina Haddad", "amina.haddad@" + DEMO_DOMAIN, passwordEncoder.encode(DEMO_PASSWORD), Role.SALES_REP),
            new User("Omar Ait Lahcen", "omar.aitlahcen@" + DEMO_DOMAIN, passwordEncoder.encode(DEMO_PASSWORD), Role.SALES_REP)
        );
        return userRepository.saveAll(users);
    }

    private List<Company> createCompanies(List<User> staff) {
        String[][] rows = {
            {"Atlas Cloud Services", "Cloud Infrastructure", "Casablanca, Morocco"},
            {"Marrakech Retail Group", "Retail", "Marrakech, Morocco"},
            {"Tanger Freight Network", "Logistics", "Tangier, Morocco"},
            {"Rabat Civic Tech", "Software", "Rabat, Morocco"},
            {"Agadir Blue Resorts", "Hospitality", "Agadir, Morocco"},
            {"Fes Learning Labs", "Education", "Fes, Morocco"},
            {"Ouarzazate Solar Works", "Energy", "Ouarzazate, Morocco"},
            {"Casablanca Fintech Studio", "Financial Services", "Casablanca, Morocco"},
            {"Kenitra Auto Parts", "Manufacturing", "Kenitra, Morocco"},
            {"Dakhla Ocean Foods", "Food Distribution", "Dakhla, Morocco"},
            {"Nordline Analytics", "Business Intelligence", "Paris, France"},
            {"SummitPay Solutions", "Payments", "London, United Kingdom"},
            {"BlueHarbor Logistics", "Logistics", "Rotterdam, Netherlands"},
            {"UrbanGrid Mobility", "Transportation", "Berlin, Germany"},
            {"Medina Health Systems", "Healthcare", "Tunis, Tunisia"},
            {"NovaCommerce Labs", "E-commerce", "Barcelona, Spain"},
            {"Apex Green Energy", "Energy", "Lisbon, Portugal"},
            {"BrightPath Consulting", "Consulting", "Dubai, UAE"},
            {"Sahara Data Exchange", "Data Services", "Laayoune, Morocco"},
            {"Cedar Grove Imports", "Wholesale", "Montreal, Canada"},
            {"PeakPoint Capital", "Investment", "New York, USA"},
            {"OliveStone Foods", "Agriculture", "Meknes, Morocco"},
            {"TerraBuild Engineering", "Construction", "Doha, Qatar"},
            {"ClearWave Telecom", "Telecommunications", "Brussels, Belgium"},
            {"RedCity Creative", "Marketing", "Marrakech, Morocco"},
            {"SilverGate Insurance", "Insurance", "Geneva, Switzerland"},
            {"PalmBridge Travel", "Travel", "Istanbul, Turkey"},
            {"HarborByte Security", "Cybersecurity", "Dublin, Ireland"},
            {"Maghreb Market Hub", "Marketplace", "Casablanca, Morocco"},
            {"Orion Office Systems", "Office Technology", "Stockholm, Sweden"}
        };
        List<Company> companies = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            String slug = slug(rows[i][0]);
            Company company = new Company(rows[i][0], staff.get((i + 1) % staff.size()));
            company.setIndustry(rows[i][1]);
            company.setAddress(rows[i][2]);
            company.setWebsite("https://" + slug + "." + DEMO_DOMAIN);
            company.setEmail("hello@" + slug + "." + DEMO_DOMAIN);
            company.setPhone(phone(i));
            companies.add(company);
        }
        return companyRepository.saveAll(companies);
    }

    private List<Contact> createContacts(List<Company> companies) {
        String[] firstNames = {"Nadia", "Karim", "Leila", "Youssef", "Salma", "Mehdi", "Hajar", "Anas", "Imane", "Rachid", "Sofia", "Adam", "Meryem", "Othmane", "Lina", "Reda", "Claire", "Marc", "Elena", "Daniel"};
        String[] lastNames = {"Berrada", "Mansouri", "Alaoui", "Tazi", "Haddad", "Bennani", "Kabbaj", "Fassi", "Rami", "Ziani", "Moreau", "Smith", "Garcia", "Schmidt", "Rossi", "Dubois", "Johnson", "Costa", "Khan", "Nielsen"};
        String[] titles = {"Operations Manager", "Finance Director", "Head of Sales", "Procurement Lead", "IT Manager", "Customer Success Lead", "General Manager", "Marketing Director", "Business Analyst", "Regional Director"};
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Company company = companies.get(i % companies.size());
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[(i * 3) % lastNames.length];
            Contact contact = new Contact(company, firstName, lastName);
            contact.setJobTitle(titles[i % titles.length]);
            contact.setEmail("contact" + String.format(Locale.ROOT, "%03d", i + 1) + "@" + slug(company.getName()) + "." + DEMO_DOMAIN);
            contact.setPhone(phone(i + 40));
            contacts.add(contact);
        }
        return contactRepository.saveAll(contacts);
    }

    private List<Lead> createLeads(List<Company> companies, List<User> staff, User admin) {
        String[] firstNames = {"Amal", "Hamza", "Sanae", "Ilyas", "Nora", "Bilal", "Mina", "Ayoub", "Jihane", "Tarek", "Maya", "Samir", "Dina", "Nabil", "Aya", "Fouad"};
        String[] lastNames = {"Sefrioui", "Bouazza", "Idrissi", "El Fassi", "Rahmani", "Bouzid", "Cherkaoui", "Mernissi", "Ouali", "Sabri", "Belaid", "Rifai", "Zeroual", "Hamdi", "Toumi", "Lahlou"};
        LeadStatus[] statusPlan = statusPlan();
        LeadSource[] sources = LeadSource.values();
        List<Lead> leads = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            Company company = companies.get((i * 2) % companies.size());
            Lead lead = new Lead(firstNames[i % firstNames.length], lastNames[(i * 5) % lastNames.length], sources[i % sources.length], admin);
            lead.setEmail("lead" + String.format(Locale.ROOT, "%03d", i + 1) + "@" + slug(company.getName()) + "." + DEMO_DOMAIN);
            lead.setPhone(phone(i + 80));
            lead.setCompany(company);
            lead.setCompanyName(company.getName());
            lead.setStatus(statusPlan[i]);
            User assignee = staff.get(1 + (i % (staff.size() - 1)));
            lead.setAssignedTo(assignee);
            lead.setAssignedAt(OffsetDateTime.now().minusDays(75L - (i % 65)));
            leads.add(lead);
        }
        return leadRepository.saveAll(leads);
    }

    private void createLeadEvents(List<Lead> leads, List<User> staff) {
        List<LeadEvent> events = new ArrayList<>();
        for (int i = 0; i < leads.size(); i++) {
            Lead lead = leads.get(i);
            User user = staff.get(i % staff.size());
            events.add(new LeadEvent(lead, LeadEventType.CREATED, null, lead.getSource().name(), user));
            events.add(new LeadEvent(lead, LeadEventType.ASSIGNED, null, lead.getAssignedTo().getFullName(), lead.getAssignedTo()));
            if (lead.getStatus() != LeadStatus.NEW) {
                events.add(new LeadEvent(lead, LeadEventType.STATUS_CHANGED, LeadStatus.NEW.name(), lead.getStatus().name(), user));
            }
        }
        leadEventRepository.saveAll(events);
    }

    private List<Opportunity> createOpportunities(List<Company> companies, List<Contact> contacts, List<Lead> leads, List<User> staff) {
        OpportunityStage[] stagePlan = stagePlan();
        String[] names = {"CRM rollout", "Revenue operations upgrade", "Customer success workspace", "Sales analytics package", "Support automation", "Regional expansion platform", "Integration services", "Data migration", "Pipeline enablement", "Executive reporting"};
        List<Lead> convertedLeads = leads.stream().filter(lead -> lead.getStatus() == LeadStatus.CONVERTED).toList();
        List<Opportunity> opportunities = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Company company = companies.get((i * 3) % companies.size());
            Opportunity opportunity = new Opportunity(company, company.getName() + " - " + names[i % names.length], staff.get(1 + (i % (staff.size() - 1))));
            opportunity.setContact(contacts.get((i * 7) % contacts.size()));
            opportunity.setStage(stagePlan[i]);
            opportunity.setAmount(amountFor(i));
            opportunity.setCurrencyCode(currencyFor(i));
            opportunity.setExpectedCloseDate(LocalDate.now().plusDays(-35L + (i * 5L)));
            opportunity.setDescription("Fictional " + DEMO_MARKER + " opportunity for portfolio CRM analytics.");
            if (i < convertedLeads.size()) {
                opportunity.setLead(convertedLeads.get(i));
            }
            opportunities.add(opportunity);
        }
        return opportunityRepository.saveAll(opportunities);
    }

    private void createInteractions(List<Company> companies, List<Contact> contacts, List<Lead> leads, List<Opportunity> opportunities, List<User> staff) {
        String[] subjects = {"Discovery call", "Pricing discussion", "Product walkthrough", "Renewal planning", "Procurement follow-up", "Executive alignment", "Technical validation", "Proposal review", "Contract notes", "Quarterly check-in"};
        InteractionType[] types = {InteractionType.CALL, InteractionType.EMAIL, InteractionType.MEETING, InteractionType.NOTE};
        List<Interaction> interactions = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            OffsetDateTime occurredAt = OffsetDateTime.now().minusDays(i < 24 ? i % 7 : 7L + (i % 83)).minusHours(i % 10);
            Interaction interaction = new Interaction(staff.get(i % staff.size()), types[i % types.length], subjects[i % subjects.length], occurredAt);
            Company company = companies.get(i % companies.size());
            interaction.setCompany(company);
            interaction.setContact(contacts.get((i * 5) % contacts.size()));
            if (i % 3 == 0) {
                interaction.setLead(leads.get((i * 2) % leads.size()));
            }
            if (i % 2 == 0) {
                interaction.setOpportunity(opportunities.get((i * 3) % opportunities.size()));
            }
            interaction.setDescription("Fictional " + DEMO_MARKER + " activity: " + subjects[i % subjects.length] + " with " + company.getName() + ".");
            if (i % 5 == 0) {
                interaction.setFollowUpAt(occurredAt.plusDays(7));
            }
            interactions.add(interaction);
        }
        interactionRepository.saveAll(interactions);
    }

    private LeadStatus[] statusPlan() {
        List<LeadStatus> statuses = new ArrayList<>();
        add(statuses, LeadStatus.NEW, 18);
        add(statuses, LeadStatus.CONTACTED, 18);
        add(statuses, LeadStatus.QUALIFIED, 16);
        add(statuses, LeadStatus.UNQUALIFIED, 14);
        add(statuses, LeadStatus.CONVERTED, 14);
        return statuses.toArray(LeadStatus[]::new);
    }

    private OpportunityStage[] stagePlan() {
        List<OpportunityStage> stages = new ArrayList<>();
        add(stages, OpportunityStage.PROSPECTING, 10);
        add(stages, OpportunityStage.QUALIFICATION, 9);
        add(stages, OpportunityStage.PROPOSAL, 9);
        add(stages, OpportunityStage.NEGOTIATION, 8);
        add(stages, OpportunityStage.CLOSED_WON, 7);
        add(stages, OpportunityStage.CLOSED_LOST, 7);
        return stages.toArray(OpportunityStage[]::new);
    }

    private <T> void add(List<T> values, T value, int count) {
        for (int i = 0; i < count; i++) {
            values.add(value);
        }
    }

    private BigDecimal amountFor(int index) {
        int base = 18_000 + (index * 7_350 % 180_000);
        return BigDecimal.valueOf(base + ((index % 5) * 1_250L));
    }

    private String currencyFor(int index) {
        String[] currencies = {"MAD", "EUR", "USD", "MAD", "EUR"};
        return currencies[index % currencies.length];
    }

    private String phone(int index) {
        String[] prefixes = {"+212 6", "+212 7", "+33 6", "+1 514", "+44 20"};
        return prefixes[index % prefixes.length] + " " + String.format(Locale.ROOT, "%02d %02d %02d %02d", index % 100, (index * 3) % 100, (index * 7) % 100, (index * 11) % 100);
    }

    private String slug(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private boolean isDemoEmail(String email) {
        if (email == null) {
            return false;
        }
        String normalized = email.toLowerCase(Locale.ROOT);
        return normalized.endsWith("@" + DEMO_DOMAIN) || normalized.endsWith("." + DEMO_DOMAIN);
    }

    private boolean containsMarker(String value) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(DEMO_MARKER);
    }

    private record DemoCounts(long users, long companies, long contacts, long leads, long opportunities, long interactions) {
        boolean isComplete() {
            return users >= 4 && companies >= 30 && contacts >= 100 && leads >= 80 && opportunities >= 50 && interactions >= 150;
        }

        boolean isEmpty() {
            return users == 0 && companies == 0 && contacts == 0 && leads == 0 && opportunities == 0 && interactions == 0;
        }
    }
}
