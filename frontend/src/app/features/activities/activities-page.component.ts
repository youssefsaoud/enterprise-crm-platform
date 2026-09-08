import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Interaction, InteractionType } from '../interactions/interaction.models';
import { InteractionService } from '../interactions/interaction.service';

@Component({
  selector: 'app-activities-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './activities-page.component.html',
  styleUrls: ['./activities-page.component.css']
})
export class ActivitiesPageComponent implements OnInit {
  readonly typeControl = new FormControl<InteractionType | ''>('', { nonNullable: true });
  readonly types: InteractionType[] = ['CALL', 'EMAIL', 'MEETING', 'NOTE'];
  activities: Interaction[] = [];
  loading = false;
  error = '';

  constructor(private readonly interactionService: InteractionService) {}

  ngOnInit(): void {
    this.load();
    this.typeControl.valueChanges.subscribe(() => this.load());
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.interactionService.search({ type: this.typeControl.value, size: 20, sort: 'occurredAt,desc' }).subscribe({
      next: (result) => { this.activities = result.content; this.loading = false; },
      error: () => { this.error = 'Activities could not be loaded.'; this.loading = false; }
    });
  }

  activityLink(activity: Interaction): string[] {
    if (activity.opportunityId) { return ['/opportunities', activity.opportunityId]; }
    if (activity.leadId) { return ['/leads', activity.leadId]; }
    if (activity.contactId) { return ['/contacts', activity.contactId]; }
    if (activity.companyId) { return ['/companies', activity.companyId]; }
    return ['/activities'];
  }

  activityTarget(activity: Interaction): string {
    return activity.opportunityName || activity.leadName || activity.contactName || activity.companyName || 'CRM record';
  }
}
