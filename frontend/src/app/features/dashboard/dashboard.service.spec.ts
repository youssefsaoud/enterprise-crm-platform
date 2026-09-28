import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';

 describe('DashboardService', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('loads summary metrics', () => {
    service.summary().subscribe((summary) => expect(summary.totalLeads).toBe(4));
    const request = httpMock.expectOne('/api/v1/dashboard/summary');
    expect(request.request.method).toBe('GET');
    request.flush({ totalLeads: 4, leadsByStatus: [], openOpportunities: 2, openPipelineValue: [], recentInteractionCount: 1 });
  });

  it('loads recent activities with a bounded limit parameter', () => {
    service.recentActivities(7).subscribe((activities) => expect(activities.length).toBe(1));
    const request = httpMock.expectOne('/api/v1/dashboard/recent-activities?limit=7');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 'a1', type: 'CALL', subject: 'Call', occurredAt: '2024-03-01T10:00:00Z', authorName: 'Rep' }]);
  });
});
