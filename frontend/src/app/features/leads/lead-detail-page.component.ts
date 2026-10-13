import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { AuthService } from '../../core/auth/auth.service';
import { Lead, LeadEvent, LeadStatus, UserSummary } from './lead.models';
import { LeadService } from './lead.service';
import { LeadConversionPanelComponent } from './lead-conversion-panel.component';
import { InteractionTimelineComponent } from '../interactions/interaction-timeline.component';

@Component({
  selector: 'app-lead-detail-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LeadConversionPanelComponent, InteractionTimelineComponent],
  templateUrl: './lead-detail-page.component.html',
  styleUrls: ['./lead-detail-page.component.css']
})
export class LeadDetailPageComponent implements OnInit {
  readonly currentUser$ = this.authService.currentUser$;
  readonly assigneeControl = new FormControl<string>('', { nonNullable: true });
  readonly statusControl = new FormControl<LeadStatus>('NEW', { nonNullable: true });
  readonly statuses: LeadStatus[] = ['NEW', 'CONTACTED', 'QUALIFIED', 'UNQUALIFIED'];

  lead: Lead | null = null;
  history: LeadEvent[] = [];
  salesReps: UserSummary[] = [];
  loading = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly leadService: LeadService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigateByUrl('/leads');
      return;
    }
    this.load(id);
    this.loadSalesRepresentatives();
  }

  assign(): void {
    if (!this.lead) {
      return;
    }
    this.leadService.assign(this.lead.id, this.assigneeControl.value || null).subscribe({
      next: (lead) => {
        this.applyLead(lead);
        this.loadHistory(lead.id);
      },
      error: (error) => this.error = apiErrorMessage(error, 'Lead assignment failed.')
    });
  }

  changeStatus(): void {
    if (!this.lead) {
      return;
    }
    this.leadService.changeStatus(this.lead.id, this.statusControl.value).subscribe({
      next: (lead) => {
        this.applyLead(lead);
        this.loadHistory(lead.id);
      },
      error: (error) => this.error = apiErrorMessage(error, 'Lead status could not be changed.')
    });
  }

  deleteLead(): void {
    if (!this.lead || !confirm('Delete this lead?')) {
      return;
    }
    this.leadService.delete(this.lead.id).subscribe({
      next: () => this.router.navigateByUrl('/leads'),
      error: (error) => this.error = apiErrorMessage(error, 'Lead could not be deleted.')
    });
  }

  canManage(role: string): boolean {
    return role === 'ADMIN' || role === 'MANAGER';
  }

  canDelete(role: string): boolean {
    return role === 'ADMIN';
  }

  load(id: string): void {
    this.loading = true;
    this.error = '';
    this.leadService.get(id).subscribe({
      next: (lead) => {
        this.applyLead(lead);
        this.loadHistory(lead.id);
        this.loading = false;
      },
      error: (error) => {
        this.error = apiErrorMessage(error, 'Lead not found.');
        this.loading = false;
      }
    });
  }

  private loadHistory(id: string): void {
    this.leadService.history(id).subscribe({
      next: (result) => this.history = result.content,
      error: () => this.history = []
    });
  }

  private loadSalesRepresentatives(): void {
    this.leadService.salesRepresentatives().subscribe({
      next: (users) => this.salesReps = users,
      error: () => this.salesReps = []
    });
  }

  private applyLead(lead: Lead): void {
    this.lead = lead;
    this.assigneeControl.setValue(lead.assignedToUserId ?? '', { emitEvent: false });
    this.statusControl.setValue(lead.status, { emitEvent: false });
  }
}
