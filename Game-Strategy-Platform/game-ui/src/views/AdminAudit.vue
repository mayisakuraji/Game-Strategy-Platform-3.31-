<template>
  <div class="admin-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>攻略审核管理</span>
          <el-button type="primary" @click="fetchData">刷新列表</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="gameTag" label="游戏分类" width="120" align="center">
          <template #default="scope">
            <el-tag>{{ scope.row.gameTag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" align="center" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button size="small" @click="handlePreview(scope.row)">预览内容</el-button>
            <el-button size="small" type="success" @click="handleAudit(scope.row, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="handleAudit(scope.row, 2)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Preview Dialog -->
    <el-dialog v-model="dialogVisible" title="攻略内容预览" width="70%" top="5vh">
      <div v-if="currentStrategy" class="preview-content">
        <h2>{{ currentStrategy.title }}</h2>
        <div class="meta-info">
          <el-tag type="info">{{ currentStrategy.gameTag }}</el-tag>
          <span style="margin-left: 10px; color: #999;">提交于: {{ currentStrategy.createTime }}</span>
        </div>
        
        <el-divider />
        
        <div v-if="currentStrategy.coverImage" class="cover-image-container">
          <img :src="currentStrategy.coverImage" alt="封面图" style="max-width: 100%; max-height: 400px; border-radius: 8px;" />
        </div>
        
        <div class="content-body" style="white-space: pre-wrap; margin-top: 20px; line-height: 1.6;">
            {{ currentStrategy.content }}
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="success" @click="confirmAudit(1)">通过审核</el-button>
          <el-button type="danger" @click="confirmAudit(2)">驳回发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const currentStrategy = ref(null)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/strategy/admin/list?status=0')
    if (res.code === 200) {
      tableData.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handlePreview = (row) => {
  currentStrategy.value = row
  dialogVisible.value = true
}

const handleAudit = async (row, status) => {
  const actionText = status === 1 ? '通过' : '驳回'
  
  try {
    await ElMessageBox.confirm(`确定要${actionText}该攻略吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: status === 1 ? 'success' : 'warning'
    })
    
    const res = await request.put(`/strategy/admin/audit/${row.id}?status=${status}`)
    if (res.code === 200) {
      ElMessage.success(`已${actionText}`)
      fetchData() // Refresh list
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const confirmAudit = async (status) => {
    if (currentStrategy.value) {
        // Close dialog first, then trigger audit flow
        dialogVisible.value = false
        await handleAudit(currentStrategy.value, status)
    }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.admin-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.cover-image-container {
  text-align: center;
  background: #f5f7fa;
  padding: 10px;
  margin-bottom: 20px;
  border-radius: 4px;
}
</style>
