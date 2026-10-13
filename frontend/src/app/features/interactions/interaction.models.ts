export type InteractionType = 'CALL' | 'EMAIL' | 'MEETING' | 'NOTE' | 'FOLLOW_UP';

export interface Interaction {
  id: string;
  type: InteractionType;
  subject: string;
  description?: string | null;
  occurredAt: string;
  followUpAt?: string | null;
  authorUserId: string;
  authorName: string;
  companyId?: string | null;
  companyName?: string | null;
  contactId?: string | null;
  contactName?: string | null;
  leadId?: string | null;
  leadName?: string | null;
  opportunityId?: string | null;
  opportunityName?: string | null;
  projectId?: string | null;
  projectName?: string | null;
  projectVendorId?: string | null;
  projectVendorName?: string | null;
  quotationId?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface InteractionPayload {
  type: InteractionType;
  subject: string;
  description?: string | null;
  occurredAt: string;
  followUpAt?: string | null;
  companyId?: string | null;
  contactId?: string | null;
  leadId?: string | null;
  opportunityId?: string | null;
  projectId?: string | null;
  projectVendorId?: string | null;
  quotationId?: string | null;
}

export interface InteractionSearchParams {
  type?: InteractionType | '';
  creatorId?: string;
  companyId?: string;
  contactId?: string;
  leadId?: string;
  opportunityId?: string;
  projectId?: string;
  projectVendorId?: string;
  fromDate?: string;
  toDate?: string;
  page?: number;
  size?: number;
  sort?: string;
}
