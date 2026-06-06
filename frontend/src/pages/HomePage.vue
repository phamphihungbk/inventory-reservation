<template>
  <div class="bg-[#e5e5e5] text-slate-950">
    <div class="mx-auto min-h-screen w-full max-w-[1180px] bg-white">
      <section class="relative overflow-hidden px-8 pt-8">
        <div class="absolute left-8 top-7 h-11 w-11 rounded-full border border-slate-700">
          <div class="absolute left-1/2 top-0 h-full w-px -translate-x-1/2 bg-slate-300" />
          <div class="absolute left-0 top-1/2 h-px w-full -translate-y-1/2 bg-slate-300" />
        </div>
        <div class="absolute right-8 top-7 h-11 w-11 rounded-full border border-slate-700">
          <div class="absolute left-1/2 top-0 h-full w-px -translate-x-1/2 bg-slate-300" />
          <div class="absolute left-0 top-1/2 h-px w-full -translate-y-1/2 bg-slate-300" />
        </div>

        <h1 class="pt-10 text-center text-[40px] font-black leading-tight tracking-tight text-indigo-600">
          Exclusive events, priceless moments
        </h1>

        <div class="relative mx-auto mt-8 h-[142px] max-w-[1120px] overflow-hidden">
          <div class="absolute bottom-0 left-0 right-0 flex items-end justify-between">
            <div v-for="person in heroPeople" :key="person" class="relative h-[118px] w-[118px]">
              <div class="absolute left-1/2 top-2 h-9 w-9 -translate-x-1/2 rounded-full border-2 border-indigo-500 bg-indigo-50" />
              <div
                class="absolute left-1/2 top-12 h-16 w-11 -translate-x-1/2 rounded-t-[2rem] bg-indigo-600"
                :class="person % 2 ? 'rotate-12' : '-rotate-12'"
              />
              <div class="absolute bottom-0 left-8 h-12 w-2 -rotate-12 rounded-full bg-slate-800" />
              <div class="absolute bottom-0 right-8 h-12 w-2 rotate-12 rounded-full bg-slate-800" />
              <div class="absolute bottom-6 left-2 h-px w-12 rotate-12 bg-slate-800" />
              <div class="absolute bottom-8 right-2 h-px w-12 -rotate-12 bg-slate-800" />
            </div>
          </div>
        </div>

        <form
          class="relative z-10 mx-auto -mt-5 grid max-w-[820px] grid-cols-[1fr_190px_96px] border border-slate-900 bg-white shadow-sm"
          @submit.prevent="goSearch"
        >
          <input
            v-model.trim="query"
            class="h-10 px-4 text-[11px] outline-none placeholder:text-slate-400"
            placeholder="Search by events, names, location, and more"
          />
          <label class="flex h-10 items-center gap-2 border-l border-slate-300 px-4 text-[11px] text-slate-500">
            <input v-model="hasDate" type="checkbox" />
            Select date
          </label>
          <button class="h-10 bg-indigo-600 text-[11px] font-semibold text-white transition hover:bg-indigo-700" type="submit">
            Search
          </button>
        </form>
      </section>

      <main class="mx-auto max-w-[980px] px-6 pb-24 pt-24">
        <div v-if="eventStore.isLoading" class="py-10 text-center">
          <LoadingSpinner label="Loading events" />
        </div>

        <EmptyState
          v-else-if="eventStore.events.length === 0"
          title="No events yet"
          message="Run make seed to generate demo events."
        />

        <template v-else>
          <EventStrip title="Upcoming Events" :events="upcomingEvents" />

          <section class="mt-16">
            <SectionHeader title="Hot Offers" />
            <div class="mt-6 grid grid-cols-2 gap-4">
              <div class="h-[250px] overflow-hidden rounded-sm bg-gradient-to-br from-violet-800 via-purple-700 to-indigo-700 p-8 text-white">
                <p class="text-3xl font-black">OVO</p>
                <p class="mt-8 text-3xl font-black leading-tight">CLBK Cashback Lagi Buat Kamu</p>
                <p class="mt-3 text-8xl font-black leading-none">50<span class="text-3xl">%</span></p>
              </div>
              <div class="relative h-[250px] overflow-hidden rounded-sm bg-slate-50 p-8">
                <div class="absolute -left-12 top-12 h-32 w-64 rotate-[-17deg] rounded-xl bg-slate-950 shadow-2xl" />
                <div class="absolute bottom-4 right-6 h-20 w-20 rounded-full border-[12px] border-pink-500 border-l-transparent border-t-transparent" />
                <div class="relative ml-auto max-w-xs text-right">
                  <p class="text-2xl font-black">Pay and get</p>
                  <p class="font-serif text-3xl italic text-orange-500">Special Discount</p>
                  <p class="text-2xl font-black leading-tight">with any debit or credit VISA card</p>
                </div>
              </div>
            </div>
          </section>

          <section class="mt-16">
            <SectionHeader title="Top Selling" />
            <div class="mt-6 grid grid-cols-3 gap-4">
              <RouterLink
                v-for="event in topSellingEvents"
                :key="event.id"
                :to="`/events/${event.id}`"
                class="relative h-[310px] overflow-hidden rounded-sm bg-slate-900 p-4 text-white"
              >
                <div class="absolute inset-0 opacity-95" :class="posterClass(event.id)" />
                <div class="absolute inset-0 bg-gradient-to-t from-black via-black/10 to-transparent" />
                <div class="relative flex h-full flex-col justify-end">
                  <h3 class="text-xl font-black">{{ event.name }}</h3>
                  <p class="mt-1 text-sm font-semibold text-red-400">{{ ticketsLeft(event) }} tickets left!</p>
                </div>
              </RouterLink>
            </div>
          </section>

          <EventStrip class="mt-16" title="Browse Arts" :events="artsEvents" />
          <EventStrip class="mt-16" title="Browse Concerts" :events="concertEvents" />
        </template>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineComponent, h, onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';

