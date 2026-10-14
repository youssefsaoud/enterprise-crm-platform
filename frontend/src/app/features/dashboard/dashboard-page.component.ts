import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  DashboardCurrencyTotal,
  DashboardSummary,
  PipelineStageSummary,
  ProjectDashboardActivity,
  ProjectDashboardSummary,
  RecentActivity
} from './dashboard.models';
import { DashboardService } from './dashboard.service';

interface PipelineStageView {
  stage: string;
  count: number;
  totals: DashboardCurrencyTotal[];
}

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
  selectedCurrency = 'ALL';
  loading = false;
  error = '';

  constructor(private readonly dashboardService: DashboardService) {}

  get availableCurrencies(): string[] {
    const currencies = new Set<string>();
    this.summary?.openPipelineValue.forEach(item => currencies.add(item.currencyCode));
    this.projectSummary?.quotationValueByCurrency.forEach(item => currencies.add(item.currencyCode));
    this.pipeline.forEach(item => currencies.add(item.currencyCode));
    return [...currencies].sort();
  }

  get groupedPipeline(): PipelineStageView[] {
    const grouped = new Map<string, PipelineStageView>();
    this.pipeline
      .filter(item => this.selectedCurrency === 'ALL' || item.currencyCode === this.selectedCurrency)
      .forEach(item => {
        const row = grouped.get(item.stage) || { stage: item.stage, count: 0, totals: [] };
        row.count += item.count;
        row.totals.push({ currencyCode: item.currencyCode, amount: item.totalAmount });
        grouped.set(item.stage, row);
      });
    return [...grouped.values()];
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.dashboardService.projectSummary().subscribe({
      next: summary => this.projectSummary = summary,
      error: () => this.projectSummary = null
    });
    this.dashboardService.summary().subscribe({
      next: summary => {
        this.summary = summary;
        this.loadPipeline();
        this.loadActivities();
      },
      error: () => {
        this.error = 'Dashboard metrics could not be loaded.';
        this.loading = false;
      }
    });
  }

  selectCurrency(currency: string): void {
    this.selectedCurrency = currency;
  }

  displayTotals(values: DashboardCurrencyTotal[] | undefined): string {
    const filtered = (values || []).filter(item => this.selectedCurrency === 'ALL' || item.currencyCode === this.selectedCurrency);
    if (filtered.length === 0) {
      return this.selectedCurrency === 'ALL' ? 'No value' : `0 ${this.selectedCurrency}`;
    }
    return filtered
      .map(item => `${item.amount.toLocaleString(undefined, { maximumFractionDigits: 2 })} ${item.currencyCode}`)
      .join(' · ');
  }

  statusLabel(value: string): string {
    return value
      .toLowerCase()
      .replaceAll('_', ' ')
      .replace(/\b\w/g, character => character.toUpperCase());
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

  projectActivityLink(activity: ProjectDashboardActivity): string[] {
    return ['/projects', activity.projectId];
  }

  private loadPipeline(): void {
    this.dashboardService.pipeline().subscribe({
      next: pipeline => this.pipeline = pipeline,
      error: () => this.pipeline = []
    });
  }

  private loadActivities(): void {
    this.dashboardService.recentActivities(10).subscribe({
      next: activities => {
        this.activities = activities;
        this.loading = false;
      },
      error: () => {
        this.activities = [];
        this.loading = false;
      }
    });
  }
}
