package com.example.crm.interactions;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
}
