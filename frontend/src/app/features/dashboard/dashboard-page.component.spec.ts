import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { DashboardPageComponent } from './dashboard-page.component';
import { DashboardService } from './dashboard.service';

 describe('DashboardPageComponent', () => {
  let fixture: ComponentFixture<DashboardPageComponent>;
  let dashboardService: jasmine.SpyObj<DashboardService>;

  beforeEach(async () => {
    dashboardService = jasmine.createSpyObj('DashboardService', ['summary', 'pipeline', 'recentActivities']);
    dashboardService.summary.and.returnValue(of({ totalLeads: 3, leadsByStatus: [], openOpportunities: 1, openPipelineValue: [], recentInteractionCount: 0 }));
    dashboardService.pipeline.and.returnValue(of([]));
    dashboardService.recentActivities.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent],
      providers: [{ provide: DashboardService, useValue: dashboardService }]
    }).compileComponents();
    fixture = TestBed.createComponent(DashboardPageComponent);
  });

  it('loads summary, pipeline and activity data', () => {
    fixture.detectChanges();
    expect(fixture.componentInstance.summary?.totalLeads).toBe(3);
    expect(dashboardService.pipeline).toHaveBeenCalled();
    expect(dashboardService.recentActivities).toHaveBeenCalledWith(10);
  });

  it('shows an error when summary loading fails', () => {
    dashboardService.summary.and.returnValue(throwError(() => new Error('down')));
    fixture.detectChanges();
    expect(fixture.componentInstance.error).toContain('could not be loaded');
  });
});
