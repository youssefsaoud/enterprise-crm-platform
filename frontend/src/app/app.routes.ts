import { Routes } from '@angular/router';

import { ActivitiesPageComponent } from './features/activities/activities-page.component';
import { CompaniesPageComponent } from './features/companies/companies-page.component';
import { CompanyDetailPageComponent } from './features/companies/company-detail-page.component';
import { CompanyFormPageComponent } from './features/companies/company-form-page.component';
import { ContactsPageComponent } from './features/contacts/contacts-page.component';
import { ContactDetailPageComponent } from './features/contacts/contact-detail-page.component';
import { ContactFormPageComponent } from './features/contacts/contact-form-page.component';
import { DashboardPageComponent } from './features/dashboard/dashboard-page.component';
import { LeadsPageComponent } from './features/leads/leads-page.component';
import { LeadFormPageComponent } from './features/leads/lead-form-page.component';
import { LeadDetailPageComponent } from './features/leads/lead-detail-page.component';
import { LoginPageComponent } from './features/auth/login-page.component';
import { OpportunitiesPageComponent } from './features/opportunities/opportunities-page.component';
import { OpportunityFormPageComponent } from './features/opportunities/opportunity-form-page.component';
import { OpportunityDetailPageComponent } from './features/opportunities/opportunity-detail-page.component';
import { StatusPageComponent } from './features/status/status-page.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardPageComponent, title: 'Dashboard', canActivate: [authGuard] },
  { path: 'companies', component: CompaniesPageComponent, title: 'Companies', canActivate: [authGuard] },
  { path: 'companies/new', component: CompanyFormPageComponent, title: 'New Company', canActivate: [authGuard] },
  { path: 'companies/:id', component: CompanyDetailPageComponent, title: 'Company Detail', canActivate: [authGuard] },
  { path: 'companies/:id/edit', component: CompanyFormPageComponent, title: 'Edit Company', canActivate: [authGuard] },
  { path: 'contacts', component: ContactsPageComponent, title: 'Contacts', canActivate: [authGuard] },
  { path: 'contacts/new', component: ContactFormPageComponent, title: 'New Contact', canActivate: [authGuard] },
  { path: 'contacts/:id', component: ContactDetailPageComponent, title: 'Contact Detail', canActivate: [authGuard] },
  { path: 'contacts/:id/edit', component: ContactFormPageComponent, title: 'Edit Contact', canActivate: [authGuard] },
  { path: 'leads', component: LeadsPageComponent, title: 'Leads', canActivate: [authGuard] },
  { path: 'leads/new', component: LeadFormPageComponent, title: 'New Lead', canActivate: [authGuard] },
  { path: 'leads/:id/edit', component: LeadFormPageComponent, title: 'Edit Lead', canActivate: [authGuard] },
  { path: 'leads/:id', component: LeadDetailPageComponent, title: 'Lead Detail', canActivate: [authGuard] },
  { path: 'opportunities', component: OpportunitiesPageComponent, title: 'Opportunities', canActivate: [authGuard] },
  { path: 'opportunities/new', component: OpportunityFormPageComponent, title: 'New Opportunity', canActivate: [authGuard] },
  { path: 'opportunities/:id/edit', component: OpportunityFormPageComponent, title: 'Edit Opportunity', canActivate: [authGuard] },
  { path: 'opportunities/:id', component: OpportunityDetailPageComponent, title: 'Opportunity Detail', canActivate: [authGuard] },
  { path: 'activities', component: ActivitiesPageComponent, title: 'Activities', canActivate: [authGuard] },
  { path: 'status', component: StatusPageComponent, title: 'API Status', canActivate: [authGuard] },
  { path: 'login', component: LoginPageComponent, title: 'Login' },
  { path: '**', redirectTo: 'dashboard' }
];





