import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Company } from './company.models';
import { CompanyFormPageComponent } from './company-form-page.component';
import { CompanyService } from './company.service';

describe('CompanyFormPageComponent', () => {
  let fixture: ComponentFixture<CompanyFormPageComponent>;
  let component: CompanyFormPageComponent;
  let companyService: jasmine.SpyObj<CompanyService>;

  async function configure(params: Record<string, string> = {}, query: Record<string, string> = {}): Promise<void> {
    companyService = jasmine.createSpyObj<CompanyService>('CompanyService', ['get', 'create', 'update']);

    await TestBed.configureTestingModule({
      imports: [CompanyFormPageComponent, RouterTestingModule],
      providers: [
        { provide: CompanyService, useValue: companyService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap(params),
              queryParamMap: convertToParamMap(query)
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CompanyFormPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('preselects only the client role for the client creation route', async () => {
    await configure({}, { role: 'CLIENT' });

    expect(component.pageTitle).toBe('New client');
    expect(component.form.controls.roles.value).toEqual(['CLIENT']);
    expect(fixture.nativeElement.querySelectorAll('.role-option.selected').length).toBe(1);
  });

  it('preselects only the vendor role for the vendor creation route', async () => {
    await configure({}, { role: 'VENDOR' });

    expect(component.pageTitle).toBe('New vendor');
    expect(component.form.controls.roles.value).toEqual(['VENDOR']);
  });

  it('allows an organization to have both roles', async () => {
    await configure({}, { role: 'CLIENT' });

    component.toggleRole('VENDOR', true);
    fixture.detectChanges();

    expect(component.form.controls.roles.value).toEqual(['CLIENT', 'VENDOR']);
    expect(fixture.nativeElement.querySelectorAll('.role-option.selected').length).toBe(2);
  });

  it('preserves existing roles when editing an organization', async () => {
    const company: Company = {
      id: 'company-1',
      name: 'Northstar Group',
      industry: 'Technology',
      website: null,
      phone: null,
      email: null,
      address: null,
      roles: ['CLIENT', 'VENDOR'],
      createdAt: '2026-10-01T10:00:00Z',
      updatedAt: '2026-10-01T10:00:00Z'
    };
    companyService = jasmine.createSpyObj<CompanyService>('CompanyService', ['get', 'create', 'update']);
    companyService.get.and.returnValue(of(company));

    await TestBed.configureTestingModule({
      imports: [CompanyFormPageComponent, RouterTestingModule],
      providers: [
        { provide: CompanyService, useValue: companyService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: company.id }),
              queryParamMap: convertToParamMap({ role: 'CLIENT' })
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CompanyFormPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.pageTitle).toBe('Edit organization');
    expect(component.form.controls.roles.value).toEqual(['CLIENT', 'VENDOR']);
    expect(companyService.get).toHaveBeenCalledWith(company.id);
  });
});
