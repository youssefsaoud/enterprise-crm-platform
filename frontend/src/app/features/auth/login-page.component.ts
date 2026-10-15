import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="auth-page">
      <header class="auth-brand">
        <img class="brand-symbol" src="assets/brand/orqestra-symbol.png" alt="" width="42" height="42" />
        <span><strong>Orqestra</strong><small>Project &amp; Vendor Platform</small></span>
      </header>

      <div class="auth-content">
        <div class="auth-intro">
          <p>Project &amp; Vendor Platform</p>
          <h1>Every project, in harmony.</h1>
          <span>Sign in to continue managing projects, vendors, and delivery activity.</span>
        </div>

        <form class="auth-form" [formGroup]="form" (ngSubmit)="submit()" aria-label="Login form">
          <div class="form-heading">
            <p>Secure access</p>
            <h2>Sign in</h2>
          </div>
          <label>
            Work email
            <input type="email" formControlName="email" autocomplete="email" placeholder="name@company.com" />
          </label>
          <label>
            Password
            <input type="password" formControlName="password" autocomplete="current-password" placeholder="Enter your password" />
          </label>
          <p class="error-message" aria-live="polite">{{ errorMessage }}</p>
          <button type="submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>
        </form>
      </div>

      <footer>Authorized users only</footer>
    </section>
  `,
  styles: [`
    .auth-page {
      min-height: 100vh;
      display: grid;
      grid-template-rows: auto 1fr auto;
      padding: clamp(1.25rem, 4vw, 3rem);
      background: var(--color-bg);
    }
    .auth-brand {
      display: flex;
      align-items: center;
      gap: .75rem;
    }
    .brand-symbol {
      width: 2.65rem;
      height: 2.65rem;
      display: block;
      object-fit: contain;
    }
    .auth-brand > span:last-child {
      display: grid;
      line-height: 1.15;
    }
    .auth-brand strong {
      font-size: 1rem;
      font-weight: 650;
    }
    .auth-brand small {
      color: var(--color-text-tertiary) !important;
      font-size: .72rem;
    }
    .auth-content {
      width: min(66rem, 100%);
      align-self: center;
      justify-self: center;
      display: grid;
      grid-template-columns: minmax(0, 1fr) minmax(20rem, 27rem);
      align-items: center;
      gap: clamp(2rem, 8vw, 8rem);
      padding: 3rem 0;
    }
    .auth-intro p,
    .form-heading p {
      margin: 0 0 .65rem;
      color: #76b5ff;
      font-size: var(--text-xs);
      font-weight: 700;
      text-transform: uppercase;
    }
    .auth-intro h1 {
      max-width: 13ch;
      margin: 0 0 1rem;
      font-size: clamp(2.6rem, 6vw, 5rem);
      font-weight: 520;
      line-height: 1;
    }
    .auth-intro > span {
      display: block;
      max-width: 30rem;
      color: var(--color-text-secondary);
      font-size: var(--text-lg);
    }
    .auth-form {
      display: grid;
      gap: 1.1rem;
      padding: 1.5rem;
      border: 1px solid var(--color-border);
      border-radius: var(--radius-lg);
      background: var(--color-surface);
      box-shadow: var(--shadow-raised);
    }
    .form-heading h2 {
      margin: 0;
      font-size: 1.5rem;
    }
    .auth-form .error-message {
      min-height: 1.25rem;
      margin: 0;
      font-size: var(--text-sm);
    }
    .auth-form .error-message:empty {
      padding: 0;
    }
    .auth-form button {
      width: 100%;
      margin-top: .25rem;
    }
    footer {
      color: var(--color-text-tertiary);
      font-size: var(--text-xs);
    }
    @media (max-width: 760px) {
      .auth-content {
        grid-template-columns: 1fr;
        gap: 2rem;
      }
      .auth-intro h1 {
        font-size: 2.5rem;
      }
      .auth-intro > span {
        font-size: var(--text-md);
      }
    }
  `]
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
      this.form.markAllAsTouched();
      this.errorMessage = 'Enter a valid email address and password.';
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
