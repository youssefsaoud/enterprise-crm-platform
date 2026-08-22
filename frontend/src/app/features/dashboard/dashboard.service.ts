import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { DashboardSummary, PipelineStageSummary, RecentActivity } from './dashboard.models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${environment.apiBaseUrl}/dashboard`;

  constructor(private readonly http: HttpClient) {}

  summary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.baseUrl}/summary`);
  }

  pipeline(): Observable<PipelineStageSummary[]> {
    return this.http.get<PipelineStageSummary[]>(`${this.baseUrl}/pipeline`);
  }

  recentActivities(limit = 10): Observable<RecentActivity[]> {
    return this.http.get<RecentActivity[]>(`${this.baseUrl}/recent-activities`, { params: { limit } });
  }
}
