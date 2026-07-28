import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { OpportunityStage } from '../opportunities/opportunity.models';

export interface LeadConversionPayload {
  existingCompanyId?: string | null;
  newCompany?: { name: string; industry?: string | null; website?: string | null; phone?: string | null; email?: string | null; address?: string | null } | null;
  existingContactId?: string | null;
  newContact?: { firstName: string; lastName: string; email?: string | null; phone?: string | null; jobTitle?: string | null } | null;
  opportunity?: { name: string; amount?: number | null; currencyCode?: string | null; expectedCloseDate?: string | null; stage?: OpportunityStage | null; description?: string | null } | null;
  notes?: string | null;
}

export interface LeadConversionResult {
  id: string;
  leadId: string;
  companyId: string;
  companyName: string;
  contactId: string;
  contactName: string;
  opportunityId?: string | null;
  opportunityName?: string | null;
  convertedByUserId: string;
  convertedByName: string;
  convertedAt: string;
  notes?: string | null;
}

@Injectable({ providedIn: 'root' })
export class LeadConversionService {
  constructor(private readonly http: HttpClient) {}

  convert(leadId: string, payload: LeadConversionPayload): Observable<LeadConversionResult> {
    return this.http.post<LeadConversionResult>(`${environment.apiBaseUrl}/leads/${leadId}/convert`, payload);
  }

  get(leadId: string): Observable<LeadConversionResult> {
    return this.http.get<LeadConversionResult>(`${environment.apiBaseUrl}/leads/${leadId}/conversion`);
  }
}
