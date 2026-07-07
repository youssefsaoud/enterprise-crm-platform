package com.example.crm.interactions;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
    @EntityGraph(attributePaths = {"author", "company", "contact", "lead", "opportunity"})
    @Query("""
        select i from Interaction i
        left join i.company company
        left join i.contact contact
        left join i.lead lead
        left join i.opportunity opportunity
        where i.deletedAt is null
          and (:canSeeAll = true or i.author.id = :userId
            or company.owner.id = :userId
            or lead.assignedTo.id = :userId or lead.createdBy.id = :userId
            or opportunity.owner.id = :userId)
          and (:type is null or i.type = :type)
          and (:creatorId is null or i.author.id = :creatorId)
          and (:companyId is null or company.id = :companyId)
          and (:contactId is null or contact.id = :contactId)
          and (:leadId is null or lead.id = :leadId)
          and (:opportunityId is null or opportunity.id = :opportunityId)
          and (:fromDate is null or i.occurredAt >= :fromDate)
          and (:toDate is null or i.occurredAt <= :toDate)
        """,
        countQuery = """
        select count(i) from Interaction i
        left join i.company company
        left join i.contact contact
        left join i.lead lead
        left join i.opportunity opportunity
        where i.deletedAt is null
          and (:canSeeAll = true or i.author.id = :userId
            or company.owner.id = :userId
            or lead.assignedTo.id = :userId or lead.createdBy.id = :userId
            or opportunity.owner.id = :userId)
          and (:type is null or i.type = :type)
          and (:creatorId is null or i.author.id = :creatorId)
          and (:companyId is null or company.id = :companyId)
          and (:contactId is null or contact.id = :contactId)
          and (:leadId is null or lead.id = :leadId)
          and (:opportunityId is null or opportunity.id = :opportunityId)
          and (:fromDate is null or i.occurredAt >= :fromDate)
          and (:toDate is null or i.occurredAt <= :toDate)
        """)
    Page<Interaction> searchVisible(
        @Param("userId") UUID userId,
        @Param("canSeeAll") boolean canSeeAll,
        @Param("type") InteractionType type,
        @Param("creatorId") UUID creatorId,
        @Param("companyId") UUID companyId,
        @Param("contactId") UUID contactId,
        @Param("leadId") UUID leadId,
        @Param("opportunityId") UUID opportunityId,
        @Param("fromDate") OffsetDateTime fromDate,
        @Param("toDate") OffsetDateTime toDate,
        Pageable pageable
    );
}
