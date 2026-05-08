<template>
  <section>
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase text-blue-700">Reservations</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-950">Reservation activity</h1>
        <p class="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
          Review active holds, canceled reservations, and expired stock returns.
        </p>
      </div>
      <button class="btn-secondary" type="button" :disabled="reservationStore.isLoading" @click="reservationStore.loadReservations()">
        Refresh reservations
      </button>
    </div>

    <div class="mt-6 grid gap-4 sm:grid-cols-2">
      <div class="panel p-4">
        <p class="text-sm text-slate-500">Reservations</p>
        <p class="mt-2 text-2xl font-bold text-slate-950">{{ reservationStore.reservations.length }}</p>
      </div>
      <div class="panel p-4">
        <p class="text-sm text-slate-500">Active holds</p>
        <p class="mt-2 text-2xl font-bold text-slate-950">{{ reservationStore.activeCount }}</p>
      </div>
    </div>

    <div v-if="reservationStore.error" class="mt-6 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
      {{ reservationStore.error }}
    </div>

    <div v-if="reservationStore.isLoading" class="mt-10 flex justify-center">
      <LoadingSpinner label="Loading reservations" />
    </div>

    <EmptyState
      v-else-if="reservationStore.reservations.length === 0"
      class="mt-8"
      title="No reservations yet"
      message="Reserve a product from the inventory dashboard to see lifecycle state here."
    >
      <template #action>
        <RouterLink to="/products" class="btn-primary">View products</RouterLink>
      </template>
    </EmptyState>

    <div v-else class="mt-8 space-y-4">
      <ReservationCard
        v-for="reservation in reservationStore.reservations"
        :key="reservation.id"
        :reservation="reservation"
        :is-canceling="cancelingId === reservation.id"
        @cancel="handleCancel"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';

import EmptyState from '@/components/EmptyState.vue';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import ReservationCard from '@/components/ReservationCard.vue';
import { useProductStore } from '@/stores/productStore';
import { useReservationStore } from '@/stores/reservationStore';

const productStore = useProductStore();
const reservationStore = useReservationStore();
const cancelingId = ref<number | null>(null);

onMounted(() => {
  void reservationStore.loadReservations();
});

async function handleCancel(id: number) {
  cancelingId.value = id;

  try {
    await reservationStore.cancelActiveReservation(id);
    await productStore.loadProducts();
  } catch {
    // Store owns user-facing error message and rolls optimistic update back.
  } finally {
    cancelingId.value = null;
  }
}
</script>
