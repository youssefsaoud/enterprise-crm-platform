import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UserRole } from '../../core/auth/auth.models';

export interface Employee {
  id: string; fullName: string; email: string; role: UserRole; active: boolean; createdAt: string;
}
export interface Invitation {
  id: string; fullName: string; email: string; role: UserRole;
  status: 'PENDING' | 'EXPIRED' | 'REVOKED' | 'ACCEPTED';
  deliveryStatus: 'SENDING' | 'SENT' | 'FAILED' | 'PREVIEW';
  createdAt: string; expiresAt: string; lastSentAt: string; acceptedAt: string | null; deliveryAttempts: number;
}
export interface InviteRequest { fullName: string; email: string; role: 'MANAGER' | 'SALES_REP'; }
export interface DeliveryResult { invitation: Invitation; submitted: boolean; message: string; }
export interface Welcome { fullName: string; role: UserRole; expiresAt: string; }

@Injectable({ providedIn: 'root' })
export class TeamService {
  private readonly api = environment.apiBaseUrl;
  constructor(private readonly http: HttpClient) {}
  employees() { return this.http.get<Employee[]>(`${this.api}/admin/employees`); }
  invitations() { return this.http.get<Invitation[]>(`${this.api}/admin/invitations`); }
  invite(request: InviteRequest) { return this.http.post<DeliveryResult>(`${this.api}/admin/invitations`, request); }
  resend(id: string) { return this.http.post<DeliveryResult>(`${this.api}/admin/invitations/${id}/resend`, {}); }
  revoke(id: string) { return this.http.post<Invitation>(`${this.api}/admin/invitations/${id}/revoke`, {}); }
  validate(token: string) { return this.http.post<Welcome>(`${this.api}/invitations/validate`, { token }); }
  accept(token: string, password: string) { return this.http.post<void>(`${this.api}/invitations/accept`, { token, password }); }
}

export function employeeRoleLabel(role: UserRole): string {
  return role === 'ADMIN' ? 'Admin' : role === 'MANAGER' ? 'Manager' : 'Associate';
}
