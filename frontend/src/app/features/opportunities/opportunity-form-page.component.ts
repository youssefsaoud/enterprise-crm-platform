import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { OpportunityPayload } from './opportunity.models';
import { OpportunityService } from './opportunity.service';

@Component({ selector: 'app-opportunity-form-page', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './opportunity-form-page.component.html', styleUrls: ['./opportunity-form-page.component.css'] })
export class OpportunityFormPageComponent implements OnInit {
  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(160)]],
    companyId: ['', Validators.required],
    contactId: [''],
    ownerUserId: [''],
    amount: [0, [Validators.required, Validators.min(0)]],
    currencyCode: ['USD', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]],
    expectedCloseDate: [''],
    description: ['', Validators.maxLength(4000)]
  });
  opportunityId: string | null = null;
  loading = false;
  saving = false;
  error = '';

  constructor(private readonly fb: FormBuilder, private readonly route: ActivatedRoute, private readonly router: Router, private readonly opportunityService: OpportunityService) {}

  ngOnInit(): void {
    this.opportunityId = this.route.snapshot.paramMap.get('id');
    if (!this.opportunityId) { return; }
    this.loading = true;
    this.opportunityService.get(this.opportunityId).subscribe({
      next: (opportunity) => {
        this.form.patchValue({ name: opportunity.name, companyId: opportunity.companyId, contactId: opportunity.contactId ?? '', ownerUserId: opportunity.ownerUserId ?? '', amount: opportunity.amount, currencyCode: opportunity.currencyCode, expectedCloseDate: opportunity.expectedCloseDate ?? '', description: opportunity.description ?? '' });
        this.loading = false;
      },
      error: (error) => { this.error = apiErrorMessage(error, 'Opportunity not found.'); this.loading = false; }
    });
  }

  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    this.error = '';
    const payload = this.form.getRawValue() as OpportunityPayload;
    const request$ = this.opportunityId ? this.opportunityService.update(this.opportunityId, payload) : this.opportunityService.create(payload);
    request$.subscribe({ next: (opportunity) => this.router.navigate(['/opportunities', opportunity.id]), error: (error) => { this.error = apiErrorMessage(error, 'Opportunity could not be saved.'); this.saving = false; } });
  }
}
