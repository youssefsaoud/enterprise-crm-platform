package com.example.crm.opportunities;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.companies.Company;
import com.example.crm.leads.Lead;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
public class Opportunity extends AuditSupport {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "company_id") private Company company;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id") private User owner;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lead_id") private Lead lead;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private OpportunityStage stage = OpportunityStage.PROSPECTING;
    @Column(nullable = false, length = 180) private String name;
    @Column(nullable = false) private BigDecimal estimatedValue = BigDecimal.ZERO;
    private LocalDate expectedCloseDate;
    private OffsetDateTime deletedAt;
    protected Opportunity() {}
    public Opportunity(Company company, String name, User owner) { this.company = company; this.name = name; this.owner = owner; }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public OpportunityStage getStage() { return stage; }
}
