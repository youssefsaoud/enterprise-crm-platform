import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PageResponse } from '../../core/api/page.model';
import { environment } from '../../../environments/environment';
import { Contact, ContactPayload, ContactSearchParams } from './contact.models';

@Injectable({ providedIn: 'root' })
export class ContactService {
  private readonly baseUrl = `${environment.apiBaseUrl}/contacts`;

  constructor(private readonly http: HttpClient) {}

  search(params: ContactSearchParams = {}): Observable<PageResponse<Contact>> {
    return this.http.get<PageResponse<Contact>>(this.baseUrl, { params: this.toParams(params) });
  }

  forCompany(companyId: string, page = 0, size = 10): Observable<PageResponse<Contact>> {
    return this.http.get<PageResponse<Contact>>(`${environment.apiBaseUrl}/companies/${companyId}/contacts`, {
      params: new HttpParams().set('page', String(page)).set('size', String(size)).set('sort', 'lastName,asc')
    });
  }

  get(id: string): Observable<Contact> {
    return this.http.get<Contact>(`${this.baseUrl}/${id}`);
  }

  create(payload: ContactPayload): Observable<Contact> {
    return this.http.post<Contact>(this.baseUrl, this.clean(payload));
  }

  update(id: string, payload: ContactPayload): Observable<Contact> {
    return this.http.put<Contact>(`${this.baseUrl}/${id}`, this.clean(payload));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  private toParams(params: ContactSearchParams): HttpParams {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10))
      .set('sort', params.sort ?? 'lastName,asc');

    if (params.search?.trim()) {
      httpParams = httpParams.set('search', params.search.trim());
    }
    if (params.companyId?.trim()) {
      httpParams = httpParams.set('companyId', params.companyId.trim());
    }
    return httpParams;
  }

  private clean(payload: ContactPayload): ContactPayload {
    return {
      companyId: payload.companyId,
      firstName: payload.firstName.trim(),
      lastName: payload.lastName.trim(),
      email: this.optional(payload.email),
      phone: this.optional(payload.phone),
      jobTitle: this.optional(payload.jobTitle)
    };
  }

  private optional(value: string | null | undefined): string | null {
    return value?.trim() ? value.trim() : null;
  }
}
