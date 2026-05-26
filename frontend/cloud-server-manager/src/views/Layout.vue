<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const isAdmin = localStorage.getItem('role') === 'ADMIN'

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  router.push('/login')
}

const adminMenus = [
  { path: '/users', label: '用户管理' },
  { path: '/servers', label: '服务器管理' },
  { path: '/orders', label: '订单管理' },
]

const userMenus = [
  { path: '/orders', label: '我的订单' },
]
</script>

<template>
  <el-container class="layout">
    <el-header class="header">
      <span class="title">服务器租赁管理系统</span>
      <el-menu
        :default-active="route.path"
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
      >
        <template v-if="isAdmin">
          <el-menu-item v-for="item in adminMenus" :key="item.path" :index="item.path"
            @click="router.push(item.path)">
            {{ item.label }}
          </el-menu-item>
        </template>
        <template v-else>
          <el-menu-item v-for="item in userMenus" :key="item.path" :index="item.path"
            @click="router.push(item.path)">
            {{ item.label }}
          </el-menu-item>
        </template>
      </el-menu>
      <el-button text @click="handleLogout">退出登录</el-button>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  white-space: nowrap;
  margin-right: 24px;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

:deep(.el-main) {
  padding: 20px;
}
</style>
