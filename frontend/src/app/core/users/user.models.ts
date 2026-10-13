export type UserRole = 'ADMIN' | 'MANAGER' | 'SALES_REP';
export interface UserSummary { id: string; fullName: string; email: string; role: UserRole; }