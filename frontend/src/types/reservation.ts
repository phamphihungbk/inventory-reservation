export type ReservationStatus = 'ACTIVE' | 'CANCELED' | 'EXPIRED';

export interface Reservation {
  id: number;
  productId: number;
  quantity: number;
  status: ReservationStatus;
  expiresAt: string;
  createdAt: string;
}

export interface CreateReservationPayload {
  productId: number;
  quantity: number;
}
