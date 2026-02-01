package com.example.crm.interactions;

import com.example.crm.companies.Company;
import com.example.crm.contacts.Contact;
import com.example.crm.leads.Lead;
import com.example.crm.opportunities.Opportunity;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interactions")
public class Interaction {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "author_id") private User author;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "company_id") private Company company;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "contact_id") private Contact contact;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lead_id") private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "opportunity_id") private Opportunity opportunity;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private InteractionType type;
    @Column(nullable = false, length = 180) private String subject;
    @Column(columnDefinition = "text") private String body;
    @Column(nullable = false) private OffsetDateTime occurredAt;
    @Column(nullable = false, updatable = false) private OffsetDateTime createdAt;
    protected Interaction() {}
    @PrePersist void onCreate() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = OffsetDateTime.now(); }
}
