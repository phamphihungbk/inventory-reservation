<template>
  <div class="min-h-screen">
    <AppNavbar />

    <main :class="isFullBleed ? '' : 'mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8'">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { RouterView, useRoute } from 'vue-router';

import AppNavbar from '@/components/AppNavbar.vue';

const route = useRoute();
const isFullBleed = computed(() => route.path === '/' || route.path.startsWith('/events/'));
</script>
