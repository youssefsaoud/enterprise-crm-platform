package com.example.crm.companies;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company extends AuditSupport {
    @Id private UUID id;
    @Column(nullable = false, length = 180) private String name;
    private String industry;
    private String website;
    private String phone;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id") private User owner;
    private OffsetDateTime deletedAt;
    protected Company() {}
    public Company(String name, User owner) { this.name = name; this.owner = owner; }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public User getOwner() { return owner; }
}
