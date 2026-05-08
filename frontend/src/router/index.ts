import { createRouter, createWebHistory } from 'vue-router';

import CreateProductPage from '@/pages/CreateProductPage.vue';
import ProductsPage from '@/pages/ProductsPage.vue';
import ReservationsPage from '@/pages/ReservationsPage.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/products',
    },
    {
      path: '/products',
      name: 'products',
      component: ProductsPage,
    },
    {
      path: '/products/new',
      name: 'create-product',
      component: CreateProductPage,
    },
    {
      path: '/reservations',
      name: 'reservations',
      component: ReservationsPage,
    },
  ],
});

export default router;
