import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { apiErrorMessage } from '../../core/api/api-error';
import { AuthService } from '../../core/auth/auth.service';
import { Contact } from '../contacts/contact.models';
import { ContactService } from '../contacts/contact.service';
import { InteractionTimelineComponent } from '../interactions/interaction-timeline.component';
import { Company } from './company.models';
import { CompanyService } from './company.service';

@Component({
  selector: 'app-company-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, InteractionTimelineComponent],
  templateUrl: './company-detail-page.component.html',
  styleUrls: ['./company-detail-page.component.css']
})
export class CompanyDetailPageComponent implements OnInit {
  readonly currentUser$ = this.authService.currentUser$;
  company: Company | null = null;
  contacts: Contact[] = [];
  loading = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly companyService: CompanyService,
    private readonly contactService: ContactService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigateByUrl('/companies');
      return;
    }
    this.loading = true;
    this.companyService.get(id).subscribe({
      next: (company) => {
        this.company = company;
        this.loadContacts(company.id);
        this.loading = false;
      },
      error: () => {
        this.error = 'Company not found.';
        this.loading = false;
      }
    });
  }

  deleteCompany(): void {
    if (!this.company || !confirm('Delete this company? Contacts must be removed first.')) {
      return;
    }
    this.companyService.delete(this.company.id).subscribe({
      next: () => this.router.navigateByUrl('/companies'),
      error: (error) => this.error = apiErrorMessage(error, 'Company could not be deleted while contacts are attached.')
    });
  }

  private loadContacts(companyId: string): void {
    this.contactService.forCompany(companyId, 0, 5).subscribe({
      next: (result) => this.contacts = result.content,
      error: () => this.contacts = []
    });
  }

  canManage(role: string): boolean {
    return role === 'ADMIN' || role === 'MANAGER';
  }

  canDelete(role: string): boolean {
    return role === 'ADMIN';
  }
}



