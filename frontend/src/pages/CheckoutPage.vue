<template>
  <section class="mx-auto max-w-xl">
    <form class="panel space-y-5 p-6" @submit.prevent="pay">
      <div>
        <p class="text-sm font-semibold uppercase text-blue-700">Checkout</p>
        <h1 class="mt-2 text-2xl font-bold text-slate-950">Simulated payment</h1>
        <p class="mt-2 text-sm text-slate-500">Use 4242424242424242 for success or 4000000000000002 for failure.</p>
      </div>
      <input v-model.trim="cardNumber" class="input-field" placeholder="Card number" />
      <div v-if="paymentStore.error" class="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
        {{ paymentStore.error }}
      </div>
      <button class="btn-primary w-full" type="submit" :disabled="paymentStore.isLoading">
        {{ paymentStore.isLoading ? 'Processing...' : 'Pay now' }}
      </button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { usePaymentStore } from '@/stores/paymentStore';

const route = useRoute();
const router = useRouter();
const paymentStore = usePaymentStore();
const cardNumber = ref('4242424242424242');

async function pay() {
  const payment = await paymentStore.pay({
    reservationId: Number(route.params.reservationId),
    cardNumber: cardNumber.value,
  });
  if (payment.status === 'SUCCEEDED' && payment.orderId) {
    await router.push(`/orders/${payment.orderId}`);
  }
}
</script>
