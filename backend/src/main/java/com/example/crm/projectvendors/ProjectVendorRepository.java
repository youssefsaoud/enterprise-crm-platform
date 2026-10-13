package com.example.crm.projectvendors;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectVendorRepository extends JpaRepository<ProjectVendor, UUID> {
    @EntityGraph(attributePaths = {"project", "vendor", "responsibleAssociate"})
    @Query("""
        select pv from ProjectVendor pv
        where pv.deletedAt is null
          and pv.project.id = :projectId
          and (:status is null or pv.status = :status)
          and (:search is null or lower(pv.vendor.name) like lower(concat('%', cast(:search as string), '%')))
        order by pv.vendor.name asc
        """)
    Page<ProjectVendor> searchByProject(@Param("projectId") UUID projectId, @Param("status") OutreachStatus status, @Param("search") String search, Pageable pageable);

    @EntityGraph(attributePaths = {"project", "vendor", "responsibleAssociate"})
    @Query("select pv from ProjectVendor pv where pv.id = :id and pv.deletedAt is null")
    Optional<ProjectVendor> findActive(@Param("id") UUID id);

    boolean existsByProjectIdAndVendorIdAndDeletedAtIsNull(UUID projectId, UUID vendorId);
    Optional<ProjectVendor> findByProjectIdAndVendorIdAndDeletedAtIsNull(UUID projectId, UUID vendorId);

    @EntityGraph(attributePaths = {"project", "vendor", "responsibleAssociate"})
    @Query("""
        select pv from ProjectVendor pv
        where pv.deletedAt is null
          and pv.nextFollowUpDate is not null
          and pv.nextFollowUpDate <= :throughDate
          and (:canSeeAll = true or pv.project.assignedAssociate.id = :userId)
        order by pv.nextFollowUpDate asc, pv.vendor.name asc
        """)
    List<ProjectVendor> findFollowUps(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll, @Param("throughDate") LocalDate throughDate);

    @Query("select count(pv) from ProjectVendor pv where pv.deletedAt is null and (:canSeeAll = true or pv.project.assignedAssociate.id = :userId)")
    long countVisible(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll);

    @Query("select count(pv) from ProjectVendor pv where pv.deletedAt is null and (:canSeeAll = true or pv.project.assignedAssociate.id = :userId) and pv.status in :statuses")
    long countVisibleByStatusIn(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll, @Param("statuses") java.util.Set<OutreachStatus> statuses);
}
