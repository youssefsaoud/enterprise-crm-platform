package com.example.crm.interactions;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
    boolean existsByProjectIdAndSubjectAndDeletedAtIsNull(UUID projectId, String subject);

    @EntityGraph(attributePaths = {"author", "company", "contact", "lead", "opportunity", "project", "projectVendor", "quotation"})
    @Query(value = """
        select i from Interaction i
        left join i.company company left join i.contact contact left join i.lead lead left join i.opportunity opportunity
        left join i.project project left join i.projectVendor projectVendor left join i.quotation quotation
        where i.deletedAt is null
          and (:canSeeAll = true or i.author.id = :userId or company.owner.id = :userId
            or lead.assignedTo.id = :userId or lead.createdBy.id = :userId or opportunity.owner.id = :userId
            or project.assignedAssociate.id = :userId or projectVendor.project.assignedAssociate.id = :userId)
          and (:type is null or i.type = :type)
          and (:creatorId is null or i.author.id = :creatorId)
          and (:companyId is null or company.id = :companyId)
          and (:contactId is null or contact.id = :contactId)
          and (:leadId is null or lead.id = :leadId)
          and (:opportunityId is null or opportunity.id = :opportunityId)
          and (:projectId is null or project.id = :projectId)
          and (:projectVendorId is null or projectVendor.id = :projectVendorId)
          and i.occurredAt >= coalesce(:fromDate, i.occurredAt)
          and i.occurredAt <= coalesce(:toDate, i.occurredAt)
        """,
        countQuery = """
        select count(i) from Interaction i
        left join i.company company left join i.contact contact left join i.lead lead left join i.opportunity opportunity
        left join i.project project left join i.projectVendor projectVendor
        where i.deletedAt is null
          and (:canSeeAll = true or i.author.id = :userId or company.owner.id = :userId
            or lead.assignedTo.id = :userId or lead.createdBy.id = :userId
            or opportunity.owner.id = :userId or project.assignedAssociate.id = :userId
            or projectVendor.project.assignedAssociate.id = :userId)
          and (:type is null or i.type = :type)
          and (:creatorId is null or i.author.id = :creatorId)
          and (:companyId is null or company.id = :companyId)
          and (:contactId is null or contact.id = :contactId)
          and (:leadId is null or lead.id = :leadId)
          and (:opportunityId is null or opportunity.id = :opportunityId)
          and (:projectId is null or project.id = :projectId)
          and (:projectVendorId is null or projectVendor.id = :projectVendorId)
          and i.occurredAt >= coalesce(:fromDate, i.occurredAt)
          and i.occurredAt <= coalesce(:toDate, i.occurredAt)
        """)
    Page<Interaction> searchVisible(@Param("userId") UUID userId, @Param("canSeeAll") boolean canSeeAll, @Param("type") InteractionType type, @Param("creatorId") UUID creatorId, @Param("companyId") UUID companyId, @Param("contactId") UUID contactId, @Param("leadId") UUID leadId, @Param("opportunityId") UUID opportunityId, @Param("projectId") UUID projectId, @Param("projectVendorId") UUID projectVendorId, @Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate, Pageable pageable);
}
