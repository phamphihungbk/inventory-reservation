export type ReservationStatus = 'ACTIVE' | 'CANCELLED' | 'EXPIRED';

export interface Reservation {
  id: number;
  ticketTypeId: number;
  ticketTypeName: string;
  eventId: number;
  eventName: string;
  quantity: number;
  status: ReservationStatus;
  expiresAt: string;
  createdAt: string;
}

export interface CreateReservationPayload {
  ticketTypeId: number;
  quantity: number;
}
