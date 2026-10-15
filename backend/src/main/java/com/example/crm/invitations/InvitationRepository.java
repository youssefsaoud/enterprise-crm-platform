package com.example.crm.invitations;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface InvitationRepository extends JpaRepository<EmployeeInvitation, UUID> {
    List<EmployeeInvitation> findAllByOrderByCreatedAtDesc();
    boolean existsByNormalizedEmailAndStatus(String email, String status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from EmployeeInvitation i where i.id = :id")
    Optional<EmployeeInvitation> lockById(@Param("id") UUID id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from EmployeeInvitation i where i.tokenHash = :hash")
    Optional<EmployeeInvitation> lockByHash(@Param("hash") String hash);
}
