import { Company } from '../companies/company.models';

export type OutreachStatus = 'NOT_CONTACTED' | 'NO_CONTACT_REACHED' | 'SOW_SENT' | 'QUOTE_IN_PROCESS' | 'QUOTE_RECEIVED' | 'QUOTE_DECLINED';

export interface ProjectVendor {
  id: string;
  projectId: string;
  projectName: string;
  projectReference: string;
  vendorId: string;
  vendorName: string;
  vendorIndustry?: string | null;
  responsibleAssociateId?: string | null;
  responsibleAssociateName?: string | null;
  status: OutreachStatus;
  firstContactDate?: string | null;
  lastContactDate?: string | null;
  nextFollowUpDate?: string | null;
  notes?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectVendorPayload {
  vendorId?: string;
  responsibleAssociateId?: string | null;
  status?: OutreachStatus;
  firstContactDate?: string | null;
  lastContactDate?: string | null;
  nextFollowUpDate?: string | null;
  notes?: string | null;
}

export type VendorDirectoryItem = Pick<Company, 'id' | 'name' | 'industry' | 'roles'>;
