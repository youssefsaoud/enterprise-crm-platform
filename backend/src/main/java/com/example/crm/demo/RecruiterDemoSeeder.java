package com.example.crm.demo;

import com.example.crm.companies.*;
import com.example.crm.contacts.*;
import com.example.crm.documents.*;
import com.example.crm.projects.*;
import com.example.crm.projectvendors.ProjectVendorRepository;
import com.example.crm.quotations.QuotationRepository;
import com.example.crm.interactions.InteractionRepository;
import com.example.crm.users.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("dev & docker-demo & !prod")
@ConditionalOnProperty(name = "crm.recruiter-demo.enabled", havingValue = "true")
public class RecruiterDemoSeeder {
    public static final String PUBLIC_PASSWORD = "OrqestraDemo2026!";

    @Bean CommandLineRunner recruiterData(UserRepository users, CompanyRepository companies, ContactRepository contacts,
        ProjectRepository projects, ProjectVendorRepository vendors, QuotationRepository quotations,
        InteractionRepository activities, ProjectDocumentRepository documents, DocumentStorage storage,
        PasswordEncoder passwords, DataSource source, TransactionTemplate transaction) {
        return args -> transaction.executeWithoutResult(status -> {
            // Serialize startup seeders sharing the same demo database without relying on row totals.
            var jdbc = new org.springframework.jdbc.core.JdbcTemplate(source);
            if (!"orqestra_demo".equals(jdbc.queryForObject("select current_database()", String.class))) throw new IllegalStateException("Unexpected demo database.");
            jdbc.execute("SELECT pg_advisory_xact_lock(73829145)");

            User admin = account(users, passwords, "Admin", "admin", Role.ADMIN, true);
            account(users, passwords, "Manager", "manager", Role.MANAGER, true);
            List<User> associates = List.of(account(users, passwords, "Associate", "associate", Role.SALES_REP, true),
                account(users, passwords, "Nadia Benali (display only)", "nadia", Role.SALES_REP, false),
                account(users, passwords, "Karim Idrissi (display only)", "karim", Role.SALES_REP, false));
            List<Company> vendorCompanies = new ArrayList<>();
            List<Company> clients = new ArrayList<>();
            String[][] rows = DemoDataSeeder.companyRows();
            for (int i = 0; i < rows.length; i++) {
                Company company = company(companies, admin, "vendor-" + i, rows[i][0], rows[i][1], rows[i][2],
                    i < 4 ? Set.of(CompanyRole.CLIENT, CompanyRole.VENDOR) : Set.of(CompanyRole.VENDOR));
                vendorCompanies.add(company);
                if (i < 4) clients.add(company);
            }
            String[] names = {"Northstar Hospitality Group", "Cedar Civic Services", "Meridian Manufacturing", "Harborview Medical Labs",
                "Aster Education Network", "Summit Logistics Partners", "Horizon Office Estates", "Lumen Research Facilities",
                "Greenway Industrial Works", "Cobalt Business Systems", "Beacon Retail Collective", "Juniper Property Services"};
            for (int i = 0; i < names.length; i++) clients.add(company(companies, admin, "client-" + i, names[i],
                "Business Operations", "Fictional demonstration office, Morocco", Set.of(CompanyRole.CLIENT)));
            List<Company> all = new ArrayList<>(vendorCompanies);
            all.addAll(clients.subList(4, clients.size()));
            for (Company company : all) {
                String email = "contact-" + company.getEmail();
                if (contacts.findAll().stream().noneMatch(c -> email.equals(c.getEmail()))) {
                    Contact contact = new Contact(company, "Demo", "Procurement Contact");
                    contact.setEmail(email); contact.setJobTitle("Procurement Lead"); contacts.save(contact);
                }
            }
            new ProjectDemoDataSeeder(projects, vendors, quotations, activities, companies, users, source)
                .seedBusinessData(clients, vendorCompanies, associates, true);
            for (int i = 0; i < 3; i++) {
                Project project = projects.findByReference(ProjectDemoDataSeeder.projectSeeds().get(i).reference()).orElseThrow();
                String filename = "Fictional-scope-" + project.getReference() + ".txt";
                if (documents.findByProjectIdOrderByCreatedAtDesc(project.getId()).stream().anyMatch(d -> filename.equals(d.getOriginalFilename()))) continue;
                byte[] text = ("Synthetic Orqestra demonstration document.\nProject: " + project.getName()
                    + "\nScope: " + project.getScopeOfWork() + "\nNot a real client agreement.\n").getBytes(StandardCharsets.UTF_8);
                try {
                    String key = storage.store(new ByteArrayInputStream(text), ".txt");
                    org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                        new org.springframework.transaction.support.TransactionSynchronization() {
                            @Override public void afterCompletion(int completion) {
                                if (completion == STATUS_ROLLED_BACK) {
                                    try { java.nio.file.Files.deleteIfExists(storage.resolve(key)); }
                                    catch (java.io.IOException ignored) { /* A failed seed can retry without exposing file paths. */ }
                                }
                            }
                        });
                    documents.save(new ProjectDocument(project, null, filename, key, "text/plain", text.length, admin));
                } catch (java.io.IOException error) { throw new IllegalStateException("Cannot create synthetic sample document", error); }
            }
        });
    }

    static User account(UserRepository users, PasswordEncoder passwords, String name, String local, Role role, boolean active) {
        String email = local + "@orqestra.demo";
        return users.findByNormalizedEmail(email).orElseGet(() -> users.save(new User(name, email,
            passwords.encode(active ? PUBLIC_PASSWORD : UUID.randomUUID().toString()), role, active)));
    }

    private static Company company(CompanyRepository companies, User owner, String key, String name, String industry, String address, Set<CompanyRole> roles) {
        String email = key + "@orqestra.demo";
        return companies.findAll().stream().filter(c -> email.equals(c.getEmail())).findFirst().orElseGet(() -> {
            Company company = new Company(name, owner);
            company.setEmail(email); company.setIndustry(industry); company.setAddress(address); company.setRoles(roles);
            return companies.save(company);
        });
    }
}
