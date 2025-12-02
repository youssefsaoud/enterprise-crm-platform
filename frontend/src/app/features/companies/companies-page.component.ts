import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-companies-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './companies-page.component.html'
})
export class CompaniesPageComponent {}
