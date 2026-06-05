<template>
  <section>
    <div class="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase text-blue-700">Ticketmaster MVP</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-950">Find tickets for live events</h1>
        <p class="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
          Search events, reserve tickets, complete simulated payment, and watch inventory update live.
        </p>
      </div>
      <form class="flex w-full max-w-md gap-2" @submit.prevent="goSearch">
        <input v-model.trim="query" class="input-field" placeholder="Coldplay Vienna" />
        <button class="btn-primary" type="submit">Search</button>
      </form>
    </div>

    <div v-if="eventStore.isLoading" class="mt-10 flex justify-center">
      <LoadingSpinner label="Loading events" />
    </div>

    <EmptyState
      v-else-if="eventStore.events.length === 0"
      class="mt-8"
      title="No events yet"
      message="Create events through the API to start the Ticketmaster flow."
    />

    <div v-else class="mt-8 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <RouterLink
        v-for="event in eventStore.events"
        :key="event.id"
        :to="`/events/${event.id}`"
        class="panel block p-5 transition hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-lg"
      >
        <p class="text-sm font-semibold text-blue-700">{{ formatDateTime(event.eventDate) }}</p>
        <h2 class="mt-2 text-xl font-bold text-slate-950">{{ event.name }}</h2>
        <p class="mt-2 text-sm text-slate-500">{{ event.venue }}, {{ event.city }}, {{ event.country }}</p>
        <p class="mt-4 text-sm text-slate-600">{{ event.description }}</p>
      </RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';

import EmptyState from '@/components/EmptyState.vue';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import { useEventStore } from '@/stores/eventStore';
import { formatDateTime } from '@/utils/formatters';

const router = useRouter();
const eventStore = useEventStore();
const query = ref('');

onMounted(() => {
  void eventStore.loadEvents();
});

function goSearch() {
  if (query.value) {
    void router.push({ path: '/search', query: { q: query.value } });
  }
}
</script>
