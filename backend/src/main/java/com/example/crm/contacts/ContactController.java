package com.example.crm.contacts;

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

@RestController
@RequestMapping("/api/v1")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    Page<ContactResponse> search(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) UUID companyId,
        Pageable pageable
    ) {
        return contactService.search(search, companyId, pageable);
    }

    @GetMapping("/contacts/{id}")
    ContactResponse get(@PathVariable UUID id) {
        return contactService.get(id);
    }

    @GetMapping("/companies/{companyId}/contacts")
    Page<ContactResponse> forCompany(@PathVariable UUID companyId, Pageable pageable) {
        return contactService.forCompany(companyId, pageable);
    }

    @PostMapping("/contacts")
    ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactCreateRequest request) {
        ContactResponse response = contactService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/contacts/" + response.id())).body(response);
    }

    @PutMapping("/contacts/{id}")
    ContactResponse update(@PathVariable UUID id, @Valid @RequestBody ContactUpdateRequest request) {
        return contactService.update(id, request);
    }

    @DeleteMapping("/contacts/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
