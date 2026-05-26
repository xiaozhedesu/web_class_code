<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/axios'

const ROLE_MAP: Record<string, string> = {
  ADMIN: '管理员',
  USER: '用户',
}

const getRoleLabel = (role: string) => ROLE_MAP[role] || role
const getRoleTagType = (role: string) => (role === 'ADMIN' ? 'danger' : '')

interface User {
  id: number
  username: string
  realName: string
  phone: string
  role: string
  createTime: string
}

const users = ref<User[]>([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 编辑弹窗
const dialogVisible = ref(false)
const editingUser = ref<User | null>(null)
const editForm = ref({ realName: '', phone: '' })

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await api.get('/admin/users', {
      params: { page: currentPage.value - 1, size: pageSize.value, keyword: keyword.value }
    })
    users.value = res.data.content
    total.value = res.data.totalElements
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handleEdit = (user: User) => {
  editingUser.value = user
  editForm.value = { realName: user.realName, phone: user.phone }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!editingUser.value) return
  try {
    await api.put(`/admin/users/${editingUser.value.id}`, editForm.value)
    ElMessage.success('修改成功')
    dialogVisible.value = false
    fetchUsers()
  } catch {
    // 错误已在拦截器处理
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    await api.delete(`/admin/users/${id}`)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {
    // 取消或错误
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchUsers()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchUsers()
}

onMounted(fetchUsers)
</script>

<template>
  <div class="user-list">
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索姓名"
          :prefix-icon="Search"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="users" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="修改用户信息" width="400px">
      <el-form :model="editForm">
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-list {
  height: 100%;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
