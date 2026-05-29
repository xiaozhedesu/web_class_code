<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from "@/utils/axios";
import { ArrowDown } from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();

const isAdmin = localStorage.getItem("role") === "ADMIN";
const username = ref("");

onMounted(async () => {
    try {
        const res = await api.get("/user/me");
        username.value = res.data.username;
    } catch {
        username.value = "unknown";
    }
});

const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    router.push("/login");
};

const handleCommand = (cmd: string) => {
    if (cmd === "profile") {
        router.push("/profile");
    } else if (cmd === "logout") {
        handleLogout();
    }
};

const adminMenus = [
    { path: "/users", label: "用户管理" },
    { path: "/servers", label: "服务器管理" },
    { path: "/orders", label: "订单管理" },
];

const userMenus = [
    { path: "/catalog", label: "套餐列表" },
    { path: "/orders", label: "我的订单" },
];
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
                    <el-menu-item
                        v-for="item in adminMenus"
                        :key="item.path"
                        :index="item.path"
                        @click="router.push(item.path)"
                    >
                        {{ item.label }}
                    </el-menu-item>
                </template>
                <template v-else>
                    <el-menu-item
                        v-for="item in userMenus"
                        :key="item.path"
                        :index="item.path"
                        @click="router.push(item.path)"
                    >
                        {{ item.label }}
                    </el-menu-item>
                </template>
            </el-menu>

            <el-dropdown @command="handleCommand">
                <span
                    class="username"
                    :class="isAdmin ? 'admin' : 'user'"
                >
                    {{ username }}
                    <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="profile">我的信息</el-dropdown-item>
                        <el-dropdown-item
                            command="logout"
                            divided
                            >退出登录</el-dropdown-item
                        >
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
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

.username {
    cursor: pointer;
    font-size: 14px;
    font-weight: bold;
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background-color 0.2s;
}

.username:hover {
    background-color: #f5f7fa;
}

.username.admin {
    color: #f56c6c;
}

.username.user {
    color: #409eff;
}

:deep(.el-main) {
    padding: 20px;
}
</style>
