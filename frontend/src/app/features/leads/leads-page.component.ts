import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';

import { Lead, LeadSource, LeadStatus } from './lead.models';
import { LeadService } from './lead.service';

@Component({
  selector: 'app-leads-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './leads-page.component.html',
  styleUrls: ['./leads-page.component.css']
})
export class LeadsPageComponent implements OnInit {
  readonly keywordControl = new FormControl('', { nonNullable: true });
  readonly statusControl = new FormControl<LeadStatus | ''>('', { nonNullable: true });
  readonly sourceControl = new FormControl<LeadSource | ''>('', { nonNullable: true });
  readonly statuses: LeadStatus[] = ['NEW', 'CONTACTED', 'QUALIFIED', 'UNQUALIFIED'];
  readonly sources: LeadSource[] = ['WEBSITE', 'REFERRAL', 'EMAIL', 'EVENT', 'OTHER'];

  leads: Lead[] = [];
  loading = false;
  error = '';
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  constructor(private readonly leadService: LeadService) {}

  ngOnInit(): void {
    this.load();
    this.keywordControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad());
    this.statusControl.valueChanges.subscribe(() => this.resetAndLoad());
    this.sourceControl.valueChanges.subscribe(() => this.resetAndLoad());
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.leadService.search({
      keyword: this.keywordControl.value,
      status: this.statusControl.value,
      source: this.sourceControl.value,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (result) => {
        this.leads = result.content;
        this.totalElements = result.totalElements;
        this.totalPages = result.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load leads.';
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

  label(value: string): string { return value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, character => character.toUpperCase()); }

  private resetAndLoad(): void {
    this.page = 0;
    this.load();
  }
}
