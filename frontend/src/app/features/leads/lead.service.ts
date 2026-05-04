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
