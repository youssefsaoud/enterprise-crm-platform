package com.example.crm.leads;

import com.example.crm.users.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    List<Lead> findByAssignedTo(User assignedTo);
    List<Lead> findByStatus(LeadStatus status);
}
