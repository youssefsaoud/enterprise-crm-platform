package com.example.crm.quotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    @EntityGraph(attributePaths = {"projectVendor", "projectVendor.project", "projectVendor.vendor"})
    List<Quotation> findByProjectVendorIdOrderByRevisionNumberDesc(UUID projectVendorId);

    @EntityGraph(attributePaths = {"projectVendor", "projectVendor.project", "projectVendor.vendor"})
    Optional<Quotation> findWithDetailsById(UUID id);

    @Query("select coalesce(max(q.revisionNumber), 0) from Quotation q where q.projectVendor.id = :projectVendorId")
    int maxRevision(@Param("projectVendorId") UUID projectVendorId);

    boolean existsByProjectVendorProjectIdAndSelectedTrue(UUID projectId);

    @Modifying
    @Query("update Quotation q set q.selected = false, q.status = case when q.status = com.example.crm.quotations.QuotationStatus.SELECTED then com.example.crm.quotations.QuotationStatus.UNDER_REVIEW else q.status end where q.projectVendor.project.id = :projectId")
    int clearSelectionForProject(@Param("projectId") UUID projectId);

    @Query("select count(q) from Quotation q where q.projectVendor.deletedAt is null and (:canSeeAll = true or q.projectVendor.project.assignedAssociate.id = :userId) and q.status in :statuses")
    long countVisibleByStatusIn(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll, @Param("statuses") java.util.Set<QuotationStatus> statuses);

    @EntityGraph(attributePaths = {"projectVendor", "projectVendor.project", "projectVendor.vendor"})
    @Query("select q from Quotation q where q.projectVendor.deletedAt is null and (:canSeeAll = true or q.projectVendor.project.assignedAssociate.id = :userId)")
    java.util.List<Quotation> findVisible(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll);
}
