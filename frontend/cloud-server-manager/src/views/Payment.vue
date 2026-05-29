<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/utils/axios'

interface Order {
  id: number
  serverId: number
  months: number
  totalPrice: number
  status: string
}

const route = useRoute()
const router = useRouter()
const order = ref<Order | null>(null)
const loading = ref(false)
const processing = ref(false)

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await api.get(`/user/orders/${route.params.id}`)
    order.value = res.data
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  if (!order.value) return
  processing.value = true
  try {
    await api.put(`/user/orders/${order.value.id}/status`, { status: 'CANCELLED' })
    ElMessage.info('订单已取消')
    router.push('/catalog')
  } finally {
    processing.value = false
  }
}

const handlePay = async () => {
  if (!order.value) return
  processing.value = true
  try {
    await api.put(`/user/orders/${order.value.id}/status`, { status: 'PAID' })
    ElMessage.success('订单已支付')
    router.push('/orders')
  } finally {
    processing.value = false
  }
}

onMounted(fetchOrder)
</script>

<template>
  <div class="payment" v-loading="loading">
    <el-card v-if="order" class="payment-card">
      <h2>订单支付</h2>
      <div class="order-info">
        <p>订单编号：<strong>#{{ order.id }}</strong></p>
        <p>服务器套餐：<strong>ID-{{ order.serverId }}</strong></p>
        <p>租赁月数：<strong>{{ order.months }} 个月</strong></p>
        <p class="total">应付金额：<span>¥{{ order.totalPrice }}</span></p>
      </div>
      <div class="actions">
        <el-button size="large" :loading="processing" @click="handleCancel">取消订单</el-button>
        <el-button size="large" type="primary" :loading="processing" @click="handlePay">进行支付</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.payment {
  display: flex;
  justify-content: center;
  padding-top: 60px;
  height: 100%;
}

.payment-card {
  width: 480px;
  align-self: flex-start;
}

.payment-card h2 {
  text-align: center;
  margin-bottom: 24px;
}

.order-info p {
  margin: 10px 0;
  font-size: 15px;
}

.total {
  font-size: 18px;
  margin-top: 20px !important;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.total span {
  font-size: 24px;
  color: #e6a23c;
  font-weight: bold;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
}
</style>
