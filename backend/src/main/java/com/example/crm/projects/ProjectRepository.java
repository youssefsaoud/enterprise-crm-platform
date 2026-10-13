package com.example.crm.projects;

import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @EntityGraph(attributePaths = {"client", "assignedAssociate"})
    @Query(value = """
        select p from Project p
        where p.deletedAt is null
          and (:canSeeAll = true or p.assignedAssociate.id = :userId)
          and (:search is null or lower(p.name) like lower(concat('%', cast(:search as string), '%')) or lower(p.reference) like lower(concat('%', cast(:search as string), '%')))
          and (:status is null or p.status = :status)
          and (:clientId is null or p.client.id = :clientId)
          and (:assignedAssociateId is null or p.assignedAssociate.id = :assignedAssociateId)
        """, countQuery = """
        select count(p) from Project p
        where p.deletedAt is null
          and (:canSeeAll = true or p.assignedAssociate.id = :userId)
          and (:search is null or lower(p.name) like lower(concat('%', cast(:search as string), '%')) or lower(p.reference) like lower(concat('%', cast(:search as string), '%')))
          and (:status is null or p.status = :status)
          and (:clientId is null or p.client.id = :clientId)
          and (:assignedAssociateId is null or p.assignedAssociate.id = :assignedAssociateId)
        """)
    Page<Project> searchVisible(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll, @Param("search") String search, @Param("status") ProjectStatus status, @Param("clientId") UUID clientId, @Param("assignedAssociateId") UUID assignedAssociateId, Pageable pageable);
}