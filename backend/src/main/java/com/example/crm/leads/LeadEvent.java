package com.example.crm.leads;

import com.example.crm.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead_events")
public class LeadEvent {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadEventType eventType;

    @Column(length = 255)
    private String oldValue;

    @Column(length = 255)
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by_user_id")
    private User performedBy;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected LeadEvent() {}

    public LeadEvent(Lead lead, LeadEventType eventType, String oldValue, String newValue, User performedBy) {
        this.lead = lead;
        this.eventType = eventType;
