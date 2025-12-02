import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-leads-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './leads-page.component.html'
})
export class LeadsPageComponent {}
