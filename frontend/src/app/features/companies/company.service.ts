import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PageResponse } from '../../core/api/page.model';
import { environment } from '../../../environments/environment';
import { Company, CompanyPayload, CompanySearchParams } from './company.models';

@Injectable({ providedIn: 'root' })
export class CompanyService {
  private readonly baseUrl = `${environment.apiBaseUrl}/companies`;

  constructor(private readonly http: HttpClient) {}

  search(params: CompanySearchParams = {}): Observable<PageResponse<Company>> {
    return this.http.get<PageResponse<Company>>(this.baseUrl, { params: this.toParams(params) });
  }

  get(id: string): Observable<Company> {
    return this.http.get<Company>(`${this.baseUrl}/${id}`);
  }

  create(payload: CompanyPayload): Observable<Company> {
    return this.http.post<Company>(this.baseUrl, this.clean(payload));
  }

  update(id: string, payload: CompanyPayload): Observable<Company> {
    return this.http.put<Company>(`${this.baseUrl}/${id}`, this.clean(payload));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  private toParams(params: CompanySearchParams): HttpParams {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10))
      .set('sort', params.sort ?? 'name,asc');

    if (params.search?.trim()) {
      httpParams = httpParams.set('search', params.search.trim());
    }
    if (params.industry?.trim()) {
      httpParams = httpParams.set('industry', params.industry.trim());
    }
    return httpParams;
  }

  private clean(payload: CompanyPayload): CompanyPayload {
    return {
      name: payload.name.trim(),
      industry: this.optional(payload.industry),
      website: this.optional(payload.website),
      phone: this.optional(payload.phone),
      email: this.optional(payload.email),
      address: this.optional(payload.address)
    };
  }

  private optional(value: string | null | undefined): string | null {
    return value?.trim() ? value.trim() : null;
  }
}
