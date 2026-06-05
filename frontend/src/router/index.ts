import { createRouter, createWebHistory } from 'vue-router';

import CheckoutPage from '@/pages/CheckoutPage.vue';
import EventDetailPage from '@/pages/EventDetailPage.vue';
import HomePage from '@/pages/HomePage.vue';
import NotificationDebugPage from '@/pages/NotificationDebugPage.vue';
import OrderSuccessPage from '@/pages/OrderSuccessPage.vue';
import ReservationPage from '@/pages/ReservationPage.vue';
import SearchPage from '@/pages/SearchPage.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/search',
      name: 'search',
      component: SearchPage,
    },
    {
      path: '/events/:id',
      name: 'event-detail',
      component: EventDetailPage,
    },
    {
      path: '/reservations/:id',
      name: 'reservation',
      component: ReservationPage,
    },
    {
      path: '/checkout/:reservationId',
      name: 'checkout',
      component: CheckoutPage,
    },
    {
      path: '/orders/:id',
      name: 'order-success',
      component: OrderSuccessPage,
    },
    {
      path: '/admin/notifications',
      name: 'notification-debug',
      component: NotificationDebugPage,
    },
  ],
});

export default router;
