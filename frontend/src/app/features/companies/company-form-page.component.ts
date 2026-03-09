import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { CompanyPayload } from './company.models';
import { CompanyService } from './company.service';

@Component({
  selector: 'app-company-form-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './company-form-page.component.html',
  styleUrls: ['./company-form-page.component.css']
})
export class CompanyFormPageComponent implements OnInit {
  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(180)]],
    industry: ['', [Validators.maxLength(120)]],
    website: ['', [Validators.maxLength(255)]],
    phone: ['', [Validators.maxLength(60)]],
    email: ['', [Validators.email, Validators.maxLength(180)]],
    address: ['', [Validators.maxLength(255)]]
  });

  companyId: string | null = null;
  loading = false;
  saving = false;
  error = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly companyService: CompanyService
  ) {}

  ngOnInit(): void {
    this.companyId = this.route.snapshot.paramMap.get('id');
    if (!this.companyId) {
      return;
    }
    this.loading = true;
    this.companyService.get(this.companyId).subscribe({
      next: (company) => {
        this.form.patchValue({
          name: company.name,
          industry: company.industry ?? '',
          website: company.website ?? '',
          phone: company.phone ?? '',
          email: company.email ?? '',
          address: company.address ?? ''
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Company not found.';
        this.loading = false;
      }
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.error = '';
    const payload = this.form.getRawValue() as CompanyPayload;
    const request$ = this.companyId
      ? this.companyService.update(this.companyId, payload)
      : this.companyService.create(payload);

    request$.subscribe({
      next: (company) => this.router.navigate(['/companies', company.id]),
      error: (error) => {
        this.error = apiErrorMessage(error, 'Company could not be saved.');
        this.saving = false;
      }
    });
  }
}

