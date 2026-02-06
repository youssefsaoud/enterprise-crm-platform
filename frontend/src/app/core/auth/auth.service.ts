import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, CurrentUser, LoginRequest } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly userSubject = new BehaviorSubject<CurrentUser | null>(null);
  private accessToken: string | null = null;

  readonly currentUser$ = this.userSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<CurrentUser> {
    return this.http.post<AuthResponse>(`${environment.apiBaseUrl}/auth/login`, request).pipe(
      tap((response) => {
        this.accessToken = response.accessToken;
      }),
      switchMap(() => this.loadCurrentUser())
    );
  }

  loadCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${environment.apiBaseUrl}/auth/me`).pipe(
      tap((user) => this.userSubject.next(user))
    );
  }

  logout(): void {
    this.accessToken = null;
    this.userSubject.next(null);
  }

  token(): string | null {
    return this.accessToken;
  }

  isAuthenticated(): boolean {
    return this.accessToken !== null;
  }
}
