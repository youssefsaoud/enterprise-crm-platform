import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { apiErrorMessage } from '../../core/api/api-error';
import { Employee, Invitation, TeamService, employeeRoleLabel } from './team.service';

@Component({
  standalone: true, selector: 'app-team-page',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './team-page.component.html', styleUrls: ['./team-page.component.css']
})
export class TeamPageComponent implements OnInit {
  @ViewChild('inviteDialog', { static: true }) dialog!: ElementRef<HTMLDialogElement>;
  readonly roleLabel = employeeRoleLabel;
  readonly form = this.fb.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(120), Validators.pattern(/\S/)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(180)]],
    role: this.fb.nonNullable.control<'MANAGER' | 'SALES_REP'>('SALES_REP', Validators.required)
  });
  employees: Employee[] = [];
  invitations: Invitation[] = [];
  loading = true;
  saving = false;
  busyId = '';
  search = '';
  status = '';
  error = '';
  dialogError = '';
  message = '';
  constructor(private readonly fb: FormBuilder, private readonly service: TeamService) {}
  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    forkJoin({ employees: this.service.employees(), invitations: this.service.invitations() }).subscribe({
      next: data => { this.employees = data.employees; this.invitations = data.invitations; this.loading = false; },
      error: error => { this.error = apiErrorMessage(error, 'Team could not be loaded.'); this.loading = false; }
    });
  }
  get activeCount() { return this.employees.filter(e => e.active).length; }
  get pendingCount() { return this.invitations.filter(i => i.status === 'PENDING').length; }
  get expiredCount() { return this.invitations.filter(i => i.status === 'EXPIRED').length; }
  get rows() {
    const employees = this.employees.map(e => ({ ...e, status: e.active ? 'ACTIVE' : 'INACTIVE', sentAt: null as string | null, invitation: null as Invitation | null }));
    const invitations = this.invitations.filter(i => i.status !== 'ACCEPTED')
      .map(i => ({ ...i, active: false, sentAt: i.lastSentAt as string | null, invitation: i as Invitation | null }));
    const search = this.search.trim().toLowerCase();
    return [...employees, ...invitations].filter(r => (!this.status || r.status === this.status)
      && (!search || `${r.fullName} ${r.email}`.toLowerCase().includes(search)));
  }
  statusLabel(status: string): string {
    return ({ ACTIVE: 'Active', INACTIVE: 'Inactive', PENDING: 'Pending invitation', EXPIRED: 'Expired', REVOKED: 'Revoked' } as Record<string, string>)[status] || status;
  }
  deliveryLabel(status: string): string {
    return ({ SENT: 'Submitted to mail server', FAILED: 'Email failed - resend to retry', PREVIEW: 'Local preview - no email sent', SENDING: 'Delivery processing' } as Record<string, string>)[status] || status;
  }
  openInvite(): void {
    this.form.reset({ fullName: '', email: '', role: 'SALES_REP' });
    this.dialogError = ''; this.dialog.nativeElement.showModal();
  }
  closeInvite(): void { if (!this.saving) this.dialog.nativeElement.close(); }
  invite(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.dialogError = ''; this.error = ''; this.message = '';
    this.service.invite(this.form.getRawValue()).subscribe({
      next: result => { this.saving = false; this.closeInvite(); this.message = result.message; this.load(); },
      error: error => {
        this.saving = false;
        if (error.status === 502 && error.error?.invitation) {
          this.closeInvite(); this.error = error.error.message; this.load();
        } else this.dialogError = apiErrorMessage(error, 'Invitation could not be created.');
      }
    });
  }
  resend(id: string): void {
    this.busyId = id; this.error = ''; this.message = '';
    this.service.resend(id).subscribe({
      next: result => { this.busyId = ''; this.message = result.message; this.load(); },
      error: error => { this.busyId = ''; this.error = apiErrorMessage(error, 'Invitation could not be resent.'); this.load(); }
    });
  }
  revoke(id: string): void {
    this.busyId = id; this.error = ''; this.message = '';
    this.service.revoke(id).subscribe({
      next: () => { this.busyId = ''; this.message = 'Invitation revoked. Its activation link no longer works.'; this.load(); },
      error: error => { this.busyId = ''; this.error = apiErrorMessage(error, 'Invitation could not be revoked.'); }
    });
  }
}
