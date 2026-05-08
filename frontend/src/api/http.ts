import axios, { AxiosError } from 'axios';

import type { ApiErrorResponse } from '@/types/api';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export function toFriendlyError(error: unknown): string {
  if (!axios.isAxiosError(error)) {
    return 'Something went wrong. Please try again.';
  }

  const axiosError = error as AxiosError<ApiErrorResponse>;

  if (!axiosError.response) {
    return 'Unable to reach the backend. Check that Spring Boot is running on port 8080.';
  }

  const data = axiosError.response.data;
  const status = axiosError.response.status;

  if (data?.details?.length) {
    return data.details.join(' ');
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error) {
    return data.error;
  }

  if (status === 409) {
    return 'Insufficient stock or inventory changed. Refresh and try again.';
  }

  if (status === 400) {
    return 'Please check the submitted values.';
  }

  return 'Backend returned an unexpected error.';
}
