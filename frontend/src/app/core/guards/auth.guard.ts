import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

import { AuthService } from '../auth/auth.service';
import { UserRole } from '../auth/auth.models';

export const authGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  return authService.isAuthenticated() ? true : router.createUrlTree(['/login']);
};

export const guestGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  return authService.isAuthenticated() ? router.createUrlTree(['/dashboard']) : true;
};

export const roleGuard = (allowedRoles: UserRole[]): CanActivateFn => (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();
  return user && allowedRoles.includes(user.role) ? true : router.createUrlTree(['/dashboard']);
};
