import { defineStore } from 'pinia';

import { toFriendlyError } from '@/api/http';
import {
  cancelReservation,
  createReservation,
  fetchReservations,
} from '@/api/reservations';
import type { CreateReservationPayload, Reservation } from '@/types/reservation';

interface ReservationState {
  reservations: Reservation[];
  isLoading: boolean;
  error: string | null;
}

export const useReservationStore = defineStore('reservationStore', {
  state: (): ReservationState => ({
    reservations: [],
    isLoading: false,
    error: null,
  }),
  getters: {
    activeCount: (state) =>
      state.reservations.filter((reservation) => reservation.status === 'ACTIVE').length,
  },
  actions: {
    async loadReservations() {
      this.isLoading = true;
      this.error = null;

      try {
        this.reservations = await fetchReservations();
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },

    async reserveProduct(payload: CreateReservationPayload) {
      this.error = null;

      try {
        const reservation = await createReservation(payload);
        this.reservations = [reservation, ...this.reservations];
        return reservation;
      } catch (error) {
        this.error = toFriendlyError(error);
        throw error;
      }
    },

    async cancelActiveReservation(id: number) {
      this.error = null;
      const previous = [...this.reservations];

      this.reservations = this.reservations.map((reservation) =>
        reservation.id === id
          ? {
              ...reservation,
              status: 'CANCELED',
            }
          : reservation,
      );

      try {
        const updated = await cancelReservation(id);
        this.reservations = this.reservations.map((reservation) =>
          reservation.id === id ? updated : reservation,
        );
        return updated;
      } catch (error) {
        this.reservations = previous;
        this.error = toFriendlyError(error);
        throw error;
      }
    },
  },
});
