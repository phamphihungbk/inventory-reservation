<template>
  <section v-if="reservation" class="mx-auto max-w-2xl">
    <div class="panel p-6">
      <p class="text-sm font-semibold uppercase text-blue-700">Reservation active</p>
      <h1 class="mt-2 text-2xl font-bold text-slate-950">{{ reservation.eventName }}</h1>
      <p class="mt-2 text-sm text-slate-500">{{ reservation.ticketTypeName }} x {{ reservation.quantity }}</p>
      <p class="mt-6 text-sm text-slate-500">Expires at</p>
      <p class="mt-1 text-lg font-bold text-slate-950">{{ formatDateTime(reservation.expiresAt) }}</p>
      <p class="mt-4 rounded-lg bg-amber-50 p-4 text-sm font-semibold text-amber-700">
        Time left: {{ timeLeft }}
      </p>
      <RouterLink :to="`/checkout/${reservation.id}`" class="btn-primary mt-6 w-full">Continue to checkout</RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';

import { fetchReservations } from '@/api/reservations';
import type { Reservation } from '@/types/reservation';
import { formatDateTime } from '@/utils/formatters';

const route = useRoute();
const reservation = ref<Reservation | null>(null);
const now = ref(Date.now());
let timer: number | undefined;

const timeLeft = computed(() => {
  if (!reservation.value) return '';
  const ms = Math.max(0, new Date(reservation.value.expiresAt).getTime() - now.value);
  const minutes = Math.floor(ms / 60_000);
  const seconds = Math.floor((ms % 60_000) / 1_000);
  return `${minutes}m ${seconds}s`;
});

onMounted(async () => {
  const reservations = await fetchReservations();
  reservation.value = reservations.find((item) => item.id === Number(route.params.id)) ?? null;
  timer = window.setInterval(() => {
    now.value = Date.now();
  }, 1000);
});

onUnmounted(() => {
  if (timer) window.clearInterval(timer);
});
</script>
