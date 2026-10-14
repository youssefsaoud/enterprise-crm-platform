import { AsyncPipe, NgIf, NgSwitch, NgSwitchCase } from '@angular/common';
import { Component } from '@angular/core';
import { catchError, map, Observable, of, startWith } from 'rxjs';

import { HealthService } from '../../core/api/health.service';

interface StatusViewModel {
  state: 'loading' | 'success' | 'error';
  status?: string;
  service?: string;
  message?: string;
}

@Component({
  selector: 'app-status-page',
  standalone: true,
  imports: [AsyncPipe, NgIf, NgSwitch, NgSwitchCase],
  template: `
    <section class="page-header">
      <div><p>System</p><h1>API status</h1><span>Live connectivity between the Angular workspace and Spring Boot.</span></div>
    </section>
    <section *ngIf="status$ | async as vm" class="status-panel" [class.success]="vm.state === 'success'" [class.error]="vm.state === 'error'">
      <span class="status-indicator" [attr.data-state]="vm.state" aria-hidden="true"></span>
      <ng-container [ngSwitch]="vm.state">
        <div *ngSwitchCase="'loading'"><h2>Checking backend connection</h2><p>Waiting for the configured health endpoint.</p></div>
        <div *ngSwitchCase="'success'"><h2>{{ vm.service }} is {{ vm.status }}</h2><p>The application can reach the backend API.</p></div>
        <div *ngSwitchCase="'error'"><h2>Backend unavailable</h2><p class="error-copy">{{ vm.message }}</p></div>
      </ng-container>
    </section>
  `,
  styles: [`
    .page-header span { display: block; margin-top: .45rem; }
    .status-panel { display: grid; grid-template-columns: auto minmax(0, 1fr); align-items: start; gap: .9rem; max-width: 42rem; padding: 1.25rem; }
    .status-indicator { width: .65rem; height: .65rem; margin-top: .35rem; border-radius: 50%; background: var(--color-text-tertiary); box-shadow: 0 0 0 4px rgba(113, 113, 122, .12); }
    .status-indicator[data-state="loading"] { background: var(--color-warning); box-shadow: 0 0 0 4px var(--color-warning-soft); }
    .status-indicator[data-state="success"] { background: var(--color-success); box-shadow: 0 0 0 4px var(--color-success-soft); }
    .status-indicator[data-state="error"] { background: var(--color-danger); box-shadow: 0 0 0 4px var(--color-danger-soft); }
    h2 { margin: 0 0 .35rem; font-size: 1rem; }
    p { margin: 0; color: var(--color-text-secondary); }
    .error-copy { color: var(--color-danger); }
  `]
})
export class StatusPageComponent {
  readonly status$: Observable<StatusViewModel> = this.healthService.getHealth().pipe(
    map((response): StatusViewModel => ({ state: 'success', status: response.status, service: response.service })),
    startWith({ state: 'loading' } satisfies StatusViewModel),
    catchError(() => of({ state: 'error', message: 'The configured backend endpoint is not responding.' } satisfies StatusViewModel))
  );

  constructor(private readonly healthService: HealthService) {}
}
