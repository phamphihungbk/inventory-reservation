<template>
  <section v-if="paymentStore.order" class="mx-auto max-w-2xl">
    <div class="panel p-6 text-center">
      <p class="text-sm font-semibold uppercase text-green-700">Order confirmed</p>
      <h1 class="mt-2 text-3xl font-bold text-slate-950">{{ paymentStore.order.eventName }}</h1>
      <p class="mt-3 text-sm text-slate-500">{{ paymentStore.order.ticketTypeName }} x {{ paymentStore.order.quantity }}</p>
      <p class="mt-6 text-2xl font-bold text-slate-950">{{ formatCurrency(Number(paymentStore.order.totalAmount)) }}</p>
      <RouterLink to="/" class="btn-primary mt-6">Back home</RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { RouterLink, useRoute } from 'vue-router';

import { usePaymentStore } from '@/stores/paymentStore';
import { formatCurrency } from '@/utils/formatters';

const route = useRoute();
const paymentStore = usePaymentStore();

onMounted(() => {
  void paymentStore.loadOrder(Number(route.params.id));
});
</script>
