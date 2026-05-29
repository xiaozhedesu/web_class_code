<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/utils/axios'

interface Server {
  id: number
  model: string
  cpu: string
  ram: string
  disk: string
  pricePerMonth: number
  isAvailable: boolean
}

const router = useRouter()
const servers = ref<Server[]>([])
const loading = ref(false)

// 购买弹窗
const dialogVisible = ref(false)
const selectedServer = ref<Server | null>(null)
const months = ref(1)

const fetchServers = async () => {
  loading.value = true
  try {
    const res = await api.get('/servers')
    servers.value = res.data.filter((s: Server) => s.isAvailable)
  } finally {
    loading.value = false
  }
}

const openBuy = (server: Server) => {
  selectedServer.value = server
  months.value = 1
  dialogVisible.value = true
}

const handleBuy = async () => {
  if (!selectedServer.value) return
  try {
    await api.post('/user/orders', {
      serverId: selectedServer.value.id,
      months: months.value,
    })
    ElMessage.success('下单成功')
    dialogVisible.value = false
    router.push('/orders')
  } catch {
    // 错误已在拦截器处理
  }
}

onMounted(fetchServers)
</script>

<template>
  <div class="catalog">
    <h2>服务器套餐</h2>
    <el-row :gutter="20" v-loading="loading">
      <el-col v-for="server in servers" :key="server.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="server-card" shadow="hover">
          <template #header>
            <span class="card-title">{{ server.model }}</span>
          </template>
          <div class="specs">
            <p><strong>CPU：</strong>{{ server.cpu }}</p>
            <p><strong>内存：</strong>{{ server.ram }}</p>
            <p><strong>磁盘：</strong>{{ server.disk }}</p>
            <p class="price">¥{{ server.pricePerMonth }} <span>/月</span></p>
          </div>
          <div class="card-footer">
            <el-button type="primary" @click="openBuy(server)">购买</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 购买弹窗 -->
    <el-dialog v-model="dialogVisible" title="租赁套餐" width="400px">
      <template v-if="selectedServer">
        <p style="margin-bottom: 16px">套餐：<strong>{{ selectedServer.model }}</strong>（¥{{ selectedServer.pricePerMonth }}/月）</p>
        <el-form>
          <el-form-item label="租赁月数">
            <el-input-number v-model="months" :min="1" :max="36" style="width: 100%" />
          </el-form-item>
          <el-form-item label="预估总价">
            <span class="total-price">¥{{ selectedServer.pricePerMonth * months }}</span>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBuy">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.catalog {
  height: 100%;
}

.catalog h2 {
  margin-bottom: 20px;
}

.server-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
}

.specs p {
  margin: 6px 0;
  font-size: 14px;
}

.price {
  font-size: 20px;
  color: #e6a23c;
  font-weight: bold;
  margin-top: 12px !important;
}

.price span {
  font-size: 14px;
  font-weight: normal;
  color: #999;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.total-price {
  font-size: 22px;
  color: #e6a23c;
  font-weight: bold;
}
</style>
