<template>
  <article class="panel p-5">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <div class="flex flex-wrap items-center gap-2">
          <h2 class="text-base font-semibold text-slate-950">Reservation #{{ reservation.id }}</h2>
          <span :class="statusClass" class="rounded-full px-2.5 py-1 text-xs font-semibold">
            {{ reservation.status }}
          </span>
        </div>
        <dl class="mt-4 grid grid-cols-2 gap-x-6 gap-y-3 text-sm sm:grid-cols-4">
          <div>
            <dt class="text-slate-500">Product</dt>
            <dd class="mt-1 font-semibold text-slate-900">#{{ reservation.productId }}</dd>
          </div>
          <div>
            <dt class="text-slate-500">Quantity</dt>
            <dd class="mt-1 font-semibold text-slate-900">{{ reservation.quantity }}</dd>
          </div>
          <div>
            <dt class="text-slate-500">Expires</dt>
            <dd class="mt-1 font-semibold text-slate-900">{{ formatDateTime(reservation.expiresAt) }}</dd>
          </div>
          <div>
            <dt class="text-slate-500">Created</dt>
            <dd class="mt-1 font-semibold text-slate-900">{{ formatDateTime(reservation.createdAt) }}</dd>
          </div>
        </dl>
      </div>

      <button
        v-if="reservation.status === 'ACTIVE'"
        class="btn-secondary sm:min-w-24"
        type="button"
        :disabled="isCanceling"
        @click="$emit('cancel', reservation.id)"
      >
        {{ isCanceling ? 'Canceling' : 'Cancel' }}
      </button>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';

import type { Reservation } from '@/types/reservation';
import { formatDateTime } from '@/utils/formatters';

const props = defineProps<{
  reservation: Reservation;
  isCanceling?: boolean;
}>();

defineEmits<{
  cancel: [id: number];
}>();

const statusClass = computed(() => {
  if (props.reservation.status === 'ACTIVE') {
    return 'bg-green-50 text-green-700';
  }

  if (props.reservation.status === 'CANCELED') {
    return 'bg-slate-100 text-slate-700';
  }

  return 'bg-amber-50 text-amber-700';
});
</script>
