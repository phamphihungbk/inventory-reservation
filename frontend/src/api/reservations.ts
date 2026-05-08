import { apiClient } from '@/api/http';
import type { CreateReservationPayload, Reservation } from '@/types/reservation';

export async function fetchReservations(): Promise<Reservation[]> {
  const { data } = await apiClient.get<Reservation[]>('/reservations');
  return data;
}

export async function createReservation(payload: CreateReservationPayload): Promise<Reservation> {
  const { data } = await apiClient.post<Reservation>('/reservations', payload);
  return data;
}

export async function cancelReservation(id: number): Promise<Reservation> {
  const { data } = await apiClient.delete<Reservation>(`/reservations/${id}`);
  return data;
}
