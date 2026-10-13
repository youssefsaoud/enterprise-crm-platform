package com.example.crm.companies;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CompanyUpdateRequest(
    @NotBlank @Size(max = 180) String name,
    @Size(max = 120) String industry,
    @Size(max = 255) @Pattern(regexp = "^(https?://).+", message = "must start with http:// or https://") String website,
    @Size(max = 60) String phone,
    @Email @Size(max = 180) String email,
    @Size(max = 255) String address,
    Set<CompanyRole> roles
) {}