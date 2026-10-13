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

export interface ProjectStatusCount {
  status: string;
  count: number;
}

export interface ProjectDashboardActivity {
  id: string;
  type: string;
  subject: string;
  occurredAt: string;
  authorName: string;
  projectId: string;
  projectName: string;
  projectVendorId?: string | null;
  vendorName?: string | null;
}

export interface ProjectDashboardSummary {
  totalProjects: number;
  activeProjects: number;
  nearingDeadlineProjects: number;
  overdueProjects: number;
  vendorsContacted: number;
  vendorsAwaitingResponse: number;
  quotationsReceived: number;
  quotationsPending: number;
  quoteResponseRate: number;
  upcomingFollowUps: number;
  overdueFollowUps: number;
  projectsByStatus: ProjectStatusCount[];
  quotationValueByCurrency: DashboardCurrencyTotal[];
  recentActivities: ProjectDashboardActivity[];
}
