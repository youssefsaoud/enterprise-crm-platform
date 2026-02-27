package com.example.crm.contacts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ContactCreateRequest(
    @NotNull UUID companyId,
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @Email @Size(max = 180) String email,
    @Size(max = 60) String phone,
    @Size(max = 120) String jobTitle
) {}
