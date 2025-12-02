import { Component } from '@angular/core';

import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-contacts-page',
  standalone: true,
  imports: [EmptyStateComponent],
  templateUrl: './contacts-page.component.html'
})
export class ContactsPageComponent {}
