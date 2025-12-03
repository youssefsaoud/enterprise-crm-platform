import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-opportunities-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './opportunities-page.component.html'
})
export class OpportunitiesPageComponent {}
