import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './dashboard-page.component.html'
})
export class DashboardPageComponent {}
