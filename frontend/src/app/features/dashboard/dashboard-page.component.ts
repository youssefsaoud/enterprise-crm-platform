import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { DashboardCurrencyTotal, DashboardSummary, PipelineStageSummary, ProjectDashboardSummary, RecentActivity } from './dashboard.models';
import { DashboardService } from './dashboard.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.css']
})
export class DashboardPageComponent implements OnInit {
  summary: DashboardSummary | null = null;
  projectSummary: ProjectDashboardSummary | null = null;
  pipeline: PipelineStageSummary[] = [];
  activities: RecentActivity[] = [];
  loading = false;
  error = '';

  constructor(private readonly dashboardService: DashboardService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.error = '';
    this.dashboardService.projectSummary().subscribe({
      next: summary => this.projectSummary = summary,
      error: () => this.projectSummary = null
    });
    this.dashboardService.summary().subscribe({
      next: summary => { this.summary = summary; this.loadPipeline(); this.loadActivities(); },
      error: () => { this.error = 'Dashboard metrics could not be loaded.'; this.loading = false; }
    });
  }

  totalPipelineValue(values: DashboardCurrencyTotal[] | undefined): string {
    if (!values || values.length === 0) return '0';
    return values.map(item => `${item.amount.toLocaleString()} ${item.currencyCode}`).join(' - ');
  }

  totalQuotationValue(values: DashboardCurrencyTotal[] | undefined): string {
    if (!values || values.length === 0) return '0';
    return values.map(item => `${item.amount.toLocaleString()} ${item.currencyCode}`).join(' - ');
  }

  stagePercent(item: PipelineStageSummary): number {
    const max = Math.max(...this.pipeline.map(row => row.count), 1);
    return Math.max(6, Math.round((item.count / max) * 100));
  }

  activityLink(activity: RecentActivity): string[] {
    if (activity.opportunityId) return ['/opportunities', activity.opportunityId];
    if (activity.leadId) return ['/leads', activity.leadId];
    if (activity.contactId) return ['/contacts', activity.contactId];
    if (activity.companyId) return ['/companies', activity.companyId];
    return ['/dashboard'];
  }

  activityTarget(activity: RecentActivity): string {
    return activity.opportunityName || activity.leadName || activity.contactName || activity.companyName || 'CRM record';
  }

  private loadPipeline(): void {
    this.dashboardService.pipeline().subscribe({ next: pipeline => this.pipeline = pipeline, error: () => this.pipeline = [] });
  }

  private loadActivities(): void {
    this.dashboardService.recentActivities(10).subscribe({
      next: activities => { this.activities = activities; this.loading = false; },
      error: () => { this.activities = []; this.loading = false; }
    });
  }
}
