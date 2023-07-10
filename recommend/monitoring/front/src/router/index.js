import Vue from 'vue'
import VueRouter from 'vue-router'
import CrawlingView from '../views/CrawlingView.vue'
import ErrorView from '../views/ErrorView'
import DataView from '../views/DataView'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'crawling',
    component: CrawlingView
  },
  {
    path: '/data',
    name: 'data',
    component: DataView
  },
  {
    path: '/error',
    name: 'error',
    component: ErrorView
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
