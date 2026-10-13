package com.example.crm.companies;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
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