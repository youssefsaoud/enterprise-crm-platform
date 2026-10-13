export type LeadStatus = 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'UNQUALIFIED' | 'CONVERTED';
export type LeadSource = 'WEBSITE' | 'REFERRAL' | 'EMAIL' | 'EVENT' | 'OTHER';
export type LeadEventType = 'CREATED' | 'UPDATED' | 'ASSIGNED' | 'UNASSIGNED' | 'STATUS_CHANGED' | 'DELETED';

export interface Lead {
  id: string;
  firstName: string;
  lastName: string;
  email?: string | null;
  phone?: string | null;
  companyName?: string | null;
  companyId?: string | null;
  linkedCompanyName?: string | null;
  source: LeadSource;
  status: LeadStatus;
  assignedToUserId?: string | null;
  assignedToName?: string | null;
  createdByUserId: string;
  createdByName: string;
  assignedAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface LeadPayload {
  firstName: string;
  lastName: string;
  email?: string | null;
  phone?: string | null;
  companyName?: string | null;
  companyId?: string | null;
  source: LeadSource;
}

export interface LeadSearchParams {
  keyword?: string;
  status?: LeadStatus | '';
  source?: LeadSource | '';
  assigneeId?: string;
  companyId?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface LeadEvent {
  id: string;
  eventType: LeadEventType;
  oldValue?: string | null;
  newValue?: string | null;
  performedByUserId: string;
  performedByName: string;
  createdAt: string;
}

export interface UserSummary {
  id: string;
  fullName: string;
  email: string;
  role: string;
}
