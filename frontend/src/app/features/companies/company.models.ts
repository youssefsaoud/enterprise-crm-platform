export interface Company {
  id: string;
  name: string;
  industry?: string | null;
  website?: string | null;
  phone?: string | null;
  email?: string | null;
  address?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CompanyPayload {
  name: string;
  industry?: string | null;
  website?: string | null;
  phone?: string | null;
  email?: string | null;
  address?: string | null;
}

export interface CompanySearchParams {
  search?: string;
  industry?: string;
  page?: number;
  size?: number;
  sort?: string;
}
