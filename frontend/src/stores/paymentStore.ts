import { defineStore } from 'pinia';

import { createPayment, fetchOrder } from '@/api/payments';
import { toFriendlyError } from '@/api/http';
import type { CreatePaymentPayload, Order, Payment } from '@/types/payment';

interface PaymentState {
  payment: Payment | null;
  order: Order | null;
  isLoading: boolean;
  error: string | null;
}

export const usePaymentStore = defineStore('paymentStore', {
  state: (): PaymentState => ({
    payment: null,
    order: null,
    isLoading: false,
    error: null,
  }),
  actions: {
    async pay(payload: CreatePaymentPayload) {
      this.isLoading = true;
      this.error = null;
      try {
        this.payment = await createPayment(payload);
        return this.payment;
      } catch (error) {
        this.error = toFriendlyError(error);
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async loadOrder(id: number) {
      this.isLoading = true;
      this.error = null;
      try {
        this.order = await fetchOrder(id);
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },
  },
});
