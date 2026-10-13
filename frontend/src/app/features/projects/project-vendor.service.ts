import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageResponse } from '../../core/api/page.model';
import { ProjectVendor, ProjectVendorPayload, OutreachStatus } from './project-vendor.models';

@Injectable({ providedIn: 'root' })
export class ProjectVendorService {
  private readonly baseUrl = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {}

  search(projectId: string, status?: OutreachStatus, search?: string): Observable<PageResponse<ProjectVendor>> {
    let params = new HttpParams().set('page', '0').set('size', '100').set('sort', 'vendor.name,asc');
    if (status) params = params.set('status', status);
    if (search?.trim()) params = params.set('search', search.trim());
    return this.http.get<PageResponse<ProjectVendor>>(`${this.baseUrl}/projects/${projectId}/vendors`, { params });
  }

  add(projectId: string, payload: ProjectVendorPayload): Observable<ProjectVendor> {
    return this.http.post<ProjectVendor>(`${this.baseUrl}/projects/${projectId}/vendors`, this.clean(payload));
  }

  update(id: string, payload: ProjectVendorPayload): Observable<ProjectVendor> {
    return this.http.patch<ProjectVendor>(`${this.baseUrl}/project-vendors/${id}`, this.clean(payload));
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/project-vendors/${id}`);
  }

  private clean(payload: ProjectVendorPayload): ProjectVendorPayload {
    return {
      ...payload,
      firstContactDate: payload.firstContactDate || null,
      lastContactDate: payload.lastContactDate || null,
      nextFollowUpDate: payload.nextFollowUpDate || null,
      notes: payload.notes?.trim() || null
    };
  }
}
