import { Location } from '@angular/common';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { ActivationPageComponent } from './activation-page.component';
import { TeamService } from '../team/team.service';

describe('ActivationPageComponent', () => {
  let fixture: ComponentFixture<ActivationPageComponent>;
  let page: ActivationPageComponent;
  let service: jasmine.SpyObj<TeamService>;
  const testToken = 'a'.repeat(43);
  beforeEach(async () => {
    service = jasmine.createSpyObj<TeamService>('TeamService', ['validate', 'accept']);
    service.validate.and.returnValue(of({ fullName: 'Taylor Lee', role: 'SALES_REP', expiresAt: '2026-10-16T10:00:00Z' }));
    await TestBed.configureTestingModule({
      imports: [ActivationPageComponent, RouterTestingModule],
      providers: [{ provide: TeamService, useValue: service }, {
        provide: ActivatedRoute, useValue: { snapshot: { fragment: `token=${testToken}`, queryParamMap: convertToParamMap({}) } }
      }]
    }).compileComponents();
    fixture = TestBed.createComponent(ActivationPageComponent); page = fixture.componentInstance;
  });
  it('validates a fragment invitation and renders employee identity', () => {
    fixture.detectChanges(); expect(service.validate).toHaveBeenCalledWith(testToken);
    expect(fixture.nativeElement.textContent).toContain('Taylor Lee'); expect(fixture.nativeElement.textContent).toContain('Associate');
  });
  it('renders expired links without a password form', () => {
    service.validate.and.returnValue(throwError(() => ({ status: 410 }))); fixture.detectChanges();
    expect(page.state).toBe('expired'); expect(fixture.nativeElement.querySelector('form')).toBeNull();
  });
  it('renders invalid links safely', () => {
    service.validate.and.returnValue(throwError(() => ({ status: 400 }))); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Invitation unavailable');
  });
  it('requires matching passwords before submitting', () => {
    fixture.detectChanges(); page.form.setValue({ password: 'TemporaryPassword42', confirmPassword: 'Mismatch42' }); page.activate();
    expect(page.form.hasError('passwordMismatch')).toBeTrue(); expect(service.accept).not.toHaveBeenCalled();
  });
  it('activates, clears the token URL, and offers sign in without logging in automatically', () => {
    service.accept.and.returnValue(of(void 0)); fixture.detectChanges();
    const location = TestBed.inject(Location); spyOn(location, 'replaceState');
    page.form.setValue({ password: 'TemporaryPassword42', confirmPassword: 'TemporaryPassword42' }); page.activate(); fixture.detectChanges();
    expect(page.state).toBe('success'); expect(location.replaceState).toHaveBeenCalledWith('/activate-account');
    expect(fixture.nativeElement.querySelector('a.primary-link').getAttribute('href')).toBe('/login');
    expect(page.form.controls.password.value).toBe('');
  });
});
