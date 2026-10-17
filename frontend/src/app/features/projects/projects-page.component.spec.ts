import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { ProjectsPageComponent } from './projects-page.component';
import { ProjectService } from './project.service';

describe('ProjectsPageComponent', () => {
  it('uses server counters, applies dashboard deep links, resets pagination and hides Associate creation', fakeAsync(() => {
    const service = jasmine.createSpyObj('ProjectService', ['search', 'statusCounts']);
    service.search.and.returnValue(of({ content: [], totalElements: 0, totalPages: 0 }));
    service.statusCounts.and.returnValue(of({ total: 5, statuses: [{ status: 'COMPLETED', label: 'Completed', count: 2 }] }));
    TestBed.configureTestingModule({ imports: [ProjectsPageComponent, RouterTestingModule], providers: [
      { provide: ProjectService, useValue: service },
      { provide: AuthService, useValue: { currentUser$: of({ role: 'SALES_REP' }) } },
      { provide: ActivatedRoute, useValue: { queryParamMap: of(convertToParamMap({ status: 'COMPLETED' })) } }
    ] });
    const fixture = TestBed.createComponent(ProjectsPageComponent); fixture.detectChanges();
    expect(service.search.calls.mostRecent().args[0].status).toBe('COMPLETED');
    expect(fixture.nativeElement.querySelector('a.project-button')).toBeNull();
    const buttons: NodeListOf<HTMLButtonElement> = fixture.nativeElement.querySelectorAll('.status-filters button');
    expect(buttons[1].getAttribute('aria-pressed')).toBe('true');
    fixture.componentInstance.page = 3; buttons[0].click(); fixture.detectChanges();
    expect(fixture.componentInstance.page).toBe(0);
    fixture.componentInstance.searchControl.setValue('camera'); tick(251);
    expect(service.statusCounts.calls.mostRecent().args[0]).toEqual({ search: 'camera' });
    expect(service.search.calls.mostRecent().args[0].page).toBe(0);
    expect(fixture.nativeElement.textContent).toContain('All 5');
  }));
});
