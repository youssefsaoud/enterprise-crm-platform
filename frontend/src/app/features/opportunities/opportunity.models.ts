export type OpportunityStage = 'PROSPECTING' | 'QUALIFICATION' | 'PROPOSAL' | 'NEGOTIATION' | 'CLOSED_WON' | 'CLOSED_LOST';

export interface Opportunity {
  id: string;
  name: string;
  companyId: string;
  companyName: string;
  contactId?: string | null;
  contactName?: string | null;
  ownerUserId?: string | null;
  ownerName?: string | null;
  stage: OpportunityStage;
  amount: number;
  currencyCode: string;
  expectedCloseDate?: string | null;
  description?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OpportunityPayload {
  name: string;
  companyId: string;
  contactId?: string | null;
  ownerUserId?: string | null;
  amount: number;
  currencyCode?: string | null;
  expectedCloseDate?: string | null;
  description?: string | null;
}

export interface OpportunitySearchParams {
  search?: string;
  stage?: OpportunityStage | '';
  ownerId?: string;
  companyId?: string;
  closeDateFrom?: string;
  closeDateTo?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PipelineSummary {
  stage: OpportunityStage;
  currencyCode: string;
  count: number;
  totalAmount: number;
}
