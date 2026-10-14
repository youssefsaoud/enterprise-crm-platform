import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { UserService } from '../../core/users/user.service';
import { CompanyService } from '../companies/company.service';
import { ProjectDocumentService } from './project-document.service';
import { ProjectDetailPageComponent } from './project-detail-page.component';
import { ProjectVendor } from './project-vendor.models';
import { ProjectVendorService } from './project-vendor.service';
import { ProjectService } from './project.service';
import { QuotationService } from './quotation.service';

describe('ProjectDetailPageComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectDetailPageComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: { currentUser$: of({ id: 'user-1', fullName: 'Admin', role: 'ADMIN' }), currentUser: () => ({ role: 'ADMIN' }) } },
        { provide: UserService, useValue: {} },
        { provide: CompanyService, useValue: {} },
        { provide: ProjectDocumentService, useValue: {} },
        { provide: ProjectVendorService, useValue: {} },
        { provide: ProjectService, useValue: {} },
        { provide: QuotationService, useValue: {} }
      ]
    }).compileComponents();
  });

  it('preserves workspace tab state and filters vendors without mutating assignments', () => {
    const component = TestBed.createComponent(ProjectDetailPageComponent).componentInstance;
    component.vendors = [
      { id: 'vendor-1', status: 'SOW_SENT', vendorName: 'Vendor One' },
      { id: 'vendor-2', status: 'QUOTE_RECEIVED', vendorName: 'Vendor Two' }
    ] as ProjectVendor[];

    component.setTab('vendors');
    component.vendorStatusFilter = 'QUOTE_RECEIVED';

    expect(component.activeTab).toBe('vendors');
    expect(component.filteredVendors.map(vendor => vendor.id)).toEqual(['vendor-2']);
    expect(component.vendors.length).toBe(2);
  });
});
