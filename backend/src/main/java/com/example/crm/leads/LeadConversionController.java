package com.example.crm.leads;

import com.example.crm.security.CrmUserPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadConversionController {
    private final LeadConversionService leadConversionService;

    public LeadConversionController(LeadConversionService leadConversionService) {
        this.leadConversionService = leadConversionService;
    }

    @PostMapping("/{id}/convert")
    LeadConversionResponse convert(@PathVariable UUID id, @Valid @RequestBody LeadConversionRequest request, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadConversionService.convert(id, request, principal);
    }

    @GetMapping("/{id}/conversion")
    LeadConversionResponse get(@PathVariable UUID id, @AuthenticationPrincipal CrmUserPrincipal principal) {
        return leadConversionService.get(id, principal);
    }
}
