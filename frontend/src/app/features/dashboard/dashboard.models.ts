export interface DashboardCurrencyTotal {
  currencyCode: string;
  amount: number;
}

export interface DashboardLeadStatus {
  status: string;
  count: number;
}

export interface DashboardSummary {
  totalLeads: number;
  leadsByStatus: DashboardLeadStatus[];
  openOpportunities: number;
  openPipelineValue: DashboardCurrencyTotal[];
  recentInteractionCount: number;
}

export interface PipelineStageSummary {
  stage: string;
  currencyCode: string;
  count: number;
  totalAmount: number;
}

export interface RecentActivity {
  id: string;
  type: string;
  subject: string;
  occurredAt: string;
  authorName: string;
  companyId?: string | null;
  companyName?: string | null;
  contactId?: string | null;
  contactName?: string | null;
  leadId?: string | null;
  leadName?: string | null;
  opportunityId?: string | null;
  opportunityName?: string | null;
}
