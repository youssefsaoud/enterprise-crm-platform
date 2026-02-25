package com.example.crm.contacts;

import com.example.crm.companies.Company;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    boolean existsByCompanyAndDeletedAtIsNull(Company company);

    @EntityGraph(attributePaths = "company")
    Page<Contact> findByCompanyAndDeletedAtIsNull(Company company, Pageable pageable);

    @EntityGraph(attributePaths = "company")
    @Query("""
        select c from Contact c
        join c.company company
        where c.deletedAt is null
          and (:companyId is null or company.id = :companyId)
          and (
            :search is null
            or lower(c.firstName) like lower(concat('%', :search, '%'))
            or lower(c.lastName) like lower(concat('%', :search, '%'))
            or lower(c.email) like lower(concat('%', :search, '%'))
          )
        """,
        countQuery = """
        select count(c) from Contact c
        where c.deletedAt is null
          and (:companyId is null or c.company.id = :companyId)
          and (
            :search is null
            or lower(c.firstName) like lower(concat('%', :search, '%'))
            or lower(c.lastName) like lower(concat('%', :search, '%'))
            or lower(c.email) like lower(concat('%', :search, '%'))
          )
        """)
    Page<Contact> search(@Param("search") String search, @Param("companyId") UUID companyId, Pageable pageable);
}
