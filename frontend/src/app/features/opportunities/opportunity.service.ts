import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PageResponse } from '../../core/api/page.model';
import { environment } from '../../../environments/environment';
import { Opportunity, OpportunityPayload, OpportunitySearchParams, OpportunityStage, PipelineSummary } from './opportunity.models';

@Injectable({ providedIn: 'root' })
export class OpportunityService {
  private readonly baseUrl = `${environment.apiBaseUrl}/opportunities`;

  constructor(private readonly http: HttpClient) {}

  search(params: OpportunitySearchParams = {}): Observable<PageResponse<Opportunity>> {
    return this.http.get<PageResponse<Opportunity>>(this.baseUrl, { params: this.toParams(params) });
  }

  get(id: string): Observable<Opportunity> {
    return this.http.get<Opportunity>(`${this.baseUrl}/${id}`);
  }

  create(payload: OpportunityPayload): Observable<Opportunity> {
    return this.http.post<Opportunity>(this.baseUrl, this.clean(payload));
  }

  update(id: string, payload: OpportunityPayload): Observable<Opportunity> {
    return this.http.put<Opportunity>(`${this.baseUrl}/${id}`, this.clean(payload));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  changeStage(id: string, stage: OpportunityStage): Observable<Opportunity> {
    return this.http.patch<Opportunity>(`${this.baseUrl}/${id}/stage`, { stage });
  }

  changeOwner(id: string, ownerUserId: string): Observable<Opportunity> {
    return this.http.patch<Opportunity>(`${this.baseUrl}/${id}/owner`, { ownerUserId });
  }

  pipelineSummary(): Observable<PipelineSummary[]> {
    return this.http.get<PipelineSummary[]>(`${this.baseUrl}/pipeline/summary`);
  }

  private toParams(params: OpportunitySearchParams): HttpParams {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10))
      .set('sort', params.sort ?? 'expectedCloseDate,asc');
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

  private clean(payload: OpportunityPayload): OpportunityPayload {
    return {
      name: payload.name.trim(),
      companyId: payload.companyId,
      contactId: this.optional(payload.contactId),
      ownerUserId: this.optional(payload.ownerUserId),
      amount: Number(payload.amount),
      currencyCode: payload.currencyCode?.trim().toUpperCase() || 'USD',
      expectedCloseDate: this.optional(payload.expectedCloseDate),
      description: this.optional(payload.description)
    };
  }

  private optional(value: string | null | undefined): string | null {
    return value?.trim() ? value.trim() : null;
  }
}
