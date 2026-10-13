import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProjectDocument } from './project-document.models';

@Injectable({ providedIn: 'root' })
export class ProjectDocumentService {
  private readonly baseUrl = `${environment.apiBaseUrl}`;
  constructor(private readonly http: HttpClient) {}

  list(projectId: string): Observable<ProjectDocument[]> {
    return this.http.get<ProjectDocument[]>(`${this.baseUrl}/projects/${projectId}/documents`);
  }

  upload(projectId: string, file: File, quotationId?: string): Observable<ProjectDocument> {
    const data = new FormData();
    data.append('file', file, file.name);
    if (quotationId) data.append('quotationId', quotationId);
    return this.http.post<ProjectDocument>(`${this.baseUrl}/projects/${projectId}/documents`, data);
  }

  download(id: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/documents/${id}/download`, { responseType: 'blob' });
  }
}
