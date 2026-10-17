import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CompanyRole } from './company.models';

export interface OrganizationRelationships { companyId: string; clientProjects: number; vendorProjects: number; }

@Component({
  selector: 'app-organization-relationships', standalone: true, imports: [CommonModule],
  template: `<span class="relationships" title="Distinct projects visible to your account"><span *ngIf="roles.includes('CLIENT')">Client <small>{{ relationships ? 'in ' + relationships.clientProjects + ' projects' : '· counts unavailable' }}</small></span><span *ngIf="roles.includes('VENDOR')">Vendor <small>{{ relationships ? 'in ' + relationships.vendorProjects + ' projects' : '· counts unavailable' }}</small></span></span>`,
  styles: [`:host{display:block}.relationships{display:flex;flex-wrap:wrap;gap:.4rem}.relationships>span{display:inline-flex;flex-wrap:wrap;gap:.35rem;align-items:baseline;border:1px solid var(--color-border);border-radius:4px;padding:.2rem .4rem;font-size:.78rem;color:var(--color-text)}small{font-size:inherit;color:var(--color-text-secondary);font-variant-numeric:tabular-nums}`]
})
export class OrganizationRelationshipsComponent {
  @Input() roles: CompanyRole[] = [];
  @Input() relationships?: OrganizationRelationships;
}
