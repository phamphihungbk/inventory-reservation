<template>
  <section class="bg-white text-slate-950">
    <div v-if="eventStore.isLoading" class="mx-auto max-w-5xl py-24 text-center text-slate-500">
      Loading event...
    </div>

    <div v-else-if="eventStore.error" class="mx-auto max-w-5xl py-24">
      <div class="rounded-lg border border-red-200 bg-red-50 p-5 text-red-700">
        {{ eventStore.error }}
      </div>
    </div>

    <div v-else-if="event" class="mx-auto grid max-w-6xl grid-cols-1 gap-8 px-4 py-16 lg:grid-cols-[72px_1fr] lg:px-0">
      <aside class="hidden pt-20 lg:block">
        <p class="mb-5 text-lg font-bold text-slate-900">Share</p>
        <div class="grid gap-6">
          <button v-for="item in shareItems" :key="item" class="grid h-10 w-10 place-items-center rounded border border-indigo-500 text-xl font-bold text-indigo-600 transition hover:bg-indigo-50" type="button">
            {{ item }}
          </button>
        </div>
      </aside>

      <main>
        <div class="rounded bg-indigo-100 p-5">
          <div class="relative overflow-hidden rounded border border-slate-900/70 bg-gradient-to-r from-orange-400 via-rose-400 to-indigo-800">
            <div class="absolute inset-0 bg-[radial-gradient(circle_at_70%_25%,rgba(255,255,255,0.22),transparent_25%),radial-gradient(circle_at_90%_60%,rgba(255,255,255,0.18),transparent_18%)]" />
            <div class="relative grid min-h-[310px] grid-cols-1 items-center gap-8 px-8 py-7 md:grid-cols-[260px_1fr]">
              <div class="mx-auto h-64 w-44 overflow-hidden rounded-sm bg-white p-3 shadow-2xl md:mx-0">
                <div class="grid h-full place-items-center bg-gradient-to-br from-slate-950 via-blue-800 to-orange-500 p-4 text-center text-white">
                  <div>
                    <p class="text-xs font-black uppercase tracking-[0.25em] text-orange-200">Live</p>
                    <p class="mt-3 text-2xl font-black leading-tight">{{ shortTitle }}</p>
                    <p class="mt-4 text-xs font-semibold text-blue-100">{{ event.city }}</p>
                  </div>
                </div>
              </div>

              <div class="text-center text-white">
                <p class="text-3xl font-black italic tracking-tight">karsis</p>
                <p class="text-xs font-black uppercase tracking-[0.35em]">Live</p>
                <p class="mt-5 text-sm font-black uppercase tracking-[0.28em]">Drive In</p>
                <h1 class="mx-auto mt-2 max-w-xl text-5xl font-black leading-none md:text-7xl">{{ heroTitle }}</h1>
                <p class="mx-auto mt-5 inline-flex rounded-full bg-white px-6 py-2 text-sm font-black text-slate-900">
                  Official Ticket Partner
                </p>
              </div>
            </div>
          </div>
        </div>

        <section class="mt-8 grid gap-10 lg:grid-cols-[1fr_330px]">
          <div>
            <h2 class="text-3xl font-black tracking-tight">{{ event.name }}</h2>

            <div class="mt-6 space-y-4 text-base font-medium text-slate-800">
              <p class="flex gap-3">
                <span class="text-indigo-600">⌖</span>
                <span>{{ event.venue }} @ {{ event.city }}, {{ event.country }}</span>
              </p>
              <p class="flex gap-3">
                <span class="text-indigo-600">▣</span>
                <span>{{ detailDate }}</span>
              </p>
            </div>

            <p class="mt-5 max-w-2xl text-lg leading-7 text-slate-700">
              {{ event.description }}
            </p>
          </div>

          <aside class="lg:sticky lg:top-28 lg:self-start">
            <div class="rounded border-4 border-blue-500 bg-white p-7 text-center shadow-[6px_6px_0_#111827]">
              <p class="text-sm text-slate-500">Tickets starting at</p>
              <p class="mt-2 text-xl font-black">{{ startingPriceLabel }}</p>

              <label class="mt-5 block text-left text-sm font-semibold text-slate-700">
                Ticket type
                <select v-model.number="selectedTicketId" class="mt-2 w-full rounded border border-slate-300 bg-white px-3 py-2 text-sm">
                  <option v-for="ticket in event.ticketTypes" :key="ticket.id" :value="ticket.id">
                    {{ ticket.name }} · {{ formatCurrency(Number(ticket.price)) }} · {{ ticket.remainingQuantity }} left
                  </option>
                </select>
              </label>

              <label class="mt-4 block text-left text-sm font-semibold text-slate-700">
                Quantity
                <input v-model.number="quantity" class="mt-2 w-full rounded border border-slate-300 px-3 py-2 text-sm" type="number" min="1" :max="selectedTicket?.remainingQuantity ?? 1" />
              </label>

              <button class="mt-5 w-full rounded bg-indigo-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50" type="button" :disabled="!selectedTicket || selectedTicket.remainingQuantity === 0 || submittingId === selectedTicketId" @click="buyTickets">
                {{ submittingId === selectedTicketId ? 'Reserving...' : 'Buy Tickets' }}
              </button>

              <p v-if="selectedTicket" class="mt-4 text-xs font-semibold text-slate-500">
                Remaining: <span class="text-indigo-600">{{ selectedTicket.remainingQuantity }}</span>
              </p>
            </div>
          </aside>
        </section>

        <section class="mt-16">
          <h3 class="text-2xl font-black">Event Information</h3>
          <div class="mt-9 grid gap-10 md:grid-cols-3">
            <div class="flex gap-5">
              <span class="text-4xl text-indigo-600">◴</span>
              <div>
                <h4 class="text-lg font-black">Duration</h4>
                <p class="mt-2 text-sm text-slate-500">{{ timeRange }}</p>
                <p class="mt-2 text-sm text-slate-500">Approx. 2 hours</p>
              </div>
            </div>
            <div class="flex gap-5">
              <span class="text-4xl text-indigo-600">♟</span>
              <div>
                <h4 class="text-lg font-black">Audience</h4>
                <p class="mt-2 text-sm leading-6 text-slate-500">Suitable for all ticket holders. Seat availability updates live.</p>
              </div>
            </div>
            <div class="flex gap-5">
              <span class="text-4xl text-indigo-600">△</span>
              <div>
                <h4 class="text-lg font-black">Attention</h4>
                <p class="mt-2 text-sm leading-6 text-slate-500">Reservation expires after checkout timer. Inventory releases automatically.</p>
              </div>
            </div>
          </div>
        </section>

        <section class="mt-12">
          <h3 class="text-xl font-black">Description</h3>
          <p class="mt-4 max-w-5xl text-base leading-7 text-slate-700">
            {{ event.description }} This page demonstrates realtime inventory, optimistic reservation flow, and payment checkout for portfolio review.
          </p>
        </section>
      </main>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useEventStore } from '@/stores/eventStore';
