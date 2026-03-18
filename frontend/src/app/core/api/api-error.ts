import { HttpErrorResponse } from '@angular/common/http';

interface ApiErrorBody {
  message?: string;
  details?: string[];
}

export function apiErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ApiErrorBody | null;
    if (body?.details?.length) {
      return body.details.join(' ');
    }
    if (body?.message) {
      return body.message;
    }
  }
  return fallback;
}
