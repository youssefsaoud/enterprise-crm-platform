import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PageResponse } from '../../core/api/page.model';
import { environment } from '../../../environments/environment';
import { Interaction, InteractionPayload, InteractionSearchParams } from './interaction.models';

@Injectable({ providedIn: 'root' })
export class InteractionService {
  private readonly baseUrl = `${environment.apiBaseUrl}/interactions`;

  constructor(private readonly http: HttpClient) {}

  search(params: InteractionSearchParams = {}): Observable<PageResponse<Interaction>> {
    return this.http.get<PageResponse<Interaction>>(this.baseUrl, { params: this.toParams(params) });
  }

  timeline(parent: 'companies' | 'contacts' | 'leads' | 'opportunities' | 'projects' | 'project-vendors', id: string): Observable<PageResponse<Interaction>> {
    return this.http.get<PageResponse<Interaction>>(`${environment.apiBaseUrl}/${parent}/${id}/interactions`, {
      params: new HttpParams().set('size', '20').set('sort', 'occurredAt,desc')
    });
  }

  create(payload: InteractionPayload): Observable<Interaction> { return this.http.post<Interaction>(this.baseUrl, this.clean(payload)); }
  update(id: string, payload: InteractionPayload): Observable<Interaction> { return this.http.patch<Interaction>(`${this.baseUrl}/${id}`, this.clean(payload)); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }

  private toParams(params: InteractionSearchParams): HttpParams {
    let httpParams = new HttpParams().set('page', String(params.page ?? 0)).set('size', String(params.size ?? 20)).set('sort', params.sort ?? 'occurredAt,desc');
    for (const [key, value] of Object.entries(params)) {
      if (['page', 'size', 'sort'].includes(key)) continue;
      if (typeof value === 'string' && value.trim()) httpParams = httpParams.set(key, value.trim());
    }
    return httpParams;
  }

  private clean(payload: InteractionPayload): InteractionPayload {
    return {
      ...payload,
      type: payload.type,
      subject: payload.subject.trim(),
      description: this.optional(payload.description),
      occurredAt: payload.occurredAt,
      followUpAt: this.optional(payload.followUpAt),
      companyId: this.optional(payload.companyId),
      contactId: this.optional(payload.contactId),
      leadId: this.optional(payload.leadId),
      opportunityId: this.optional(payload.opportunityId),
      projectId: this.optional(payload.projectId),
      projectVendorId: this.optional(payload.projectVendorId),
      quotationId: this.optional(payload.quotationId)
    };
  }

  private optional(value: string | null | undefined): string | null { return value?.trim() ? value.trim() : null; }
}
