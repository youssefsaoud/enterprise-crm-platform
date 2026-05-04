import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PageResponse } from '../../core/api/page.model';
import { environment } from '../../../environments/environment';
import { Lead, LeadEvent, LeadPayload, LeadSearchParams, LeadStatus, UserSummary } from './lead.models';

@Injectable({ providedIn: 'root' })
export class LeadService {
  private readonly baseUrl = `${environment.apiBaseUrl}/leads`;

  constructor(private readonly http: HttpClient) {}

  search(params: LeadSearchParams = {}): Observable<PageResponse<Lead>> {
    return this.http.get<PageResponse<Lead>>(this.baseUrl, { params: this.toParams(params) });
  }

  get(id: string): Observable<Lead> {
    return this.http.get<Lead>(`${this.baseUrl}/${id}`);
  }

  create(payload: LeadPayload): Observable<Lead> {
    return this.http.post<Lead>(this.baseUrl, this.clean(payload));
  }

  update(id: string, payload: LeadPayload): Observable<Lead> {
    return this.http.put<Lead>(`${this.baseUrl}/${id}`, this.clean(payload));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  assign(id: string, assigneeUserId: string | null): Observable<Lead> {
    return this.http.patch<Lead>(`${this.baseUrl}/${id}/assignment`, { assigneeUserId });
  }

  changeStatus(id: string, status: LeadStatus): Observable<Lead> {
    return this.http.patch<Lead>(`${this.baseUrl}/${id}/status`, { status });
  }

  history(id: string): Observable<PageResponse<LeadEvent>> {
    return this.http.get<PageResponse<LeadEvent>>(`${this.baseUrl}/${id}/history`, {
      params: new HttpParams().set('size', '20').set('sort', 'createdAt,desc')
    });
  }

  salesRepresentatives(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${environment.apiBaseUrl}/users/sales-representatives`);
  }

  private toParams(params: LeadSearchParams): HttpParams {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10))
      .set('sort', params.sort ?? 'createdAt,desc');

    for (const [key, value] of Object.entries(params)) {
      if (['page', 'size', 'sort'].includes(key)) {
        continue;
      }
      if (typeof value === 'string' && value.trim()) {
        httpParams = httpParams.set(key, value.trim());
      }
    }
    return httpParams;
  }

  private clean(payload: LeadPayload): LeadPayload {
    return {
      firstName: payload.firstName.trim(),
      lastName: payload.lastName.trim(),
      email: this.optional(payload.email),
      phone: this.optional(payload.phone),
      companyName: this.optional(payload.companyName),
      companyId: this.optional(payload.companyId),
      source: payload.source
    };
  }

  private optional(value: string | null | undefined): string | null {
    return value?.trim() ? value.trim() : null;
  }
}
