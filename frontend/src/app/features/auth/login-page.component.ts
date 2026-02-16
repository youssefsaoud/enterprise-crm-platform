import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="page-header">
      <p>Access</p>
      <h1>Login</h1>
    </section>
    <form class="form-panel" [formGroup]="form" (ngSubmit)="submit()" aria-label="Login form">
      <label>
        Email
        <input type="email" formControlName="email" autocomplete="email" placeholder="admin@example.com" />
      </label>
      <label>
        Password
        <input type="password" formControlName="password" autocomplete="current-password" placeholder="Password" />
      </label>
      <p class="error-message" aria-live="polite">{{ errorMessage }}</p>
      <button type="submit" [disabled]="form.invalid || loading">{{ loading ? 'Signing in...' : 'Sign in' }}</button>
    </form>
  `
})
export class LoginPageComponent {
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });
  loading = false;
  errorMessage = '';

  constructor(private readonly fb: FormBuilder, private readonly authService: AuthService, private readonly router: Router) {}

  submit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Email and password are required.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigateByUrl('/dashboard'),
      error: () => {
        this.loading = false;
        this.errorMessage = 'Invalid email or password.';
      }
    });
  }
}
