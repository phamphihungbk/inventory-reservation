<template>
  <header class="border-b border-slate-200 bg-white">
    <nav class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
      <RouterLink to="/" class="flex items-center gap-3">
        <span class="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-sm font-bold text-white">
          TM
        </span>
        <span>
          <span class="block text-base font-semibold text-slate-950">Ticketmaster MVP</span>
          <span class="block text-xs text-slate-500">Live ticket reservations</span>
        </span>
      </RouterLink>

      <div class="flex flex-wrap items-center gap-2">
        <RouterLink
          to="/"
          class="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-950"
          active-class="bg-slate-100 text-slate-950"
        >
          Home
        </RouterLink>
        <RouterLink
          to="/search"
          class="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-950"
          active-class="bg-slate-100 text-slate-950"
        >
          Search
        </RouterLink>
        <RouterLink
          to="/admin/notifications"
          class="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-950"
          active-class="bg-slate-100 text-slate-950"
        >
          Notifications
        </RouterLink>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>
