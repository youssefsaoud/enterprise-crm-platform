import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { apiErrorMessage } from '../../core/api/api-error';
import { AuthService } from '../../core/auth/auth.service';
import { CompanyService } from '../companies/company.service';
import { Company } from '../companies/company.models';
import { OutreachStatus, ProjectVendor } from './project-vendor.models';
import { ProjectVendorService } from './project-vendor.service';
import { Project, ProjectStatus } from './project.models';
import { ProjectService } from './project.service';

@Component({
  selector: 'app-project-detail-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.css']
})
export class ProjectDetailPageComponent implements OnInit {
  readonly currentUser$ = this.authService.currentUser$;
  readonly statuses: OutreachStatus[] = ['NOT_CONTACTED', 'NO_CONTACT_REACHED', 'SOW_SENT', 'QUOTE_IN_PROCESS', 'QUOTE_RECEIVED', 'QUOTE_DECLINED'];
  project: Project | null = null;
  vendors: ProjectVendor[] = [];
  vendorDirectory: Company[] = [];
  loading = false;
  vendorLoading = false;
  error = '';
  vendorError = '';
  readonly vendorForm = new FormGroup({
    vendorId: new FormControl('', { nonNullable: true }),
    status: new FormControl<OutreachStatus>('NOT_CONTACTED', { nonNullable: true }),
    nextFollowUpDate: new FormControl('', { nonNullable: true }),
    notes: new FormControl('', { nonNullable: true })
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly projectService: ProjectService,
    private readonly projectVendorService: ProjectVendorService,
    private readonly companyService: CompanyService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      void this.router.navigateByUrl('/projects');
      return;
    }
    this.loading = true;
    this.projectService.get(id).subscribe({
      next: project => {
        this.project = project;
        this.loading = false;
        this.loadVendors();
        this.loadVendorDirectory();
      },
      error: error => {
        this.error = apiErrorMessage(error, 'Project not found.');
        this.loading = false;
      }
    });
  }

  deleteProject(): void {
    if (!this.project || !confirm('Delete this project?')) return;
    this.projectService.delete(this.project.id).subscribe({
      next: () => void this.router.navigateByUrl('/projects'),
      error: error => this.error = apiErrorMessage(error, 'Project could not be deleted.')
    });
  }

  loadVendors(): void {
    if (!this.project) return;
    this.vendorLoading = true;
    this.projectVendorService.search(this.project.id).subscribe({
      next: page => {
        this.vendors = page.content;
        this.vendorLoading = false;
      },
      error: error => {
        this.vendorError = apiErrorMessage(error, 'Project vendors could not be loaded.');
        this.vendorLoading = false;
      }
    });
  }

  loadVendorDirectory(): void {
    this.companyService.search({ role: 'VENDOR', size: 100, sort: 'name,asc' }).subscribe({
      next: page => this.vendorDirectory = page.content
    });
  }

  addVendor(): void {
    if (!this.project || !this.vendorForm.controls.vendorId.value) return;
    this.vendorError = '';
    this.projectVendorService.add(this.project.id, this.vendorForm.getRawValue()).subscribe({
      next: vendor => {
        this.vendors = [...this.vendors, vendor].sort((left, right) => left.vendorName.localeCompare(right.vendorName));
        this.vendorForm.reset({ vendorId: '', status: 'NOT_CONTACTED', nextFollowUpDate: '', notes: '' });
      },
      error: error => this.vendorError = apiErrorMessage(error, 'Vendor could not be assigned.')
    });
  }

  updateStatus(vendor: ProjectVendor, status: OutreachStatus): void {
    this.projectVendorService.update(vendor.id, { status }).subscribe({
      next: updated => vendor.status = updated.status,
      error: error => this.vendorError = apiErrorMessage(error, 'Vendor status could not be updated.')
    });
  }

  removeVendor(vendor: ProjectVendor): void {
    if (!confirm(`Remove ${vendor.vendorName} from this project?`)) return;
    this.projectVendorService.remove(vendor.id).subscribe({
      next: () => this.vendors = this.vendors.filter(item => item.id !== vendor.id),
      error: error => this.vendorError = apiErrorMessage(error, 'Vendor assignment could not be removed.')
    });
  }

  canManage(role: string): boolean { return role === 'ADMIN' || role === 'MANAGER'; }
  statusLabel(status: ProjectStatus): string { return status.replaceAll('_', ' '); }
  outreachLabel(status: OutreachStatus): string { return status.replaceAll('_', ' '); }
  isOverdue(vendor: ProjectVendor): boolean { return !!vendor.nextFollowUpDate && vendor.nextFollowUpDate < new Date().toISOString().slice(0, 10); }
}
