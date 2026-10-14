import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { DashboardPageComponent } from './dashboard-page.component';
import { DashboardService } from './dashboard.service';

describe('DashboardPageComponent', () => {
  let fixture: ComponentFixture<DashboardPageComponent>;
  let dashboardService: jasmine.SpyObj<DashboardService>;

  beforeEach(async () => {
    dashboardService = jasmine.createSpyObj('DashboardService', ['summary', 'projectSummary', 'pipeline', 'recentActivities']);
    dashboardService.projectSummary.and.returnValue(of({
      totalProjects: 2,
      activeProjects: 1,
      nearingDeadlineProjects: 0,
      overdueProjects: 0,
      vendorsContacted: 1,
      vendorsAwaitingResponse: 1,
      quotationsReceived: 1,
      quotationsPending: 1,
      quoteResponseRate: 50,
      upcomingFollowUps: 1,
      overdueFollowUps: 0,
      projectsByStatus: [],
      quotationValueByCurrency: [],
      recentActivities: []
    }));
    dashboardService.summary.and.returnValue(of({
      totalLeads: 3,
      leadsByStatus: [],
      openOpportunities: 1,
      openPipelineValue: [],
      recentInteractionCount: 0
    }));
    dashboardService.pipeline.and.returnValue(of([]));
    dashboardService.recentActivities.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent, RouterTestingModule],
      providers: [{ provide: DashboardService, useValue: dashboardService }]
    }).compileComponents();
    fixture = TestBed.createComponent(DashboardPageComponent);
  });

  it('loads project and legacy dashboard data', () => {
    fixture.detectChanges();

    expect(fixture.componentInstance.summary?.totalLeads).toBe(3);
    expect(fixture.componentInstance.projectSummary?.totalProjects).toBe(2);
    expect(dashboardService.projectSummary).toHaveBeenCalled();
    expect(dashboardService.pipeline).toHaveBeenCalled();
    expect(dashboardService.recentActivities).toHaveBeenCalledWith(10);
  });

  it('groups pipeline stages once while preserving currency totals', () => {
    fixture.componentInstance.pipeline = [
      { stage: 'NEGOTIATION', currencyCode: 'USD', count: 2, totalAmount: 12000 },
      { stage: 'NEGOTIATION', currencyCode: 'EUR', count: 1, totalAmount: 9000 },
      { stage: 'PROPOSAL', currencyCode: 'USD', count: 3, totalAmount: 18000 }
    ];

    expect(fixture.componentInstance.groupedPipeline.length).toBe(2);
    expect(fixture.componentInstance.groupedPipeline[0].count).toBe(3);
    expect(fixture.componentInstance.groupedPipeline[0].totals.length).toBe(2);

    fixture.componentInstance.selectCurrency('USD');

    expect(fixture.componentInstance.groupedPipeline[0].count).toBe(2);
    expect(fixture.componentInstance.displayTotals(fixture.componentInstance.groupedPipeline[0].totals)).toContain('USD');
    expect(fixture.componentInstance.displayTotals(fixture.componentInstance.groupedPipeline[0].totals)).not.toContain('EUR');
  });

  it('shows an error when summary loading fails', () => {
    dashboardService.summary.and.returnValue(throwError(() => new Error('down')));
    fixture.detectChanges();

    expect(fixture.componentInstance.error).toContain('could not be loaded');
  });
});
