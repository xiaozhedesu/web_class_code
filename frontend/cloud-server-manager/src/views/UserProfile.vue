<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/axios'

const user = ref({ username: '', realName: '', phone: '', role: '', createTime: '' })
const editing = ref(false)
const form = ref({ realName: '', phone: '' })
const loading = ref(false)

const fetchProfile = async () => {
  try {
    const res = await api.get('/user/me')
    user.value = res.data
  } catch { /* 错误已在拦截器处理 */ }
}

const startEdit = () => {
  form.value = { realName: user.value.realName, phone: user.value.phone }
  editing.value = true
}

const handleSave = async () => {
  loading.value = true
  try {
    const res = await api.put('/user/me', form.value)
    user.value = res.data
    editing.value = false
    ElMessage.success('修改成功')
  } finally {
    loading.value = false
  }
}

onMounted(fetchProfile)
</script>

<template>
  <div class="profile">
    <el-card class="profile-card">
      <h2>我的信息</h2>
      <el-descriptions :column="1" border v-if="!editing">
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ user.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="user.role === 'ADMIN' ? 'danger' : ''" size="small">
            {{ user.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-form :model="form" v-if="editing">
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button v-if="!editing" type="primary" @click="startEdit">编辑</el-button>
        <template v-else>
          <el-button @click="editing = false">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
        </template>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.profile {
  display: flex;
  justify-content: center;
  padding-top: 40px;
  height: 100%;
}

.profile-card {
  width: 500px;
  align-self: flex-start;
}

.profile-card h2 {
  text-align: center;
  margin-bottom: 24px;
}

.actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
