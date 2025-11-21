/*
 * @Author: aquamarine5 && aquamarine5_@outlook.com
 * Copyright (c) 2025 by @aquamarine5, RC. All Rights Reversed.
 * lovely lonely, but be a quokka.
 */
import { createRouter, createWebHistory } from 'vue-router'
import GameView from '../views/GameView.vue'
import AdminView from '../views/AdminView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: GameView,
    },
    {
      path: '/result',
      name: "result",
      component: () => import('../views/ResultView.vue')
    }
    ,
    {
      path: '/admin',
      name: 'admin',
      component: AdminView
    }
  ],
})

export default router
