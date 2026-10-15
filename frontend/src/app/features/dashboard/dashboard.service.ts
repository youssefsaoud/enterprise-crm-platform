import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { DashboardDateRange, DashboardSummary, PipelineStageSummary, ProjectDashboardAnalytics, ProjectDashboardSummary, RecentActivity } from './dashboard.models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${environment.apiBaseUrl}/dashboard`;

  constructor(private readonly http: HttpClient) {}

  summary(): Observable<DashboardSummary> { return this.http.get<DashboardSummary>(`${this.baseUrl}/summary`); }
  projectSummary(): Observable<ProjectDashboardSummary> { return this.http.get<ProjectDashboardSummary>(`${environment.apiBaseUrl}/project-dashboard/summary`); }
  projectAnalytics(range: DashboardDateRange, associateId?: string): Observable<ProjectDashboardAnalytics> {
    const params: Record<string, string> = { range };
    if (associateId) params['associateId'] = associateId;
    return this.http.get<ProjectDashboardAnalytics>(`${environment.apiBaseUrl}/project-dashboard/analytics`, { params });
  }
  pipeline(): Observable<PipelineStageSummary[]> { return this.http.get<PipelineStageSummary[]>(`${this.baseUrl}/pipeline`); }
  recentActivities(limit = 10): Observable<RecentActivity[]> { return this.http.get<RecentActivity[]>(`${this.baseUrl}/recent-activities`, { params: { limit } }); }
}
