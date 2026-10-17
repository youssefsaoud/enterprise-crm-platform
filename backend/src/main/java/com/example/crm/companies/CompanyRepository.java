package com.example.crm.companies;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    interface RelationshipCount { UUID getCompanyId(); long getCount(); }

    @Query("""
        select p.client.id as companyId, count(distinct p.id) as count from Project p
        where p.deletedAt is null and p.client.deletedAt is null and p.client.id in :ids
          and (:all = true or p.assignedAssociate.id = :userId)
        group by p.client.id
        """)
    java.util.List<RelationshipCount> clientRelationships(java.util.List<UUID> ids, UUID userId, boolean all);

    @Query("""
        select pv.vendor.id as companyId, count(distinct pv.project.id) as count from ProjectVendor pv
        where pv.deletedAt is null and pv.project.deletedAt is null and pv.vendor.deletedAt is null and pv.vendor.id in :ids
          and (:all = true or pv.project.assignedAssociate.id = :userId)
        group by pv.vendor.id
        """)
    java.util.List<RelationshipCount> vendorRelationships(java.util.List<UUID> ids, UUID userId, boolean all);

    @Query("""
        select distinct c from Company c
        left join c.roles companyRole
        where c.deletedAt is null
          and (:search is null or lower(c.name) like lower(concat('%', cast(:search as string), '%')))
          and (:industry is null or lower(c.industry) = lower(cast(:industry as string)))
          and (:role is null or companyRole = :role)
        """)
    Page<Company> search(@Param("search") String search, @Param("industry") String industry, @Param("role") CompanyRole role, Pageable pageable);
}
