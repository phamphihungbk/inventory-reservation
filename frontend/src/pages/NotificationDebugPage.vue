<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { fetchNotifications } from '@/api/notifications';
import EmptyState from '@/components/EmptyState.vue';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import type { NotificationDebug } from '@/types/notification';
import { formatDateTime } from '@/utils/formatters';

const notifications = ref<NotificationDebug[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);

async function loadNotifications() {
  isLoading.value = true;
  error.value = null;

  try {
    notifications.value = await fetchNotifications();
  } catch {
    error.value = 'Unable to load notification debug data.';
  } finally {
    isLoading.value = false;
  }
}

onMounted(loadNotifications);
</script>

<template>
  <section class="space-y-6">
    <div class="flex flex-col gap-3 border-b border-slate-200 pb-5 md:flex-row md:items-end md:justify-between">
      <div>
        <p class="text-sm font-semibold uppercase tracking-wide text-slate-500">Admin</p>
        <h1 class="mt-2 text-3xl font-bold text-slate-950">Notification Debug</h1>
      </div>
      <button
        class="rounded-md bg-slate-950 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
        type="button"
        @click="loadNotifications"
      >
        Refresh
      </button>
    </div>

    <div v-if="error" class="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
      {{ error }}
    </div>

    <div v-if="isLoading" class="flex justify-center py-16">
      <LoadingSpinner />
    </div>

    <EmptyState
      v-else-if="notifications.length === 0"
      title="No notifications yet"
      description="Create a reservation or complete checkout to see MailHog notification records."
    />

    <div v-else class="overflow-hidden rounded-lg border border-slate-200 bg-white">
      <table class="min-w-full divide-y divide-slate-200 text-sm">
        <thead class="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
          <tr>
            <th class="px-4 py-3">Event Type</th>
            <th class="px-4 py-3">Subject</th>
            <th class="px-4 py-3">Recipient</th>
            <th class="px-4 py-3">Status</th>
            <th class="px-4 py-3">Sent</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="notification in notifications" :key="notification.id">
            <td class="px-4 py-3 font-medium text-slate-900">{{ notification.eventType }}</td>
            <td class="px-4 py-3 text-slate-700">{{ notification.subject }}</td>
            <td class="px-4 py-3 text-slate-600">{{ notification.recipient }}</td>
            <td class="px-4 py-3">
              <span class="rounded-full bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-700">
                {{ notification.status }}
              </span>
            </td>
            <td class="px-4 py-3 text-slate-600">{{ formatDateTime(notification.sentAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
