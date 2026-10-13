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
      <p>Development</p>
      <h1>API Status</h1>
    </section>

    <section *ngIf="status$ | async as vm" class="status-panel" [class.success]="vm.state === 'success'" [class.error]="vm.state === 'error'">
      <ng-container [ngSwitch]="vm.state">
        <ng-container *ngSwitchCase="'loading'">
          <h2>Checking backend connection</h2>
          <p>Waiting for the Spring Boot health endpoint.</p>
        </ng-container>
        <ng-container *ngSwitchCase="'success'">
          <h2>{{ vm.service }} is {{ vm.status }}</h2>
          <p>Angular can reach the Spring Boot API through the configured health endpoint.</p>
        </ng-container>
        <ng-container *ngSwitchCase="'error'">
          <h2>Backend unavailable</h2>
          <p class="error-message">{{ vm.message }}</p>
        </ng-container>
      </ng-container>
    </section>
  `,
  styles: [`
    .status-panel {
      border: 1px solid #dfe5ef;
      border-radius: 8px;
      background: #ffffff;
      padding: 1.25rem;
    }

    .status-panel.success {
      border-color: #1f9d55;
    }

    .status-panel.error {
      border-color: #b42318;
    }

    h2 {
      font-size: 1.1rem;
      margin: 0 0 0.35rem;
    }

    p {
      margin: 0;
      color: #5f6b7a;
    }
  `]
})
export class StatusPageComponent {
  readonly status$: Observable<StatusViewModel> = this.healthService.getHealth().pipe(
    map((response): StatusViewModel => ({
      state: 'success',
      status: response.status,
      service: response.service
    })),
    startWith({ state: 'loading' } satisfies StatusViewModel),
    catchError(() => of({
      state: 'error',
      message: 'Start the backend on http://localhost:8080 and try again.'
    } satisfies StatusViewModel))
  );

  constructor(private readonly healthService: HealthService) {}
}
