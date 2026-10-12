package com.example.crm.companies;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    @Query("""
        select c from Company c
        where c.deletedAt is null
          and (:search is null or lower(c.name) like lower(concat('%', cast(:search as string), '%')))
          and (:industry is null or lower(c.industry) = lower(cast(:industry as string)))
        """)
    Page<Company> search(@Param("search") String search, @Param("industry") String industry, Pageable pageable);
}
