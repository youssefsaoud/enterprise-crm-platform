import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-activities-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './activities-page.component.html'
})
export class ActivitiesPageComponent {}
