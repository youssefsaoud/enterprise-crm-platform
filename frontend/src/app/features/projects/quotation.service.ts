import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quotation, QuotationPayload } from './quotation.models';

@Injectable({ providedIn: 'root' })
export class QuotationService {
  private readonly baseUrl = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {}

  list(projectVendorId: string): Observable<Quotation[]> {
    return this.http.get<Quotation[]>(`${this.baseUrl}/project-vendors/${projectVendorId}/quotations`);
  }

  create(projectVendorId: string, payload: QuotationPayload): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.baseUrl}/project-vendors/${projectVendorId}/quotations`, this.clean(payload));
  }

  select(id: string): Observable<Quotation> {
    return this.http.post<Quotation>(`${this.baseUrl}/quotations/${id}/select`, {});
  }

  private clean(payload: QuotationPayload): QuotationPayload {
    return { ...payload, currencyCode: payload.currencyCode.trim().toUpperCase(), validUntil: payload.validUntil || null, notes: payload.notes?.trim() || null };
  }
}
