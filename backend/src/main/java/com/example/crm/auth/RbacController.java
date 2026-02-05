package com.example.crm.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rbac")
public class RbacController {
    @GetMapping("/admin") @PreAuthorize("hasRole('ADMIN')") public String admin() { return "admin"; }
    @GetMapping("/manager") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public String manager() { return "manager"; }
    @GetMapping("/sales") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_REP')") public String sales() { return "sales"; }
}
