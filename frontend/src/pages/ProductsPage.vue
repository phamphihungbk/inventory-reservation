<template>
  <section>
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase text-blue-700">Products</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-950">Inventory dashboard</h1>
        <p class="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
          Reserve stock, watch counts update, and keep backend inventory flow visible.
        </p>
      </div>
      <div class="flex flex-wrap gap-2">
        <button class="btn-secondary" type="button" :disabled="productStore.isLoading" @click="productStore.loadProducts()">
          Refresh inventory
        </button>
        <RouterLink to="/products/new" class="btn-primary">Create product</RouterLink>
      </div>
    </div>

    <div class="mt-6 grid gap-4 sm:grid-cols-3">
      <div class="panel p-4">
        <p class="text-sm text-slate-500">Products</p>
        <p class="mt-2 text-2xl font-bold text-slate-950">{{ productStore.products.length }}</p>
      </div>
      <div class="panel p-4">
        <p class="text-sm text-slate-500">Total stock</p>
        <p class="mt-2 text-2xl font-bold text-slate-950">{{ productStore.totalStock }}</p>
      </div>
      <div class="panel p-4">
        <p class="text-sm text-slate-500">Low stock</p>
        <p class="mt-2 text-2xl font-bold text-slate-950">{{ productStore.lowStockCount }}</p>
      </div>
    </div>

    <div v-if="productStore.error || reservationStore.error" class="mt-6 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
      {{ reservationStore.error ?? productStore.error }}
    </div>

    <div v-if="productStore.isLoading" class="mt-10 flex justify-center">
      <LoadingSpinner label="Loading products" />
    </div>

    <EmptyState
      v-else-if="productStore.products.length === 0"
      class="mt-8"
      title="No products yet"
      message="Create a product to test stock reservation against the Spring Boot API."
    >
      <template #action>
        <RouterLink to="/products/new" class="btn-primary">Create product</RouterLink>
      </template>
    </EmptyState>

    <div v-else class="mt-8 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <ProductCard
        v-for="product in productStore.products"
        :key="product.id"
        :product="product"
        :is-submitting="submittingProductId === product.id"
        @reserve="handleReserve"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';

import EmptyState from '@/components/EmptyState.vue';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import ProductCard from '@/components/ProductCard.vue';
import { useProductStore } from '@/stores/productStore';
import { useReservationStore } from '@/stores/reservationStore';

const productStore = useProductStore();
const reservationStore = useReservationStore();
const submittingProductId = ref<number | null>(null);

onMounted(() => {
  void productStore.loadProducts();
});

async function handleReserve(payload: { productId: number; quantity: number }) {
  submittingProductId.value = payload.productId;

  try {
    await reservationStore.reserveProduct(payload);
    await productStore.loadProducts();
  } catch {
    await productStore.loadProducts();
  } finally {
    submittingProductId.value = null;
  }
}
</script>
