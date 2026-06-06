<template>
  <div class="bg-[#f7f7f8] text-slate-950">
    <div class="mx-auto min-h-screen max-w-[1180px] bg-white px-8 py-20">
      <div class="grid gap-12 lg:grid-cols-[250px_1fr]">
        <aside class="border-r border-slate-200 pr-9">
          <h2 class="text-2xl font-black">Filters</h2>

          <div class="mt-10 border-t border-slate-200 pt-5">
            <label class="flex items-center gap-3 text-sm text-slate-700">
              <span class="relative inline-flex h-4 w-9 items-center rounded-full border border-slate-900">
                <span class="ml-0.5 h-3 w-3 rounded-full bg-white shadow ring-1 ring-slate-900" />
              </span>
              Online
            </label>
          </div>

          <FilterGroup title="Location" :items="locations" active="Jabodetabek" />
          <FilterGroup title="Categories" :items="categories" active="Movies" />

          <div class="mt-6 border-t border-slate-200 pt-5">
            <div class="flex items-center justify-between">
              <h3 class="font-semibold">Price</h3>
              <span>⌃</span>
            </div>
            <div class="mt-8 px-1">
              <div class="relative h-px bg-indigo-200">
                <div class="absolute left-[22%] right-[24%] h-px bg-indigo-600" />
                <div class="absolute left-[22%] top-1/2 h-4 w-4 -translate-y-1/2 rounded-full bg-indigo-600" />
                <div class="absolute right-[24%] top-1/2 h-4 w-4 -translate-y-1/2 rounded-full bg-indigo-600" />
              </div>
              <div class="mt-6 flex items-center justify-between text-sm text-slate-700">
                <span>Rp. 100.000</span>
                <span>-</span>
                <span>Rp. 600.000</span>
              </div>
            </div>
          </div>
        </aside>

        <main>
          <h1 class="text-center text-2xl font-light text-slate-700">
            Search results for <span class="font-black text-slate-950">“{{ displayQuery }}”</span>
          </h1>

          <form class="mx-auto mt-7 grid max-w-2xl grid-cols-[1fr_110px] border border-slate-300 bg-white lg:hidden" @submit.prevent="search">
            <input v-model.trim="query" class="h-10 px-3 text-sm outline-none" placeholder="Search events" />
            <button class="bg-indigo-600 text-sm font-semibold text-white" type="submit">Search</button>
          </form>

          <div v-if="eventStore.isLoading" class="mt-16 flex justify-center">
            <LoadingSpinner label="Searching events" />
          </div>

          <div v-else class="mt-16 grid gap-8 md:grid-cols-2 xl:grid-cols-3">
            <RouterLink
              v-for="(event, index) in events"
              :key="event.id"
              :to="`/events/${event.id}`"
              class="overflow-hidden rounded border transition hover:-translate-y-0.5 hover:shadow-md"
              :class="index === 1 ? 'border-indigo-600 shadow-[6px_6px_0_#1d1fcf]' : 'border-slate-300'"
            >
              <div class="h-[180px]" :class="posterClass(event.id)" />
              <div class="grid grid-cols-[54px_1fr] gap-4 bg-white p-4">
                <div class="text-center text-sm font-semibold uppercase text-slate-800">
                  <div>{{ dateMonth(event.eventDate) }}</div>
                  <div class="mt-2 text-slate-950">{{ dateDay(event.eventDate) }}</div>
                </div>
                <div class="min-w-0">
                  <p class="text-sm font-medium text-indigo-600">Movies · Drive In</p>
                  <h2 class="mt-1 truncate text-base font-semibold text-slate-950">{{ event.name }}</h2>
                  <p class="mt-2 text-sm text-slate-700">Rp. {{ priceRange(event) }}</p>
                  <p class="mt-2 flex items-center gap-2 truncate text-sm text-slate-600">
                    <span>⌖</span>
                    {{ event.venue }}
                  </p>
                </div>
              </div>
            </RouterLink>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineComponent, h, onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

import LoadingSpinner from '@/components/LoadingSpinner.vue';
import { useEventStore } from '@/stores/eventStore';
import type { Event, EventSearchResult } from '@/types/event';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();
const query = ref(String(route.query.q ?? 'Drive In'));

const locations = ['International', 'Jakarta Selatan', 'Jakarta Timur', 'Tangerang Selatan', 'Jabodetabek'];
const categories = ['Concert', 'Arts', 'Conference', 'Movies'];

const displayQuery = computed(() => query.value || 'Drive In');
const events = computed(() => {
  if (query.value && (eventStore.searchResults?.content.length ?? 0) > 0) {
    return eventStore.searchResults?.content ?? [];
  }
  return eventStore.events;
});

const FilterGroup = defineComponent({
  props: {
    title: { type: String, required: true },
    items: { type: Array as () => string[], required: true },
    active: { type: String, required: true },
  },
  setup(props) {
    return () =>
      h('div', { class: 'mt-6 border-t border-slate-200 pt-5' }, [
        h('div', { class: 'flex items-center justify-between' }, [
          h('h3', { class: 'font-semibold' }, props.title),
          h('span', '⌃'),
        ]),
        h(
          'div',
          { class: 'mt-5 space-y-4' },
          props.items.map((item) =>
            h('label', { class: 'flex items-center gap-3 text-sm text-slate-700' }, [
              h('input', {
                type: 'checkbox',
                checked: item === props.active,
                class: 'h-4 w-4 accent-indigo-600',
              }),
              h('span', { class: item === props.active ? 'text-indigo-600' : '' }, item),
            ]),
          ),
        ),
      ]);
  },
});

onMounted(() => {
  void eventStore.loadEvents();
  if (query.value) {
    void eventStore.search(query.value);
  }
});

watch(
  () => route.query.q,
  (value) => {
    query.value = String(value ?? 'Drive In');
    if (query.value) void eventStore.search(query.value);
  },
);

function search() {
  void router.push({ path: '/search', query: { q: query.value } });
}

function dateMonth(value: string): string {
  return new Intl.DateTimeFormat('en-US', { month: 'short' }).format(new Date(value));
}

function dateDay(value: string): string {
  return new Intl.DateTimeFormat('en-US', { day: '2-digit' }).format(new Date(value));
}

function priceRange(event: Event | EventSearchResult): string {
  if ('ticketTypes' in event && event.ticketTypes.length > 0) {
    const prices = event.ticketTypes.map((ticket) => Number(ticket.price));
    return `${formatPrice(Math.min(...prices))} - ${formatPrice(Math.max(...prices))}`;
  }
  return '100.000 - 550.000';
}

function formatPrice(value: number): string {
  return new Intl.NumberFormat('id-ID', { maximumFractionDigits: 0 }).format(value);
}

function posterClass(id: number): string {
  const classes = [
    'bg-gradient-to-br from-purple-950 via-fuchsia-600 to-yellow-300',
    'bg-gradient-to-r from-orange-400 via-rose-400 to-indigo-800',
    'bg-gradient-to-br from-cyan-900 via-sky-500 to-yellow-300',
    'bg-gradient-to-br from-yellow-400 via-orange-500 to-red-900',
    'bg-gradient-to-br from-slate-950 via-purple-800 to-pink-500',
    'bg-gradient-to-br from-emerald-100 via-orange-200 to-red-400',
    'bg-gradient-to-br from-blue-950 via-slate-900 to-purple-500',
    'bg-gradient-to-br from-orange-900 via-yellow-500 to-slate-900',
    'bg-gradient-to-br from-cyan-950 via-slate-900 to-white',
  ];
  return classes[id % classes.length];
}
</script>
