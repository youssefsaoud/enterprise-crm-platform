import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterOutlet, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AppComponent } from '../../app.component';
import { AuthService } from '../auth/auth.service';
import { authGuard, guestGuard } from './auth.guard';

@Component({ standalone: true, template: '<span>Login page</span>' })
class LoginStubComponent {}

@Component({ standalone: true, template: '<span>Dashboard page</span>' })
class DashboardStubComponent {}

@Component({ standalone: true, imports: [RouterOutlet], template: '<span>Authenticated shell</span><router-outlet></router-outlet>' })
class ShellStubComponent {}

const testRoutes: Routes = [
  { path: 'login', component: LoginStubComponent, canActivate: [guestGuard] },
  {
    path: '',
    component: ShellStubComponent,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [{ path: 'dashboard', component: DashboardStubComponent }]
  },
  { path: '**', redirectTo: 'dashboard' }
];

describe('authentication routing', () => {
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['isAuthenticated']);
    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterTestingModule.withRoutes(testRoutes)],
      providers: [{ provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    router = TestBed.inject(Router);
  });

  afterEach(() => fixture.destroy());

  it('redirects an unauthenticated protected navigation to the standalone login page', async () => {
    authService.isAuthenticated.and.returnValue(false);

    await router.navigateByUrl('/dashboard');
    fixture.detectChanges();

    expect(router.url).toBe('/login');
    expect(fixture.nativeElement.textContent).toContain('Login page');
    expect(fixture.nativeElement.textContent).not.toContain('Authenticated shell');
  });

  it('redirects an authenticated login navigation to the dashboard shell', async () => {
    authService.isAuthenticated.and.returnValue(true);

    await router.navigateByUrl('/login');
    fixture.detectChanges();

    expect(router.url).toBe('/dashboard');
    expect(fixture.nativeElement.textContent).toContain('Authenticated shell');
    expect(fixture.nativeElement.textContent).toContain('Dashboard page');
    expect(fixture.nativeElement.textContent).not.toContain('Login page');
  });
});
