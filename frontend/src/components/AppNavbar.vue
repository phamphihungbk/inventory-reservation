<template>
  <header class="border-b border-slate-200 bg-white">
    <nav class="mx-auto flex h-16 max-w-[1180px] items-center justify-between px-8">
      <RouterLink to="/" class="flex items-center gap-3">
        <img class="h-10 w-10" src="/ticketflow-mark.svg" alt="" />
        <span class="text-2xl font-black tracking-tight text-slate-950">
          TicketFlow<span class="text-indigo-600">.</span>
        </span>
      </RouterLink>

      <div class="hidden items-center gap-14 text-sm font-medium text-slate-900 md:flex">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          class="hover:text-indigo-600"
          :to="item.to"
        >
          {{ item.label }}
        </RouterLink>
      </div>

      <div class="hidden items-center gap-4 md:flex">
        <button class="rounded border border-indigo-500 px-4 py-2 text-xs font-semibold text-indigo-600 transition hover:bg-indigo-50" type="button">
          Log In
        </button>
        <button class="rounded bg-indigo-600 px-4 py-2 text-xs font-semibold text-white transition hover:bg-indigo-700" type="button">
          Sign Up
        </button>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';

const navItems = [
  { label: 'Concerts', to: '/' },
  { label: 'Search', to: '/search' },
  { label: 'Notifications', to: '/admin/notifications' },
];
</script>
