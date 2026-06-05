import { apiClient } from '@/api/http';
import type { CreatePaymentPayload, Order, Payment } from '@/types/payment';

export async function createPayment(payload: CreatePaymentPayload): Promise<Payment> {
  const { data } = await apiClient.post<Payment>('/payments', payload);
  return data;
}

export async function fetchOrder(id: number): Promise<Order> {
  const { data } = await apiClient.get<Order>(`/orders/${id}`);
  return data;
}
