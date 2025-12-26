package com.example.crm.contacts;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
public class Contact extends AuditSupport {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "company_id") private Company company;
    @Column(nullable = false, length = 80) private String firstName;
    @Column(nullable = false, length = 80) private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private OffsetDateTime deletedAt;
    protected Contact() {}
    public Contact(Company company, String firstName, String lastName) { this.company = company; this.firstName = firstName; this.lastName = lastName; }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public Company getCompany() { return company; }
}
