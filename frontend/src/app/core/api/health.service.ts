import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface HealthResponse {
  status: 'UP' | string;
  service: string;
}

@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly healthUrl = `${environment.apiBaseUrl}/health`;

  constructor(private readonly http: HttpClient) {}

  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(this.healthUrl);
  }
}
