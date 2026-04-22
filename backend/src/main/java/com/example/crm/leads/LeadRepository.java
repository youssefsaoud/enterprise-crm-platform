package com.example.crm.leads;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    @EntityGraph(attributePaths = {"company", "assignedTo", "createdBy"})
    @Query("""
        select l from Lead l
        where l.deletedAt is null
          and (:canSeeAll = true or l.assignedTo.id = :userId or l.createdBy.id = :userId)
          and (:status is null or l.status = :status)
          and (:source is null or l.source = :source)
          and (:assigneeId is null or l.assignedTo.id = :assigneeId)
          and (:companyId is null or l.company.id = :companyId)
          and (
            :keyword is null
            or lower(l.firstName) like lower(concat('%', :keyword, '%'))
            or lower(l.lastName) like lower(concat('%', :keyword, '%'))
            or lower(l.email) like lower(concat('%', :keyword, '%'))
            or lower(l.companyName) like lower(concat('%', :keyword, '%'))
          )
        """,
        countQuery = """
        select count(l) from Lead l
        where l.deletedAt is null
          and (:canSeeAll = true or l.assignedTo.id = :userId or l.createdBy.id = :userId)
          and (:status is null or l.status = :status)
          and (:source is null or l.source = :source)
          and (:assigneeId is null or l.assignedTo.id = :assigneeId)
          and (:companyId is null or l.company.id = :companyId)
          and (
            :keyword is null
            or lower(l.firstName) like lower(concat('%', :keyword, '%'))
            or lower(l.lastName) like lower(concat('%', :keyword, '%'))
            or lower(l.email) like lower(concat('%', :keyword, '%'))
            or lower(l.companyName) like lower(concat('%', :keyword, '%'))
          )
        """)
    Page<Lead> searchVisible(
        @Param("userId") UUID userId,
        @Param("canSeeAll") boolean canSeeAll,
        @Param("status") LeadStatus status,
        @Param("source") LeadSource source,
        @Param("assigneeId") UUID assigneeId,
        @Param("companyId") UUID companyId,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
