import { NgIf } from '@angular/common';
import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { OrchestrationScene } from './orchestration-scene';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule],
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements AfterViewInit, OnDestroy {
  @ViewChild('sceneCanvas', { static: true }) private sceneCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('visualPanel', { static: true }) private visualPanel?: ElementRef<HTMLElement>;
  @ViewChild('loginCard', { static: true }) private loginCard?: ElementRef<HTMLFormElement>;

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });
  loading = false;
  errorMessage = '';

  private scene?: OrchestrationScene;
  private spotlightFrameId: number | null = null;
  private spotlightX = 0;
  private spotlightY = 0;
  private removeSpotlightListeners?: () => void;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly ngZone: NgZone
  ) {}

  ngAfterViewInit(): void {
    const canvas = this.sceneCanvas?.nativeElement;
    const panel = this.visualPanel?.nativeElement;
    if (canvas && panel) {
      this.ngZone.runOutsideAngular(() => {
        this.scene = new OrchestrationScene(canvas, panel);
        void this.scene.start();
      });
    }

    this.bindSpotlight();
  }

  ngOnDestroy(): void {
    this.scene?.destroy();
    this.removeSpotlightListeners?.();
    if (this.spotlightFrameId !== null) cancelAnimationFrame(this.spotlightFrameId);
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

  private bindSpotlight(): void {
    const card = this.loginCard?.nativeElement;
    if (!card || window.matchMedia('(prefers-reduced-motion: reduce)').matches || window.matchMedia('(pointer: coarse)').matches) {
      return;
    }

    this.ngZone.runOutsideAngular(() => {
      const onPointerMove = (event: PointerEvent): void => {
        if (event.pointerType === 'touch') return;
        const bounds = card.getBoundingClientRect();
        this.spotlightX = event.clientX - bounds.left;
        this.spotlightY = event.clientY - bounds.top;
        card.style.setProperty('--card-glow-opacity', '1');
        if (this.spotlightFrameId === null) {
          this.spotlightFrameId = requestAnimationFrame(() => this.renderSpotlight(card));
        }
      };
      const onPointerLeave = (): void => card.style.setProperty('--card-glow-opacity', '0');

      card.addEventListener('pointermove', onPointerMove, { passive: true });
      card.addEventListener('pointerleave', onPointerLeave);
      this.removeSpotlightListeners = () => {
        card.removeEventListener('pointermove', onPointerMove);
        card.removeEventListener('pointerleave', onPointerLeave);
      };
    });
  }

  private renderSpotlight(card: HTMLFormElement): void {
    card.style.setProperty('--card-pointer-x', `${this.spotlightX}px`);
    card.style.setProperty('--card-pointer-y', `${this.spotlightY}px`);
    this.spotlightFrameId = null;
  }
}
