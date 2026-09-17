import axios from "axios";
import { ElMessage } from "element-plus";

const api = axios.create({
    baseURL: "/api",
    timeout: 10000,
});

// 请求拦截器：自动携带 token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 响应拦截器：解包 ApiResponse 并统一处理错误
api.interceptors.response.use(
    (response) => {
        const { code, message, data } = response.data;
        if (code === 200) {
            return data; // 直接返回业务数据
        }
        ElMessage.error(message || "请求失败");
        return Promise.reject(new Error(message));
    },
    (error) => {
        const message = error.response?.data?.message || "请求失败";
        ElMessage.error(message);
        if (error.response?.status === 401) {
            localStorage.removeItem("token");
            localStorage.removeItem("role");
            window.location.href = "/login";
        }
        return Promise.reject(error);
    },
);

export default api;
