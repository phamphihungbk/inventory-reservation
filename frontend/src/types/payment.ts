export type PaymentStatus = 'PENDING' | 'SUCCEEDED' | 'FAILED';

export interface CreatePaymentPayload {
  reservationId: number;
  cardNumber: string;
}

export interface Payment {
  id: number;
  reservationId: number;
  status: PaymentStatus;
  provider: string;
  providerReference: string;
  orderId: number | null;
  createdAt: string;
}

export interface Order {
  id: number;
  reservationId: number;
  eventName: string;
  ticketTypeName: string;
  quantity: number;
  totalAmount: number;
  createdAt: string;
}
