import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  template: `
    <section class="empty-state">
      <h2>{{ title }}</h2>
      <p>{{ message }}</p>
    </section>
  `,
  styles: [`
    .empty-state {
      border: 1px dashed #b7c3d6;
      border-radius: 8px;
      padding: 1.25rem;
      background: #ffffff;
    }

    h2 {
      font-size: 1rem;
      margin: 0 0 0.4rem;
    }

    p {
      color: #5f6b7a;
      margin: 0;
    }
  `]
})
export class EmptyStateComponent {
  @Input({ required: true }) title = '';
  @Input({ required: true }) message = '';
}