import EmptyState from '@/components/EmptyState.vue';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import { useEventStore } from '@/stores/eventStore';
import type { Event } from '@/types/event';
import { formatCurrency } from '@/utils/formatters';

const router = useRouter();
const eventStore = useEventStore();
const query = ref('');
const hasDate = ref(false);
const heroPeople = [1, 2, 3, 4, 5, 6, 7, 8];

const sortedEvents = computed(() =>
  [...eventStore.events].sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime()),
);
const upcomingEvents = computed(() => pick(0, 4));
const topSellingEvents = computed(() => pick(4, 3));
const artsEvents = computed(() => pick(7, 4));
const concertEvents = computed(() => pick(11, 4));

const SectionHeader = defineComponent({
  props: { title: { type: String, required: true } },
  setup(props) {
    return () =>
      h('div', { class: 'flex items-center justify-between' }, [
        h('h2', { class: 'text-lg font-black' }, props.title),
        h(RouterLink, { to: '/search', class: 'text-[11px] font-semibold text-indigo-600' }, () => 'View All ›'),
      ]);
  },
});

const EventStrip = defineComponent({
  props: {
    title: { type: String, required: true },
    events: { type: Array as () => Event[], required: true },
  },
  setup(props) {
    return () =>
      h('section', [
        h(SectionHeader, { title: props.title }),
        h(
          'div',
          { class: 'mt-6 grid grid-cols-4 gap-4' },
          props.events.map((event) =>
            h(
              RouterLink,
              {
                key: event.id,
                to: `/events/${event.id}`,
                class: 'block overflow-hidden rounded-sm border border-slate-200 bg-white transition hover:-translate-y-0.5 hover:shadow-md',
              },
              () => [
                h('div', { class: ['h-[126px]', posterClass(event.id)] }),
                h('div', { class: 'grid grid-cols-[38px_1fr] gap-3 p-3' }, [
                  h('div', { class: 'text-center text-[9px] font-bold uppercase text-slate-700' }, [
                    h('div', dateMonth(event.eventDate)),
                    h('div', { class: 'mt-1 text-slate-950' }, dateDay(event.eventDate)),
                  ]),
                  h('div', [
                    h('h3', { class: 'line-clamp-2 text-[12px] font-black leading-4 text-slate-950' }, event.name),
                    h('p', { class: 'mt-2 truncate text-[10px] text-slate-500' }, formatCurrency(startingPrice(event))),
                    h('p', { class: 'mt-1 truncate text-[10px] text-slate-500' }, event.venue),
                  ]),
                ]),
              ],
            ),
          ),
        ),
        h('div', { class: 'mx-auto mt-7 h-px w-20 bg-slate-300' }),
      ]);
  },
});

onMounted(() => {
  void eventStore.loadEvents();
});

function goSearch() {
  if (query.value) {
    void router.push({ path: '/search', query: { q: query.value } });
  }
}

function pick(start: number, count: number): Event[] {
  const slice = sortedEvents.value.slice(start, start + count);
  return slice.length === count ? slice : sortedEvents.value.slice(0, count);
}

function startingPrice(event: Event): number {
  if (event.ticketTypes.length === 0) return 0;
  return Math.min(...event.ticketTypes.map((ticket) => Number(ticket.price)));
}

function ticketsLeft(event: Event): number {
  return event.ticketTypes.reduce((sum, ticket) => sum + ticket.remainingQuantity, 0);
}

function dateMonth(value: string): string {
  return new Intl.DateTimeFormat('en-US', { month: 'short' }).format(new Date(value));
}

function dateDay(value: string): string {
  return new Intl.DateTimeFormat('en-US', { day: '2-digit' }).format(new Date(value));
}

function posterClass(id: number): string {
  const classes = [
    'bg-gradient-to-br from-purple-700 via-pink-500 to-orange-300',
    'bg-gradient-to-br from-slate-900 via-amber-700 to-yellow-300',
    'bg-gradient-to-br from-indigo-900 via-blue-500 to-cyan-300',
    'bg-gradient-to-br from-zinc-900 via-zinc-500 to-white',
    'bg-gradient-to-br from-violet-900 via-indigo-500 to-fuchsia-300',
  ];
  return classes[id % classes.length];
}
</script>
