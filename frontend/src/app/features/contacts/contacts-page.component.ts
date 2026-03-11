import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { Contact } from './contact.models';
import { ContactService } from './contact.service';

@Component({
  selector: 'app-contacts-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './contacts-page.component.html',
  styleUrls: ['./contacts-page.component.css']
})
export class ContactsPageComponent implements OnInit {
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly companyIdControl = new FormControl('', { nonNullable: true });
  readonly currentUser$ = this.authService.currentUser$;

  contacts: Contact[] = [];
  loading = false;
  error = '';
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly contactService: ContactService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const companyId = this.route.snapshot.queryParamMap.get('companyId');
    if (companyId) {
      this.companyIdControl.setValue(companyId, { emitEvent: false });
    }
    this.load();
    this.searchControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad());
    this.companyIdControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad());
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.contactService.search({
      search: this.searchControl.value,
      companyId: this.companyIdControl.value,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (result) => {
        this.contacts = result.content;
        this.totalElements = result.totalElements;
        this.totalPages = result.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load contacts.';
        this.loading = false;
      }
    });
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page += 1;
      this.load();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page -= 1;
      this.load();
    }
  }

  private resetAndLoad(): void {
    this.page = 0;
    this.load();
  }
}
