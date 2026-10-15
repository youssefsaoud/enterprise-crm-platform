import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, ViewChild } from '@angular/core';
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

        <form #spotlightCard class="auth-form" [formGroup]="form" (ngSubmit)="submit()" aria-label="Login form">
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
      min-height: 100dvh;
      overflow-x: clip;
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
      position: relative;
      isolation: isolate;
      width: min(66rem, 100%);
      align-self: center;
      justify-self: center;
      display: grid;
      grid-template-columns: minmax(0, 1fr) minmax(20rem, 27rem);
      align-items: center;
      gap: clamp(2rem, 8vw, 8rem);
      padding: 3rem 0;
    }
    .auth-content::after {
      content: '';
      position: absolute;
      z-index: -1;
      right: 0;
      width: min(36rem, 58vw);
      aspect-ratio: 1;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(35, 131, 255, .13) 0, rgba(35, 131, 255, .045) 42%, transparent 70%);
      filter: blur(18px);
      transform: translateX(28%);
      pointer-events: none;
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
      --spotlight-x: 50%;
      --spotlight-y: 42%;
      --spotlight-opacity: 0;
      position: relative;
      isolation: isolate;
      overflow: hidden;
      display: grid;
      gap: 1.1rem;
      padding: 1.5rem;
      border: 1px solid rgba(121, 132, 153, .28);
      border-radius: var(--radius-lg);
      background: rgba(23, 25, 31, .78);
      box-shadow: 0 26px 72px rgba(2, 8, 23, .44), inset 0 1px 0 rgba(255, 255, 255, .045);
      backdrop-filter: blur(18px) saturate(115%);
      -webkit-backdrop-filter: blur(18px) saturate(115%);
      transition: border-color var(--transition-base), box-shadow var(--transition-base);
    }
    .auth-form::before,
    .auth-form::after {
      content: '';
      position: absolute;
      inset: 0;
      z-index: -1;
      pointer-events: none;
      opacity: var(--spotlight-opacity);
      transition: opacity 320ms ease;
    }
    .auth-form::before {
      background: radial-gradient(15rem circle at var(--spotlight-x) var(--spotlight-y), rgba(35, 131, 255, .16), transparent 68%);
    }
    .auth-form::after {
      inset: -1px;
      border: 1px solid transparent;
      border-radius: inherit;
      background: radial-gradient(10rem circle at var(--spotlight-x) var(--spotlight-y), rgba(118, 181, 255, .68), transparent 72%) border-box;
      -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
    }
    .auth-form:hover {
      border-color: rgba(121, 132, 153, .38);
      box-shadow: 0 28px 78px rgba(2, 8, 23, .5), inset 0 1px 0 rgba(255, 255, 255, .06);
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
      .auth-content::after {
        right: 0;
        width: 28rem;
        transform: translateX(36%);
      }
      .auth-form {
        backdrop-filter: blur(12px) saturate(110%);
        -webkit-backdrop-filter: blur(12px) saturate(110%);
      }
    }
    @media (hover: none), (pointer: coarse) {
      .auth-form {
        --spotlight-opacity: .34;
        --spotlight-x: 70%;
        --spotlight-y: 15%;
      }
      .auth-form::after {
        opacity: 0;
      }
    }
    @media (prefers-reduced-motion: reduce) {
      .auth-form,
      .auth-form::before,
      .auth-form::after {
        transition: none;
      }
    }
  `]
})
export class LoginPageComponent implements AfterViewInit, OnDestroy {
  @ViewChild('spotlightCard') private spotlightCard?: ElementRef<HTMLFormElement>;

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });
  loading = false;
  errorMessage = '';

  private frameId: number | null = null;
  private pointerX = 0;
  private pointerY = 0;
  private removeSpotlightListeners?: () => void;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly ngZone: NgZone
  ) {}

  ngAfterViewInit(): void {
    const card = this.spotlightCard?.nativeElement;
    if (!card || window.matchMedia('(prefers-reduced-motion: reduce)').matches || window.matchMedia('(pointer: coarse)').matches) {
      return;
    }

    this.ngZone.runOutsideAngular(() => {
      const onPointerMove = (event: PointerEvent): void => {
        if (event.pointerType === 'touch') return;
        const bounds = card.getBoundingClientRect();
        this.pointerX = event.clientX - bounds.left;
        this.pointerY = event.clientY - bounds.top;
        card.style.setProperty('--spotlight-opacity', '1');
        if (this.frameId === null) {
          this.frameId = requestAnimationFrame(() => this.renderSpotlight(card));
        }
      };
      const onPointerLeave = (): void => card.style.setProperty('--spotlight-opacity', '0');

      card.addEventListener('pointermove', onPointerMove, { passive: true });
      card.addEventListener('pointerleave', onPointerLeave);
      this.removeSpotlightListeners = () => {
        card.removeEventListener('pointermove', onPointerMove);
        card.removeEventListener('pointerleave', onPointerLeave);
      };
    });
  }

  ngOnDestroy(): void {
    this.removeSpotlightListeners?.();
    if (this.frameId !== null) cancelAnimationFrame(this.frameId);
  }

  private renderSpotlight(card: HTMLFormElement): void {
    card.style.setProperty('--spotlight-x', `${this.pointerX}px`);
    card.style.setProperty('--spotlight-y', `${this.pointerY}px`);
    this.frameId = null;
  }

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
