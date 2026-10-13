import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Company } from '../companies/company.models';
import { CompanyService } from '../companies/company.service';
import { apiErrorMessage } from '../../core/api/api-error';
import { ContactPayload } from './contact.models';
import { ContactService } from './contact.service';

@Component({
  selector: 'app-contact-form-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './contact-form-page.component.html',
  styleUrls: ['./contact-form-page.component.css']
})
export class ContactFormPageComponent implements OnInit {
  readonly form = this.fb.nonNullable.group({
    companyId: ['', Validators.required],
    firstName: ['', [Validators.required, Validators.maxLength(80)]],
    lastName: ['', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.email, Validators.maxLength(180)]],
    phone: ['', [Validators.maxLength(60)]],
    jobTitle: ['', [Validators.maxLength(120)]]
  });

  companies: Company[] = [];
  contactId: string | null = null;
  loading = false;
  saving = false;
  error = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly companyService: CompanyService,
    private readonly contactService: ContactService
  ) {}

  ngOnInit(): void {
    this.contactId = this.route.snapshot.paramMap.get('id');
    const companyId = this.route.snapshot.queryParamMap.get('companyId');
    if (companyId) {
      this.form.patchValue({ companyId });
    }
    this.loadCompanies();
    if (this.contactId) {
      this.loadContact(this.contactId);
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.error = '';
    const payload = this.form.getRawValue() as ContactPayload;
    const request$ = this.contactId
      ? this.contactService.update(this.contactId, payload)
      : this.contactService.create(payload);

    request$.subscribe({
      next: (contact) => this.router.navigate(['/contacts', contact.id]),
      error: (error) => {
        this.error = apiErrorMessage(error, 'Contact could not be saved.');
        this.saving = false;
      }
    });
  }

  private loadCompanies(): void {
    this.companyService.search({ size: 100 }).subscribe({
      next: (result) => this.companies = result.content,
      error: () => this.error = 'Companies could not be loaded.'
    });
  }

  private loadContact(id: string): void {
    this.loading = true;
    this.contactService.get(id).subscribe({
      next: (contact) => {
        this.form.patchValue({
          companyId: contact.companyId,
          firstName: contact.firstName,
          lastName: contact.lastName,
          email: contact.email ?? '',
          phone: contact.phone ?? '',
          jobTitle: contact.jobTitle ?? ''
        });
        this.loading = false;
      },
      error: () => {
        this.error = 'Contact not found.';
        this.loading = false;
      }
    });
  }
}
