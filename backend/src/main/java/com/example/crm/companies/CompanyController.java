package com.example.crm.companies;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;
    public CompanyController(CompanyService companyService) { this.companyService = companyService; }
    @GetMapping Page<CompanyResponse> search(@RequestParam(required = false) String search, @RequestParam(required = false) String industry, @RequestParam(required = false) CompanyRole role, @org.springdoc.core.annotations.ParameterObject Pageable pageable) { return companyService.search(search, industry, role, pageable); }
    @GetMapping("/{id}") CompanyResponse get(@PathVariable UUID id) { return companyService.get(id); }
    @PostMapping ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyCreateRequest request) { CompanyResponse response = companyService.create(request); return ResponseEntity.created(URI.create("/api/v1/companies/" + response.id())).body(response); }
    @PutMapping("/{id}") CompanyResponse update(@PathVariable UUID id, @Valid @RequestBody CompanyUpdateRequest request) { return companyService.update(id, request); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { companyService.delete(id); return ResponseEntity.noContent().build(); }
}
