import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AuthService } from './core/auth/auth.service';
import { AppShellComponent } from './layout/app-shell.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AsyncPipe, NgIf, RouterOutlet, AppShellComponent],
  template: `
    <ng-container *ngIf="currentUser$ | async; else publicLayout">
      <app-shell><router-outlet></router-outlet></app-shell>
    </ng-container>
    <ng-template #publicLayout>
      <main class="public-layout"><router-outlet></router-outlet></main>
    </ng-template>
  `,
  styles: [`
    .public-layout {
      min-height: 100vh;
      background: var(--color-bg);
      color: var(--color-text);
    }
  `]
})
export class AppComponent {
  readonly currentUser$ = this.authService.currentUser$;

  constructor(private readonly authService: AuthService) {}
}
