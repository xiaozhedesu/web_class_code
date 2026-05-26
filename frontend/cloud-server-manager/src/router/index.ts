import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: "/login",
            name: "Login",
            component: () => import("@/views/Login.vue"),
        },
        {
            path: "/register",
            name: "Register",
            component: () => import("@/views/Register.vue"),
        },
        {
            path: "/",
            component: () => import("@/views/Layout.vue"),
            children: [
                {
                    path: "users",
                    name: "UserList",
                    component: () => import("@/views/UserList.vue"),
                    meta: { requiresAdmin: true },
                },
                {
                    path: "servers",
                    name: "ServerList",
                    component: () => import("@/views/ServerList.vue"),
                    meta: { requiresAdmin: true },
                },
                {
                    path: "orders",
                    name: "OrderList",
                    component: () => import("@/views/OrderList.vue"),
                },
                {
                    path: "",
                    redirect: "/orders",
                },
            ],
        },
        {
            path: "/:pathMatch(.*)*",
            redirect: "/login",
        },
    ],
});

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    // 未登录 → 只能去 login/register
    if (!token) {
        if (to.path === "/login" || to.path === "/register") {
            next();
        } else {
            next("/login");
        }
        return;
    }

    // // 已登录 → 不能去 login/register
    // if (to.path === '/login' || to.path === '/register') {
    //   next('/orders')
    //   return
    // }

    // 管理员路由 → 非 ADMIN 重定向
    if (to.meta.requiresAdmin && role !== "ADMIN") {
        next("/orders");
        return;
    }

    next();
});

export default router;
