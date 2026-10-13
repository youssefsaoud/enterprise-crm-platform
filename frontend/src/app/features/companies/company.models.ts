export type CompanyRole = 'CLIENT' | 'VENDOR';
export interface Company { id:string; name:string; industry?:string|null; website?:string|null; phone?:string|null; email?:string|null; address?:string|null; roles:CompanyRole[]; createdAt:string; updatedAt:string; }
export interface CompanyPayload { name:string; industry?:string|null; website?:string|null; phone?:string|null; email?:string|null; address?:string|null; roles:CompanyRole[]; }
export interface CompanySearchParams { search?:string; industry?:string; role?:CompanyRole; page?:number; size?:number; sort?:string; }