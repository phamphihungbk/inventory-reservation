<template>
  <section class="mx-auto max-w-2xl">
    <div>
      <p class="text-sm font-semibold uppercase text-blue-700">Create Product</p>
      <h1 class="mt-2 text-3xl font-bold text-slate-950">Add inventory item</h1>
      <p class="mt-2 text-sm leading-6 text-slate-500">
        New products become immediately available for reservations.
      </p>
    </div>

    <form class="panel mt-8 space-y-6 p-6" @submit.prevent="submit">
      <div>
        <label class="text-sm font-medium text-slate-700" for="name">Name</label>
        <input id="name" v-model.trim="form.name" class="input-field mt-2" placeholder="Runner Sneaker" />
        <p v-if="errors.name" class="mt-2 text-sm text-red-600">{{ errors.name }}</p>
      </div>

      <div class="grid gap-5 sm:grid-cols-2">
        <div>
          <label class="text-sm font-medium text-slate-700" for="stock">Stock</label>
          <input id="stock" v-model.number="form.stock" class="input-field mt-2" type="number" min="0" />
          <p v-if="errors.stock" class="mt-2 text-sm text-red-600">{{ errors.stock }}</p>
        </div>

        <div>
          <label class="text-sm font-medium text-slate-700" for="price">Price</label>
          <input id="price" v-model.number="form.price" class="input-field mt-2" type="number" min="0" step="0.01" />
          <p v-if="errors.price" class="mt-2 text-sm text-red-600">{{ errors.price }}</p>
        </div>
      </div>

      <div v-if="productStore.error" class="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
        {{ productStore.error }}
      </div>

      <div class="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
        <RouterLink to="/products" class="btn-secondary">Cancel</RouterLink>
        <button class="btn-primary" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Creating' : 'Create product' }}
        </button>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';

import { useProductStore } from '@/stores/productStore';

const router = useRouter();
const productStore = useProductStore();
const isSubmitting = ref(false);

const form = reactive({
  name: '',
  stock: 0,
  price: 0,
});

const errors = reactive({
  name: '',
  stock: '',
  price: '',
});

function validate(): boolean {
  errors.name = form.name ? '' : 'Name is required.';
  errors.stock = Number.isInteger(form.stock) && form.stock >= 0 ? '' : 'Stock must be zero or more.';
  errors.price = Number.isFinite(form.price) && form.price >= 0 ? '' : 'Price must be zero or more.';

  return !errors.name && !errors.stock && !errors.price;
}

async function submit() {
  if (!validate()) {
    return;
  }

  isSubmitting.value = true;

  try {
    await productStore.addProduct({
      name: form.name,
      stock: form.stock,
      price: Number(form.price.toFixed(2)),
    });
    await router.push('/products');
  } catch {
    // Store owns user-facing error message.
  } finally {
    isSubmitting.value = false;
  }
}
</script>
