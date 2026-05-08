<template>
  <div class="min-h-screen">
    <AppNavbar />

    <main class="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';

import AppNavbar from '@/components/AppNavbar.vue';
</script>
