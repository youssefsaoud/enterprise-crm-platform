import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { AuthService } from '../../core/auth/auth.service';
import { Contact } from './contact.models';
import { ContactService } from './contact.service';
import { InteractionTimelineComponent } from '../interactions/interaction-timeline.component';

@Component({
  selector: 'app-contact-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, InteractionTimelineComponent],
  templateUrl: './contact-detail-page.component.html',
  styleUrls: ['./contact-detail-page.component.css']
})
export class ContactDetailPageComponent implements OnInit {
  readonly currentUser$ = this.authService.currentUser$;
  contact: Contact | null = null;
  loading = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly contactService: ContactService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigateByUrl('/contacts');
      return;
    }
    this.loading = true;
    this.contactService.get(id).subscribe({
      next: (contact) => {
        this.contact = contact;
        this.loading = false;
      },
      error: () => {
        this.error = 'Contact not found.';
        this.loading = false;
      }
    });
  }

  deleteContact(): void {
    if (!this.contact || !confirm('Delete this contact?')) {
      return;
    }
    this.contactService.delete(this.contact.id).subscribe({
      next: () => this.router.navigateByUrl('/contacts'),
      error: (error) => this.error = apiErrorMessage(error, 'Contact could not be deleted.')
    });
  }

  canDelete(role: string): boolean {
    return role === 'ADMIN' || role === 'MANAGER';
  }
}


