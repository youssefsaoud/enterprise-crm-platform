export interface Contact {
  id: string;
  companyId: string;
  companyName: string;
  firstName: string;
  lastName: string;
  email?: string | null;
  phone?: string | null;
  jobTitle?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ContactPayload {
  companyId: string;
  firstName: string;
  lastName: string;
  email?: string | null;
  phone?: string | null;
  jobTitle?: string | null;
}

export interface ContactSearchParams {
  search?: string;
  companyId?: string;
  page?: number;
  size?: number;
  sort?: string;
}
