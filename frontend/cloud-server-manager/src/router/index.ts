import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/users',
      name: 'UserList',
      component: () => import('@/views/UserList.vue'),
    },
    {
      path: '/servers',
      name: 'ServerList',
      component: () => import('@/views/ServerList.vue'),
    },
    {
      path: '/orders',
      name: 'OrderList',
      component: () => import('@/views/OrderList.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login',
    },
  ],
})

export default router
