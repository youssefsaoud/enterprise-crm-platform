import { TestBed } from '@angular/core/testing';
import { OrganizationRelationshipsComponent } from './organization-relationships.component';

describe('OrganizationRelationshipsComponent', () => {
  it('shows both roles independently, including zero relationships and unavailable counts', () => {
    const fixture = TestBed.createComponent(OrganizationRelationshipsComponent);
    fixture.componentInstance.roles = ['CLIENT', 'VENDOR'];
    fixture.componentInstance.relationships = { companyId: 'demo', clientProjects: 0, vendorProjects: 3 };
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('in 0 projects');
    expect(fixture.nativeElement.textContent).toContain('in 3 projects');
    fixture.componentInstance.relationships = undefined; fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('counts unavailable');
    expect(fixture.nativeElement.textContent).not.toContain('in 0 projects');
  });
});
