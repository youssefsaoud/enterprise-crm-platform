import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { Project, ProjectStatus } from './project.models';
import { ProjectService } from './project.service';
import { AuthService } from '../../core/auth/auth.service';
@Component({ selector: 'app-projects-page', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './projects-page.component.html', styleUrls: ['./projects-page.component.css'] })
export class ProjectsPageComponent implements OnInit {
  readonly searchControl = new FormControl('', { nonNullable: true }); readonly statusControl = new FormControl<ProjectStatus | ''>('', { nonNullable: true }); readonly statuses: ProjectStatus[] = ['PLANNED','IN_PROGRESS','ON_HOLD','COMPLETED','CANCELLED'];
  projects: Project[] = []; loading = false; error = ''; page = 0; size = 10; totalElements = 0; totalPages = 0;
  readonly currentUser$ = this.authService.currentUser$;
  constructor(private readonly projectService: ProjectService, private readonly authService: AuthService) {}
  ngOnInit(): void { this.load(); this.searchControl.valueChanges.pipe(debounceTime(250), distinctUntilChanged()).subscribe(() => this.resetAndLoad()); this.statusControl.valueChanges.subscribe(() => this.resetAndLoad()); }
  load(): void { this.loading = true; this.error = ''; this.projectService.search({ search: this.searchControl.value, status: this.statusControl.value || undefined, page: this.page, size: this.size }).subscribe({ next: result => { this.projects = result.content; this.totalElements = result.totalElements; this.totalPages = result.totalPages; this.loading = false; }, error: () => { this.error = 'Could not load projects.'; this.loading = false; } }); }
  nextPage(): void { if (this.page + 1 < this.totalPages) { this.page += 1; this.load(); } } previousPage(): void { if (this.page > 0) { this.page -= 1; this.load(); } } statusLabel(status: ProjectStatus): string { return status.replace('_', ' '); } canManage(role: string): boolean { return role === 'ADMIN' || role === 'MANAGER'; } private resetAndLoad(): void { this.page = 0; this.load(); }
}