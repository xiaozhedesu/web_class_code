<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/axios'

interface Order {
  id: number
  userId: number
  username: string
  realName: string
  serverId: number
  months: number
  totalPrice: number
  status: string
  orderTime: string
}

interface Server {
  id: number
  model: string
  pricePerMonth: number
  isAvailable: boolean
}

const orders = ref<Order[]>([])
const loading = ref(false)
const isAdmin = localStorage.getItem('role') === 'ADMIN'

const STATUS_MAP: Record<string, string> = {
  PENDING: '待支付',
  PAID: '已支付',
  CANCELLED: '已取消',
  COMPLETED: '已完成',
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { PENDING: 'warning', PAID: 'success', CANCELLED: 'info', COMPLETED: '' }
  return map[status] || 'info'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const url = isAdmin ? '/admin/orders' : '/user/orders'
    const res = await api.get(url)
    orders.value = res.data
  } finally {
    loading.value = false
  }
}

// 管理员：修改状态
const statusDialogVisible = ref(false)
const currentOrder = ref<Order | null>(null)
const newStatus = ref('')
const allowedStatuses = ['PENDING', 'PAID', 'CANCELLED', 'COMPLETED']

const openStatusEdit = (order: Order) => {
  currentOrder.value = order
  newStatus.value = order.status
  statusDialogVisible.value = true
}

const handleStatusSave = async () => {
  if (!currentOrder.value) return
  try {
    await api.put(`/admin/orders/${currentOrder.value.id}/status`, { status: newStatus.value })
    ElMessage.success('修改成功')
    statusDialogVisible.value = false
    fetchOrders()
  } catch {
    // 错误已在拦截器处理
  }
}

// 用户：创建订单
const createDialogVisible = ref(false)
const servers = ref<Server[]>([])
const selectedServer = ref<number | null>(null)
const months = ref(1)

const openCreate = async () => {
  const res = await api.get('/servers')
  servers.value = res.data.filter((s: Server) => s.isAvailable)
  selectedServer.value = null
  months.value = 1
  createDialogVisible.value = true
}

const handleCreate = async () => {
  if (!selectedServer.value) {
    ElMessage.warning('请选择服务器')
    return
  }
  try {
    await api.post('/user/orders', { serverId: selectedServer.value, months: months.value })
    ElMessage.success('下单成功')
    createDialogVisible.value = false
    fetchOrders()
  } catch {
    // 错误已在拦截器处理
  }
}

onMounted(fetchOrders)
</script>

<template>
  <div class="order-list">
    <el-card>
      <div class="toolbar" v-if="!isAdmin">
        <el-button type="primary" @click="openCreate">新建订单</el-button>
      </div>

      <el-table :data="orders" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column v-if="isAdmin" prop="username" label="用户" width="100" />
        <el-table-column v-if="isAdmin" prop="realName" label="姓名" width="100" />
        <el-table-column prop="serverId" label="套餐ID" width="80" />
        <el-table-column prop="months" label="月数" width="60" />
        <el-table-column prop="totalPrice" label="总价(元)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ STATUS_MAP[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" min-width="180" />
        <el-table-column v-if="isAdmin" label="操作" width="80">
          <template #default="{ row }">
            <el-button size="small" @click="openStatusEdit(row)">状态</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 管理员修改状态 -->
    <el-dialog v-model="statusDialogVisible" title="修改订单状态" width="360px">
      <el-form>
        <el-form-item label="状态">
          <el-select v-model="newStatus" style="width: 100%">
            <el-option v-for="s in allowedStatuses" :key="s" :label="STATUS_MAP[s]" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 用户创建订单 -->
    <el-dialog v-model="createDialogVisible" title="新建租赁订单" width="420px">
      <el-form>
        <el-form-item label="服务器套餐">
          <el-select v-model="selectedServer" placeholder="请选择套餐" style="width: 100%">
            <el-option v-for="s in servers" :key="s.id" :label="`${s.model} (￥${s.pricePerMonth}/月)`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="租赁月数">
          <el-input-number v-model="months" :min="1" :max="36" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="selectedServer" label="预估总价">
          <span style="font-size: 18px; color: #e6a23c; font-weight: bold">
            ￥{{ (servers.find(s => s.id === selectedServer)?.pricePerMonth || 0) * months }}
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-list {
  height: 100%;
}

.toolbar {
  margin-bottom: 16px;
}
</style>
