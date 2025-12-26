package com.example.crm.contacts;

import com.example.crm.companies.Company;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByCompany(Company company);
}
