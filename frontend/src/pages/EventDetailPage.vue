<template>
  <section v-if="eventStore.selectedEvent">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase text-blue-700">{{ formatDateTime(eventStore.selectedEvent.eventDate) }}</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-950">{{ eventStore.selectedEvent.name }}</h1>
        <p class="mt-2 text-sm text-slate-500">
          {{ eventStore.selectedEvent.venue }}, {{ eventStore.selectedEvent.city }}, {{ eventStore.selectedEvent.country }}
        </p>
        <p class="mt-4 max-w-3xl text-sm leading-6 text-slate-600">{{ eventStore.selectedEvent.description }}</p>
      </div>
      <span class="rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm font-semibold text-green-700">
        Live inventory on
      </span>
    </div>

    <div class="mt-8 grid gap-5 md:grid-cols-2">
      <article v-for="ticket in eventStore.selectedEvent.ticketTypes" :key="ticket.id" class="panel p-5">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="text-lg font-bold text-slate-950">{{ ticket.name }}</h2>
            <p class="mt-1 text-sm text-slate-500">{{ formatCurrency(Number(ticket.price)) }}</p>
          </div>
          <span class="rounded-full px-2.5 py-1 text-xs font-semibold" :class="ticket.remainingQuantity < 10 ? 'bg-red-50 text-red-700' : 'bg-green-50 text-green-700'">
            {{ ticket.remainingQuantity }} left
          </span>
        </div>
        <form class="mt-5 flex gap-2" @submit.prevent="reserve(ticket.id)">
          <input v-model.number="quantities[ticket.id]" class="input-field" type="number" min="1" :max="ticket.remainingQuantity" />
          <button class="btn-primary" type="submit" :disabled="ticket.remainingQuantity === 0 || submittingId === ticket.id">
            Reserve
          </button>
        </form>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useEventStore } from '@/stores/eventStore';
import { useReservationStore } from '@/stores/reservationStore';
import { formatCurrency, formatDateTime } from '@/utils/formatters';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();
const reservationStore = useReservationStore();
const quantities = reactive<Record<number, number>>({});
const submittingId = ref<number | null>(null);
const streams: EventSource[] = [];

onMounted(async () => {
  await eventStore.loadEvent(Number(route.params.id));
  eventStore.selectedEvent?.ticketTypes.forEach((ticket) => {
    quantities[ticket.id] = 1;
    const stream = new EventSource(`${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'}/tickets/${ticket.id}/watch`);
    stream.addEventListener('inventory-updated', (message) => {
      const update = JSON.parse((message as MessageEvent<string>).data) as {
        ticketTypeId: number;
        remainingQuantity: number;
      };
      eventStore.applyInventoryUpdate(update.ticketTypeId, update.remainingQuantity);
    });
    streams.push(stream);
  });
});

onBeforeUnmount(() => {
  streams.forEach((stream) => stream.close());
});

async function reserve(ticketTypeId: number) {
  submittingId.value = ticketTypeId;
  try {
    const reservation = await reservationStore.reserveTickets({
      ticketTypeId,
      quantity: quantities[ticketTypeId] ?? 1,
    });
    await router.push(`/reservations/${reservation.id}`);
  } finally {
    submittingId.value = null;
  }
}
</script>
