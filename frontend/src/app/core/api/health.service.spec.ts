import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { HealthService } from './health.service';

describe('HealthService', () => {
  let service: HealthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(HealthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('calls the backend health endpoint', () => {
    service.getHealth().subscribe((response) => {
      expect(response).toEqual({ status: 'UP', service: 'crm-api' });
    });

    const request = httpMock.expectOne('/api/v1/health');
    expect(request.request.method).toBe('GET');
    request.flush({ status: 'UP', service: 'crm-api' });
  });
});
