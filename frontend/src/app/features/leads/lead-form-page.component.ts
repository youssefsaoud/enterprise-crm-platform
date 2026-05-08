import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { LeadPayload, LeadSource } from './lead.models';
import { LeadService } from './lead.service';

@Component({
  selector: 'app-lead-form-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './lead-form-page.component.html',
  styleUrls: ['./lead-form-page.component.css']
})
export class LeadFormPageComponent implements OnInit {
  readonly sources: LeadSource[] = ['WEBSITE', 'REFERRAL', 'EMAIL', 'EVENT', 'OTHER'];
  readonly form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(80)]],
    lastName: ['', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.email, Validators.maxLength(180)]],
    phone: ['', [Validators.maxLength(60)]],
    companyName: ['', [Validators.maxLength(180)]],
    companyId: ['', [Validators.maxLength(36)]],
    source: ['WEBSITE' as LeadSource, Validators.required]
  });

  leadId: string | null = null;
  loading = false;
  saving = false;
  error = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly leadService: LeadService
  ) {}

  ngOnInit(): void {
    this.leadId = this.route.snapshot.paramMap.get('id');
    if (!this.leadId) {
      return;
    }
    this.loading = true;
    this.leadService.get(this.leadId).subscribe({
      next: (lead) => {
        this.form.patchValue({
          firstName: lead.firstName,
          lastName: lead.lastName,
          email: lead.email ?? '',
          phone: lead.phone ?? '',
          companyName: lead.companyName ?? '',
          companyId: lead.companyId ?? '',
          source: lead.source
        });
        this.loading = false;
      },
      error: (error) => {
        this.error = apiErrorMessage(error, 'Lead not found.');
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
    const payload = this.form.getRawValue() as LeadPayload;
    const request$ = this.leadId ? this.leadService.update(this.leadId, payload) : this.leadService.create(payload);
    request$.subscribe({
      next: (lead) => this.router.navigate(['/leads', lead.id]),
      error: (error) => {
        this.error = apiErrorMessage(error, 'Lead could not be saved.');
        this.saving = false;
      }
    });
  }
}
