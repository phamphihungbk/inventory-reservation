<template>
  <div class="panel flex flex-col items-center justify-center px-6 py-12 text-center">
    <div class="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-slate-100 text-xl">
      {{ icon }}
    </div>
    <h2 class="text-lg font-semibold text-slate-950">{{ title }}</h2>
    <p v-if="message" class="mt-2 max-w-md text-sm leading-6 text-slate-500">{{ message }}</p>
    <div v-if="$slots.action" class="mt-6">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string;
    message?: string;
    icon?: string;
  }>(),
  {
    icon: '[]',
  },
);
</script>
