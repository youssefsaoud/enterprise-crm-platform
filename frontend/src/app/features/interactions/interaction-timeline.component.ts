import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { apiErrorMessage } from '../../core/api/api-error';
import { Interaction, InteractionType } from './interaction.models';
import { InteractionService } from './interaction.service';

@Component({
  selector: 'app-interaction-timeline',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './interaction-timeline.component.html',
  styleUrls: ['./interaction-timeline.component.css']
})
export class InteractionTimelineComponent implements OnChanges {
  @Input({ required: true }) parent!: 'companies' | 'contacts' | 'leads' | 'opportunities' | 'projects' | 'project-vendors';
  @Input({ required: true }) recordId!: string;

  readonly types: InteractionType[] = ['CALL', 'EMAIL', 'MEETING', 'NOTE', 'FOLLOW_UP'];
  readonly form = this.fb.nonNullable.group({
    type: ['NOTE' as InteractionType, Validators.required],
    subject: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(160)]],
    description: ['', Validators.maxLength(4000)],
    occurredAt: [this.localNow(), Validators.required],
    followUpAt: ['']
  });

  interactions: Interaction[] = [];
  loading = false;
  saving = false;
  error = '';

  constructor(private readonly fb: FormBuilder, private readonly interactionService: InteractionService) {}

  ngOnChanges(): void { if (this.recordId) this.load(); }

  load(): void {
    this.loading = true;
    this.error = '';
    this.interactionService.timeline(this.parent, this.recordId).subscribe({
      next: result => { this.interactions = result.content; this.loading = false; },
      error: error => { this.error = apiErrorMessage(error, 'Activity timeline could not be loaded.'); this.loading = false; }
    });
  }

  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true;
    this.error = '';
    const value = this.form.getRawValue();
    this.interactionService.create({
      type: value.type,
      subject: value.subject,
      description: value.description,
      occurredAt: new Date(value.occurredAt).toISOString(),
      followUpAt: value.followUpAt ? new Date(value.followUpAt).toISOString() : null,
      companyId: this.parent === 'companies' ? this.recordId : null,
      contactId: this.parent === 'contacts' ? this.recordId : null,
      leadId: this.parent === 'leads' ? this.recordId : null,
      opportunityId: this.parent === 'opportunities' ? this.recordId : null,
      projectId: this.parent === 'projects' ? this.recordId : null,
      projectVendorId: this.parent === 'project-vendors' ? this.recordId : null
    }).subscribe({
      next: () => { this.form.patchValue({ subject: '', description: '', followUpAt: '', occurredAt: this.localNow() }); this.saving = false; this.load(); },
      error: error => { this.error = apiErrorMessage(error, 'Interaction could not be saved.'); this.saving = false; }
    });
  }

  private localNow(): string { return new Date().toISOString().slice(0, 16); }
}
