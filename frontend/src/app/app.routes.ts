import { Routes } from '@angular/router';

import { ActivitiesPageComponent } from './features/activities/activities-page.component';
import { CompaniesPageComponent } from './features/companies/companies-page.component';
import { ContactsPageComponent } from './features/contacts/contacts-page.component';
import { DashboardPageComponent } from './features/dashboard/dashboard-page.component';
import { LeadsPageComponent } from './features/leads/leads-page.component';
import { LoginPageComponent } from './features/auth/login-page.component';
import { OpportunitiesPageComponent } from './features/opportunities/opportunities-page.component';
import { StatusPageComponent } from './features/status/status-page.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardPageComponent, title: 'Dashboard', canActivate: [authGuard] },
  { path: 'companies', component: CompaniesPageComponent, title: 'Companies', canActivate: [authGuard] },
  { path: 'contacts', component: ContactsPageComponent, title: 'Contacts', canActivate: [authGuard] },
  { path: 'leads', component: LeadsPageComponent, title: 'Leads', canActivate: [authGuard] },
  { path: 'opportunities', component: OpportunitiesPageComponent, title: 'Opportunities', canActivate: [authGuard] },
  { path: 'activities', component: ActivitiesPageComponent, title: 'Activities', canActivate: [authGuard] },
  { path: 'status', component: StatusPageComponent, title: 'API Status', canActivate: [authGuard] },
  { path: 'login', component: LoginPageComponent, title: 'Login' },
  { path: '**', redirectTo: 'dashboard' }
];

