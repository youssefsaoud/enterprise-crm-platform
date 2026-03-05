import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { Company } from './company.models';
import { CompanyService } from './company.service';

@Component({
  selector: 'app-companies-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './companies-page.component.html',
  styleUrls: ['./companies-page.component.css']
})
export class CompaniesPageComponent implements OnInit {
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly industryControl = new FormControl('', { nonNullable: true });
  readonly currentUser$ = this.authService.currentUser$;

  companies: Company[] = [];
  loading = false;
  error = '';
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  constructor(private readonly companyService: CompanyService, private readonly authService: AuthService) {}

  ngOnInit(): void {
    this.load();
    this.searchControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad());
    this.industryControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad());
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.companyService.search({
      search: this.searchControl.value,
      industry: this.industryControl.value,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (result) => {
        this.companies = result.content;
        this.totalElements = result.totalElements;
        this.totalPages = result.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load companies.';
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

  canManage(role: string): boolean {
    return role === 'ADMIN' || role === 'MANAGER';
  }

  private resetAndLoad(): void {
    this.page = 0;
    this.load();
  }
}
