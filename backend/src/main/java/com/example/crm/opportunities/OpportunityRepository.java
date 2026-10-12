package com.example.crm.opportunities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    @EntityGraph(attributePaths = {"company", "contact", "owner"})
    @Query(value = """
        select o from Opportunity o
        where o.deletedAt is null
          and (:canSeeAll = true or o.owner.id = :userId)
          and (:search is null or lower(o.name) like lower(concat('%', cast(:search as string), '%')))
          and (:stage is null or o.stage = :stage)
          and (:ownerId is null or o.owner.id = :ownerId)
          and (:companyId is null or o.company.id = :companyId)
          and (:closeDateFrom is null or o.expectedCloseDate >= :closeDateFrom)
          and (:closeDateTo is null or o.expectedCloseDate <= :closeDateTo)
        """,
        countQuery = """
        select count(o) from Opportunity o
        where o.deletedAt is null
          and (:canSeeAll = true or o.owner.id = :userId)
          and (:search is null or lower(o.name) like lower(concat('%', cast(:search as string), '%')))
          and (:stage is null or o.stage = :stage)
          and (:ownerId is null or o.owner.id = :ownerId)
          and (:companyId is null or o.company.id = :companyId)
          and (:closeDateFrom is null or o.expectedCloseDate >= :closeDateFrom)
          and (:closeDateTo is null or o.expectedCloseDate <= :closeDateTo)
        """)
    Page<Opportunity> searchVisible(
        @Param("userId") UUID userId,
        @Param("canSeeAll") boolean canSeeAll,
        @Param("search") String search,
        @Param("stage") OpportunityStage stage,
        @Param("ownerId") UUID ownerId,
        @Param("companyId") UUID companyId,
        @Param("closeDateFrom") LocalDate closeDateFrom,
        @Param("closeDateTo") LocalDate closeDateTo,
        Pageable pageable
    );

    @Query(value = """
        select o.stage as stage, o.currencyCode as currencyCode, count(o) as count, coalesce(sum(o.amount), 0) as totalAmount
        from Opportunity o
        where o.deletedAt is null
          and (:canSeeAll = true or o.owner.id = :userId)
        group by o.stage, o.currencyCode
        order by o.stage, o.currencyCode
        """)
    List<OpportunityPipelineRow> pipelineSummary(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll);
}
