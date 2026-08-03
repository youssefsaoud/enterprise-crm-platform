import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { Lead } from './lead.models';
import { LeadConversionResult, LeadConversionService } from './lead-conversion.service';

@Component({
  selector: 'app-lead-conversion-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './lead-conversion-panel.component.html',
  styleUrls: ['./lead-conversion-panel.component.css']
})
export class LeadConversionPanelComponent implements OnChanges {
  @Input({ required: true }) lead!: Lead;
  @Output() converted = new EventEmitter<LeadConversionResult>();

  readonly form = this.fb.nonNullable.group({
    existingCompanyId: [''],
    companyName: ['', [Validators.maxLength(180)]],
    existingContactId: [''],
    contactFirstName: ['', Validators.maxLength(80)],
    contactLastName: ['', Validators.maxLength(80)],
    contactEmail: ['', [Validators.email, Validators.maxLength(180)]],
    createOpportunity: [false],
    opportunityName: ['', Validators.maxLength(160)],
    opportunityAmount: [0, Validators.min(0)],
    opportunityCurrency: ['USD', Validators.pattern(/^[A-Z]{3}$/)],
    opportunityCloseDate: [''],
    notes: ['', Validators.maxLength(4000)]
  });

  result: LeadConversionResult | null = null;
  saving = false;
  error = '';

  constructor(private readonly fb: FormBuilder, private readonly conversionService: LeadConversionService) {}

  ngOnChanges(): void {
    if (!this.lead) {
      return;
    }
    this.form.patchValue({
      companyName: this.lead.companyName || '',
      contactFirstName: this.lead.firstName,
      contactLastName: this.lead.lastName,
      contactEmail: this.lead.email || '',
      opportunityName: `${this.lead.firstName} ${this.lead.lastName} opportunity`
    }, { emitEvent: false });
    this.conversionService.get(this.lead.id).subscribe({ next: (result) => this.result = result, error: () => this.result = null });
  }

  convert(): void {
    if (!this.lead || this.lead.status !== 'QUALIFIED' || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.saving = true;
    this.error = '';
    this.conversionService.convert(this.lead.id, {
      existingCompanyId: this.optional(value.existingCompanyId),
      newCompany: this.optional(value.existingCompanyId) ? null : { name: value.companyName || this.lead.companyName || `${this.lead.firstName} ${this.lead.lastName}` },
      existingContactId: this.optional(value.existingContactId),
      newContact: this.optional(value.existingContactId) ? null : { firstName: value.contactFirstName || this.lead.firstName, lastName: value.contactLastName || this.lead.lastName, email: this.optional(value.contactEmail), phone: this.lead.phone ?? null },
      opportunity: value.createOpportunity ? { name: value.opportunityName, amount: value.opportunityAmount, currencyCode: value.opportunityCurrency, expectedCloseDate: this.optional(value.opportunityCloseDate), stage: 'PROSPECTING' } : null,
      notes: this.optional(value.notes)
    }).subscribe({
      next: (result) => { this.result = result; this.saving = false; this.converted.emit(result); },
      error: (error) => { this.error = apiErrorMessage(error, 'Lead could not be converted.'); this.saving = false; }
    });
  }

  private optional(value: string | null | undefined): string | null {
    return value?.trim() ? value.trim() : null;
  }
}
