import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageResponse } from '../../core/api/page.model';
import { Project, ProjectPayload, ProjectSearchParams } from './project.models';
@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly baseUrl = `${environment.apiBaseUrl}/projects`;
  constructor(private readonly http: HttpClient) {}
  search(params: ProjectSearchParams = {}): Observable<PageResponse<Project>> { return this.http.get<PageResponse<Project>>(this.baseUrl, { params: this.toParams(params) }); }
  get(id: string): Observable<Project> { return this.http.get<Project>(`${this.baseUrl}/${id}`); }
  create(payload: ProjectPayload): Observable<Project> { return this.http.post<Project>(this.baseUrl, this.clean(payload)); }
  update(id: string, payload: ProjectPayload): Observable<Project> { return this.http.put<Project>(`${this.baseUrl}/${id}`, this.clean(payload)); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
  private toParams(params: ProjectSearchParams): HttpParams { let p = new HttpParams().set('page', String(params.page ?? 0)).set('size', String(params.size ?? 10)).set('sort', params.sort ?? 'deadline,asc'); if (params.search?.trim()) p = p.set('search', params.search.trim()); if (params.status) p = p.set('status', params.status); if (params.clientId) p = p.set('clientId', params.clientId); if (params.assignedAssociateId) p = p.set('assignedAssociateId', params.assignedAssociateId); return p; }
  private clean(payload: ProjectPayload): ProjectPayload { return { ...payload, name: payload.name.trim(), reference: payload.reference.trim().toUpperCase(), description: this.optional(payload.description), category: this.optional(payload.category), location: this.optional(payload.location), deadline: payload.deadline || null, estimatedBudget: payload.estimatedBudget ?? null, currencyCode: payload.currencyCode.trim().toUpperCase(), assignedAssociateId: payload.assignedAssociateId || null, scopeOfWork: this.optional(payload.scopeOfWork), notes: this.optional(payload.notes) }; }
  private optional(value: string | null | undefined): string | null { return value?.trim() ? value.trim() : null; }
}