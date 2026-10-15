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

export type DashboardDateRange = 'LAST_30_DAYS' | 'LAST_90_DAYS' | 'THIS_YEAR' | 'ALL_TIME';

export interface OutreachStatusCount {
  status: string;
  count: number;
}

export interface QuotationTrendPoint {
  periodStart: string;
  quotationCount: number;
}

export interface AssociateWorkload {
  associateId: string;
  associateName: string;
  activeProjects: number;
  dueFollowUps: number;
}

export interface ProjectDashboardAnalytics {
  role: 'ADMIN' | 'MANAGER' | 'SALES_REP';
  dateRange: DashboardDateRange;
  trendStartDate: string;
  trendEndDate: string;
  trendGranularity: 'DAY' | 'MONTH';
  selectedAssociateId?: string | null;
  selectedAssociateName?: string | null;
  totalProjects: number;
  activeProjects: number;
  completedProjects: number;
  nearingDeadlineProjects: number;
  overdueProjects: number;
  totalVendorAssignments: number;
  vendorsAwaitingResponse: number;
  quotationsReceived: number;
  vendorAssignmentsWithQuotes: number;
  quoteResponseRate: number;
  followUpsDueToday: number;
  overdueFollowUps: number;
  upcomingFollowUps: number;
  projectsByStatus: ProjectStatusCount[];
  vendorOutreachByStatus: OutreachStatusCount[];
  quotationTrends: QuotationTrendPoint[];
  associateWorkloads: AssociateWorkload[];
  quotationValueByCurrency: DashboardCurrencyTotal[];
}
