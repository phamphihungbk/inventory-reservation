<template>
  <header class="border-b border-slate-200 bg-white">
    <nav class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
      <RouterLink to="/products" class="flex items-center gap-3">
        <span class="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-sm font-bold text-white">
          IR
        </span>
        <span>
          <span class="block text-base font-semibold text-slate-950">Inventory Reservation</span>
          <span class="block text-xs text-slate-500">Ecommerce stock operations</span>
        </span>
      </RouterLink>

      <div class="flex flex-wrap items-center gap-2">
        <RouterLink
          to="/products"
          class="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-950"
          active-class="bg-slate-100 text-slate-950"
        >
          Products
        </RouterLink>
        <RouterLink
          to="/reservations"
          class="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-950"
          active-class="bg-slate-100 text-slate-950"
        >
          Reservations
        </RouterLink>
        <RouterLink to="/products/new" class="btn-primary">
          New product
        </RouterLink>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>
