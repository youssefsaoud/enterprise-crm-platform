export type QuotationStatus = 'RECEIVED' | 'UNDER_REVIEW' | 'REVISED' | 'REJECTED' | 'SELECTED' | 'EXPIRED' | 'WITHDRAWN';

export interface Quotation {
  id: string;
  projectVendorId: string;
  projectId: string;
  projectName: string;
  vendorName: string;
  amount: number;
  currencyCode: string;
  receivedDate: string;
  validUntil?: string | null;
  status: QuotationStatus;
  revisionNumber: number;
  selected: boolean;
  notes?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface QuotationPayload {
  amount: number;
  currencyCode: string;
  receivedDate: string;
  validUntil?: string | null;
  revisionNumber?: number | null;
  status?: QuotationStatus;
  notes?: string | null;
}
