import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { AuthService } from '../../core/auth/auth.service';
import { Lead, LeadEvent, LeadStatus, UserSummary } from './lead.models';
import { LeadService } from './lead.service';

@Component({
  selector: 'app-lead-detail-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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
