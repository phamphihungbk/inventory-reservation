<template>
  <article class="panel flex h-full flex-col p-5">
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-slate-950">{{ product.name }}</h2>
        <p class="mt-1 text-sm text-slate-500">Product #{{ product.id }}</p>
      </div>
      <span v-if="product.stock < 5" class="rounded-full bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-700">
        Low Stock
      </span>
    </div>

    <div class="mt-5 grid grid-cols-2 gap-3">
      <div class="rounded-lg bg-slate-50 p-3">
        <p class="text-xs font-medium uppercase text-slate-500">Price</p>
        <p class="mt-1 text-base font-semibold text-slate-950">{{ formatCurrency(Number(product.price)) }}</p>
      </div>
      <div class="rounded-lg bg-slate-50 p-3">
        <p class="text-xs font-medium uppercase text-slate-500">Stock</p>
        <p class="mt-1 text-base font-semibold text-slate-950">{{ product.stock }}</p>
      </div>
    </div>

    <form class="mt-5 flex flex-1 flex-col justify-end gap-3" @submit.prevent="submit">
      <label class="text-sm font-medium text-slate-700" :for="quantityId">Reserve quantity</label>
      <div class="flex gap-2">
        <input
          :id="quantityId"
          v-model.number="quantity"
          class="input-field"
          type="number"
          min="1"
          :max="Math.max(product.stock, 1)"
          :disabled="product.stock === 0 || isSubmitting"
        />
        <button class="btn-primary min-w-24" type="submit" :disabled="!canReserve || isSubmitting">
          {{ isSubmitting ? 'Reserving' : 'Reserve' }}
        </button>
      </div>
      <p v-if="error" class="text-sm text-red-600">{{ error }}</p>
    </form>
  </article>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import type { Product } from '@/types/product';
import { formatCurrency } from '@/utils/formatters';

const props = defineProps<{
  product: Product;
  isSubmitting?: boolean;
}>();

const emit = defineEmits<{
  reserve: [payload: { productId: number; quantity: number }];
}>();

const quantity = ref(1);
const error = ref<string | null>(null);
const quantityId = `reserve-${props.product.id}`;

const canReserve = computed(
  () => props.product.stock > 0 && quantity.value >= 1 && quantity.value <= props.product.stock,
);

function submit() {
  error.value = null;

  if (!canReserve.value) {
    error.value =
      props.product.stock === 0
        ? 'This product is out of stock.'
        : 'Quantity must fit current stock.';
    return;
  }

  emit('reserve', {
    productId: props.product.id,
    quantity: quantity.value,
  });
}
</script>