import { useReservationStore } from '@/stores/reservationStore';
import { formatCurrency, formatDateTime } from '@/utils/formatters';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();
const reservationStore = useReservationStore();
const selectedTicketId = ref<number | null>(null);
const quantity = ref(1);
const submittingId = ref<number | null>(null);
const streams: EventSource[] = [];
const shareItems = ['↗', '◎', '𝕏', 'f'];

const event = computed(() => eventStore.selectedEvent);
const tickets = computed(() => event.value?.ticketTypes ?? []);
const selectedTicket = computed(() => tickets.value.find((ticket) => ticket.id === selectedTicketId.value) ?? tickets.value[0]);
const cheapestTicket = computed(() => {
  if (tickets.value.length === 0) return null;
  return tickets.value.reduce((lowest, ticket) => (Number(ticket.price) < Number(lowest.price) ? ticket : lowest), tickets.value[0]);
});
const startingPriceLabel = computed(() => (cheapestTicket.value ? formatCurrency(Number(cheapestTicket.value.price)) : 'Sold out'));
const detailDate = computed(() => (event.value ? formatDateTime(event.value.eventDate) : ''));
const timeRange = computed(() => {
  if (!event.value) return '';
  const start = new Date(event.value.eventDate);
  const end = new Date(start.getTime() + 2 * 60 * 60 * 1000);
  const formatter = new Intl.DateTimeFormat('en-US', { hour: 'numeric', minute: '2-digit' });
  return `${formatter.format(start)} - ${formatter.format(end)}`;
});
const heroTitle = computed(() => event.value?.city ?? 'Live');
const shortTitle = computed(() => event.value?.name.split(' ').slice(0, 3).join(' ') ?? 'Event');

onMounted(loadEvent);

watch(cheapestTicket, (ticket) => {
  if (!selectedTicketId.value && ticket) selectedTicketId.value = ticket.id;
});

onBeforeUnmount(() => {
  streams.forEach((stream) => stream.close());
});

async function loadEvent() {
  await eventStore.loadEvent(Number(route.params.id));
  streams.forEach((stream) => stream.close());

  event.value?.ticketTypes.forEach((ticket) => {
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

  selectedTicketId.value = cheapestTicket.value?.id ?? null;
}

async function buyTickets() {
  if (!selectedTicket.value) return;

  submittingId.value = selectedTicket.value.id;
  try {
    const reservation = await reservationStore.reserveTickets({
      ticketTypeId: selectedTicket.value.id,
      quantity: quantity.value,
    });
    await router.push(`/reservations/${reservation.id}`);
  } finally {
    submittingId.value = null;
  }
}
</script>