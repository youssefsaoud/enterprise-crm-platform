import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { apiErrorMessage } from '../../core/api/api-error';
import { CompanyPayload, CompanyRole } from './company.models';
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
    address: ['', [Validators.maxLength(255)]],
    roles: this.fb.control<CompanyRole[]>(['CLIENT'], { nonNullable: true })
  });

  companyId: string | null = null;
  requestedRole: CompanyRole | null = null;
  loading = false;
  saving = false;
  error = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly companyService: CompanyService
  ) {}

  get pageTitle(): string {
    if (this.companyId) return 'Edit organization';
    if (this.requestedRole === 'CLIENT') return 'New client';
    if (this.requestedRole === 'VENDOR') return 'New vendor';
    return 'New organization';
  }

  get pageDescription(): string {
    if (this.companyId) return 'Update organization details and business roles.';
    if (this.requestedRole === 'CLIENT') return 'Add a client organization to the shared directory.';
    if (this.requestedRole === 'VENDOR') return 'Add a vendor organization to the shared directory.';
    return 'Add an organization and choose how it works with your projects.';
  }

  ngOnInit(): void {
    this.companyId = this.route.snapshot.paramMap.get('id');
    const requested = this.route.snapshot.queryParamMap.get('role');
    this.requestedRole = requested === 'CLIENT' || requested === 'VENDOR' ? requested : null;

    if (!this.companyId && this.requestedRole) {
      this.form.controls.roles.setValue([this.requestedRole]);
    }

    if (!this.companyId) return;

    this.loading = true;
    this.companyService.get(this.companyId).subscribe({
      next: company => {
        this.form.patchValue({
          name: company.name,
          industry: company.industry ?? '',
          website: company.website ?? '',
          phone: company.phone ?? '',
          email: company.email ?? '',
          address: company.address ?? '',
          roles: company.roles ?? ['CLIENT']
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Company not found.';
        this.loading = false;
      }
    });
  }

  toggleRole(role: CompanyRole, checked: boolean): void {
    const roles = this.form.controls.roles.value.filter(item => item !== role);
    if (checked) roles.push(role);
    this.form.controls.roles.setValue(roles);
  }

  hasRole(role: CompanyRole): boolean {
    return this.form.controls.roles.value.includes(role);
  }

  save(): void {
    if (this.form.invalid || this.form.controls.roles.value.length === 0) {
      this.form.markAllAsTouched();
      this.error = this.form.controls.roles.value.length === 0
        ? 'Select at least one organization role.'
        : '';
      return;
    }

    this.saving = true;
    this.error = '';
    const payload = this.form.getRawValue() as CompanyPayload;
    const request$ = this.companyId
      ? this.companyService.update(this.companyId, payload)
      : this.companyService.create(payload);

    request$.subscribe({
      next: company => this.router.navigate(['/companies', company.id]),
      error: error => {
        this.error = apiErrorMessage(error, 'Company could not be saved.');
        this.saving = false;
      }
    });
  }
}
