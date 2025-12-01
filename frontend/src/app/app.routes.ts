import { Routes } from '@angular/router';

import { ActivitiesPageComponent } from './features/activities/activities-page.component';
import { CompaniesPageComponent } from './features/companies/companies-page.component';
import { ContactsPageComponent } from './features/contacts/contacts-page.component';
import { DashboardPageComponent } from './features/dashboard/dashboard-page.component';
import { LeadsPageComponent } from './features/leads/leads-page.component';
import { LoginPageComponent } from './features/auth/login-page.component';
import { OpportunitiesPageComponent } from './features/opportunities/opportunities-page.component';
import { StatusPageComponent } from './features/status/status-page.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardPageComponent, title: 'Dashboard' },
  { path: 'companies', component: CompaniesPageComponent, title: 'Companies' },
  { path: 'contacts', component: ContactsPageComponent, title: 'Contacts' },
  { path: 'leads', component: LeadsPageComponent, title: 'Leads' },
  { path: 'opportunities', component: OpportunitiesPageComponent, title: 'Opportunities' },
  { path: 'activities', component: ActivitiesPageComponent, title: 'Activities' },
  { path: 'status', component: StatusPageComponent, title: 'API Status' },
  { path: 'login', component: LoginPageComponent, title: 'Login' },
  { path: '**', redirectTo: 'dashboard' }
];
