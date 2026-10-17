import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import { Project, ProjectStatus } from './project.models';
import { ProjectService, ProjectStatusCounts } from './project.service';
import { AuthService } from '../../core/auth/auth.service';
import { MoneyComponent } from '../../shared/money.component';

@Component({ selector: 'app-projects-page', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink, MoneyComponent], templateUrl: './projects-page.component.html', styleUrls: ['./projects-page.component.css'] })
export class ProjectsPageComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private listRequest?: Subscription;
  private countRequest?: Subscription;
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly statusControl = new FormControl<ProjectStatus | ''>('', { nonNullable: true });
  counts: ProjectStatusCounts | null = null;
  countsLoading = false;
  projects: Project[] = []; loading = false; error = ''; page = 0; size = 10; totalElements = 0; totalPages = 0;
  readonly currentUser$ = this.authService.currentUser$;
  constructor(private readonly projectService: ProjectService, private readonly authService: AuthService, private readonly route: ActivatedRoute) {}
  ngOnInit(): void {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      this.statusControl.setValue((params.get('status') || '') as ProjectStatus | '', { emitEvent: false });
      this.resetAndLoad();
    });
    this.searchControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef)).subscribe(() => this.resetAndLoad());
    this.statusControl.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.resetAndLoad());
  }
  load(): void {
    this.listRequest?.unsubscribe(); this.countRequest?.unsubscribe();
    this.loading = true; this.error = ''; this.countsLoading = true;
    const filters = { search: this.searchControl.value };
    this.listRequest = this.projectService.search({ ...filters, status: this.statusControl.value || undefined, page: this.page, size: this.size }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: result => { this.projects = result.content; this.totalElements = result.totalElements; this.totalPages = result.totalPages; this.loading = false; },
      error: () => { this.error = 'Could not load projects.'; this.loading = false; }
    });
    this.countRequest = this.projectService.statusCounts(filters).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: counts => { this.counts = counts; this.countsLoading = false; },
      error: () => { this.counts = null; this.countsLoading = false; }
    });
  }
  nextPage(): void { if (this.page + 1 < this.totalPages) { this.page += 1; this.load(); } }
  previousPage(): void { if (this.page > 0) { this.page -= 1; this.load(); } }
  statusLabel(status: ProjectStatus): string { return this.counts?.statuses.find(item => item.status === status)?.label || status.replace(/_/g, ' ').toLowerCase(); }
  canManage(role: string): boolean { return role === 'ADMIN' || role === 'MANAGER'; }
  private resetAndLoad(): void { this.page = 0; this.load(); }
}
