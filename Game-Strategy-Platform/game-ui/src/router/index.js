import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    { path: '/create', name: 'create', component: () => import('../views/CreateStrategy.vue') },
    { path: '/admin', name: 'admin-audit', component: () => import('../views/AdminAudit.vue') },
    { path: '/strategy/:id', name: 'strategy-detail', component: () => import('../views/StrategyDetail.vue') },
    { path: '/profile', name: 'user-profile', component: () => import('../views/UserProfile.vue') },
    { path: '/search', name: 'search', component: () => import('../views/Search.vue') }
  ]
})

export default router
