<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import api from "@/utils/axios";

interface Server {
    id: number;
    model: string;
    cpu: string;
    ram: string;
    disk: string;
    pricePerMonth: number;
    isAvailable: boolean;
}

const servers = ref<Server[]>([]);
const loading = ref(false);

// 编辑/新增弹窗
const dialogVisible = ref(false);
const isEditing = ref(false);
const editingServer = ref<Server | null>(null);
const form = ref({ model: "", cpu: "", ram: "", disk: "", pricePerMonth: 0, isAvailable: true });

const fetchServers = async () => {
    loading.value = true;
    try {
        const res = await api.get("/servers");
        servers.value = res.data;
    } finally {
        loading.value = false;
    }
};

const openAdd = () => {
    isEditing.value = false;
    form.value = { model: "", cpu: "", ram: "", disk: "", pricePerMonth: 0, isAvailable: true };
    dialogVisible.value = true;
};

const openEdit = (server: Server) => {
    isEditing.value = true;
    editingServer.value = server;
    form.value = { ...server };
    dialogVisible.value = true;
};

const handleSave = async () => {
    if (!form.value.model || !form.value.pricePerMonth) {
        ElMessage.warning("型号和月费不能为空");
        return;
    }
    try {
        if (isEditing.value && editingServer.value) {
            await api.put(`/admin/servers/${editingServer.value.id}`, form.value);
            ElMessage.success("修改成功");
        } else {
            await api.post("/admin/servers", form.value);
            ElMessage.success("新增成功");
        }
        dialogVisible.value = false;
        fetchServers();
    } catch {
        // 错误已在拦截器处理
    }
};

const handleDelete = async (id: number) => {
    try {
        await ElMessageBox.confirm("确定要删除该套餐吗？", "提示", { type: "warning" });
        await api.delete(`/admin/servers/${id}`);
        ElMessage.success("删除成功");
        fetchServers();
    } catch {
        // 取消或错误
    }
};

onMounted(fetchServers);
</script>

<template>
    <div class="server-list">
        <el-card>
            <div class="toolbar">
                <el-button
                    type="primary"
                    @click="openAdd"
                    >新增套餐</el-button
                >
            </div>

            <el-table
                :data="servers"
                v-loading="loading"
                stripe
                style="width: 100%"
            >
                <el-table-column
                    prop="id"
                    label="ID"
                    width="60"
                />
                <el-table-column
                    prop="model"
                    label="型号"
                    min-width="140"
                />
                <el-table-column
                    prop="cpu"
                    label="CPU"
                    width="80"
                />
                <el-table-column
                    prop="ram"
                    label="内存"
                    width="80"
                />
                <el-table-column
                    prop="disk"
                    label="磁盘"
                    min-width="120"
                />
                <el-table-column
                    prop="pricePerMonth"
                    label="月费(元)"
                    width="100"
                />
                <el-table-column
                    prop="isAvailable"
                    label="可用"
                    width="80"
                >
                    <template #default="{ row }">
                        <el-tag
                            :type="row.isAvailable ? 'success' : 'info'"
                            size="small"
                        >
                            {{ row.isAvailable ? "可用" : "已下架" }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column
                    label="操作"
                    width="160"
                >
                    <template #default="{ row }">
                        <el-button
                            size="small"
                            @click="openEdit(row)"
                            >编辑</el-button
                        >
                        <el-button
                            size="small"
                            type="danger"
                            @click="handleDelete(row.id)"
                            >删除</el-button
                        >
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 新增/编辑弹窗 -->
        <el-dialog
            v-model="dialogVisible"
            :title="isEditing ? '修改套餐' : '新增套餐'"
            width="480px"
        >
            <el-form :model="form">
                <el-form-item label="型号">
                    <el-input v-model="form.model" />
                </el-form-item>
                <el-form-item label="CPU">
                    <el-input v-model="form.cpu" />
                </el-form-item>
                <el-form-item label="内存">
                    <el-input v-model="form.ram" />
                </el-form-item>
                <el-form-item label="磁盘">
                    <el-input v-model="form.disk" />
                </el-form-item>
                <el-form-item label="月费(元)">
                    <el-input-number
                        v-model="form.pricePerMonth"
                        :min="0"
                        :precision="2"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="上架">
                    <el-switch v-model="form.isAvailable" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button
                    type="primary"
                    @click="handleSave"
                    >保存</el-button
                >
            </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.server-list {
    height: 100%;
}

.toolbar {
    margin-bottom: 16px;
}
</style>
