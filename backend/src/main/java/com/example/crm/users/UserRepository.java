package com.example.crm.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByNormalizedEmail(String normalizedEmail);
    boolean existsByRole(Role role);
    List<User> findByRoleAndActiveTrueOrderByFullNameAsc(Role role);
}
