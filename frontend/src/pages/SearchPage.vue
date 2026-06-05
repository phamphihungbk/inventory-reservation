<template>
  <section>
    <form class="panel flex gap-2 p-4" @submit.prevent="search">
      <input v-model.trim="query" class="input-field" placeholder="Search event, venue, city, country" />
      <button class="btn-primary" type="submit">Search</button>
    </form>

    <div v-if="eventStore.isLoading" class="mt-10 flex justify-center">
      <LoadingSpinner label="Searching events" />
    </div>

    <div v-else class="mt-8 space-y-4">
      <RouterLink
        v-for="event in eventStore.searchResults?.content ?? []"
        :key="event.id"
        :to="`/events/${event.id}`"
        class="panel block p-5 transition hover:border-blue-200"
      >
        <h2 class="text-lg font-bold text-slate-950">{{ event.name }}</h2>
        <p class="mt-2 text-sm text-slate-500">{{ event.venue }}, {{ event.city }}, {{ event.country }}</p>
        <p class="mt-2 text-sm font-semibold text-blue-700">{{ formatDateTime(event.eventDate) }}</p>
      </RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

import LoadingSpinner from '@/components/LoadingSpinner.vue';
import { useEventStore } from '@/stores/eventStore';
import { formatDateTime } from '@/utils/formatters';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();
const query = ref(String(route.query.q ?? ''));

onMounted(() => {
  if (query.value) void eventStore.search(query.value);
});

watch(
  () => route.query.q,
  (value) => {
    query.value = String(value ?? '');
    if (query.value) void eventStore.search(query.value);
  },
);

function search() {
  void router.push({ path: '/search', query: { q: query.value } });
}
</script>
