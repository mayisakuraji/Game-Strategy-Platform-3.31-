<template>
  <div class="profile-container">
    <div class="profile-header-bg"></div>
    
    <div class="profile-content-wrapper">
      <el-row :gutter="24">
        <!-- Sidebar Menu -->
        <el-col :span="6">
          <div class="profile-sidebar">
            <div class="user-info-card">
              <el-avatar :size="80" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" class="profile-avatar" />
              <h2 class="profile-name">{{ userStore.user.username }}</h2>
              <div class="profile-role">
                <el-tag :type="userStore.user.role === 'ROLE_ADMIN' ? 'danger' : 'info'" effect="dark" size="small">
                  {{ userStore.user.role === 'ROLE_ADMIN' ? 'ADMIN' : 'MEMBER' }}
                </el-tag>
              </div>
            </div>

            <div class="menu-list">
              <div 
                class="menu-item" 
                :class="{ active: activeTab === 'published' }"
                @click="changeTab('published')"
              >
                <el-icon><Document /></el-icon> 我发布的
              </div>
              <div 
                class="menu-item" 
                :class="{ active: activeTab === 'favorites' }"
                @click="changeTab('favorites')"
              >
                <el-icon><Star /></el-icon> 我的收藏
              </div>
              <div 
                v-if="userStore.user.role === 'ROLE_ADMIN'"
                class="menu-item" 
                :class="{ active: activeTab === 'audit' }"
                @click="changeTab('audit')"
              >
                <el-icon><Warning /></el-icon> 审核队列
                <span class="badge" v-if="auditCount > 0">{{ auditCount }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <!-- Main Content -->
        <el-col :span="18">
          <div class="content-panel">
            <div class="panel-header">
              <h3>{{ getTabTitle() }}</h3>
              <el-button v-if="activeTab === 'audit'" type="primary" link icon="Refresh" @click="fetchData">刷新</el-button>
            </div>

            <div class="panel-body" v-loading="loading">
              <template v-if="strategies.length > 0">
                <div v-for="item in strategies" :key="item.id" class="list-item">
                  <div class="item-content" @click="$router.push(`/strategy/${item.id}`)">
                    <h4 class="item-title">
                      {{ item.title }}
                      <el-tag v-if="activeTab === 'audit'" size="small" type="warning" class="status-tag">待审核</el-tag>
                    </h4>
                    <div class="item-meta">
                      <span class="meta-tag">{{ item.gameTag }}</span>
                      <span><el-icon><Calendar /></el-icon> {{ formatDate(item.createTime) }}</span>
                      <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                      
                      <span v-if="activeTab === 'published'" class="status-text" :class="'status-' + item.status">
                        {{ getStatusText(item.status) }}
                      </span>
                    </div>
                  </div>

                  <!-- Audit Actions -->
                  <div v-if="activeTab === 'audit'" class="item-actions">
                    <el-button type="success" size="small" @click.stop="handleAudit(item.id, 1)">
                      通过
                    </el-button>
                    <el-button type="danger" size="small" @click.stop="handleAudit(item.id, 2)">
                      驳回
                    </el-button>
                  </div>
                </div>
              </template>
              <el-empty v-else :description="getEmptyText()" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useUserStore } from '../stores/user'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { Document, Star, Warning, View, Calendar, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('published')
const strategies = ref([])
const loading = ref(false)
const auditCount = ref(0) // Could be fetched separately

const changeTab = (tab) => {
  activeTab.value = tab
  router.replace({ query: { ...route.query, tab } })
  fetchData()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const getStatusText = (status) => {
  const map = { 0: '审核中', 1: '已发布', 2: '被驳回' }
  return map[status] || '未知'
}

const getTabTitle = () => {
  if (activeTab.value === 'published') return '我发布的攻略'
  if (activeTab.value === 'favorites') return '我的收藏'
  if (activeTab.value === 'audit') return '审核管理'
  return ''
}

const getEmptyText = () => {
  if (activeTab.value === 'published') return '你还没有发布过攻略'
  if (activeTab.value === 'favorites') return '还没有收藏任何攻略'
  if (activeTab.value === 'audit') return '当前没有待审核的内容'
  return '暂无数据'
}

const fetchData = async () => {
  loading.value = true
  strategies.value = []
  try {
    let url = ''
    if (activeTab.value === 'published') {
      url = '/strategy/my'
    } else if (activeTab.value === 'favorites') {
      url = '/strategy/my-favorites'
    } else if (activeTab.value === 'audit') {
      url = '/strategy/admin/list?status=0'
    }
    
    if (url) {
      const res = await request.get(url)
      if (res.code === 200) {
        strategies.value = res.data
        if (activeTab.value === 'audit') {
            auditCount.value = strategies.value.length
        }
      }
    }
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const handleAudit = async (id, status) => {
  try {
    const res = await request.put(`/strategy/admin/audit/${id}?status=${status}`)
    if (res.code === 200) {
      ElMessage.success(status === 1 ? '已通过' : '已驳回')
      fetchData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

watch(() => route.query.tab, (newTab) => {
  if (newTab && ['published', 'favorites', 'audit'].includes(newTab)) {
    activeTab.value = newTab
    fetchData()
  }
})

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  fetchData()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  position: relative;
}

.profile-header-bg {
  height: 200px;
  background: linear-gradient(to right, #1f242c, #161b22);
  border-bottom: 1px solid var(--border-color);
}

.profile-content-wrapper {
  max-width: 1200px;
  margin: -100px auto 0;
  padding: 0 20px 40px;
  position: relative;
  z-index: 10;
}

.profile-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-info-card {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 24px;
  text-align: center;
}

.profile-avatar {
  border: 4px solid var(--bg-secondary);
  box-shadow: 0 0 0 1px var(--border-color);
}

.profile-name {
  margin: 16px 0 8px;
  font-size: 20px;
  color: var(--text-primary);
}

.menu-list {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
}

.menu-item {
  padding: 16px 20px;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background-color: var(--hover-color);
  color: var(--text-primary);
}

.menu-item.active {
  background-color: rgba(88, 166, 255, 0.1);
  color: var(--accent-color);
  border-left-color: var(--accent-color);
}

.content-panel {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  min-height: 500px;
}

.panel-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-item {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background-color 0.2s;
}

.list-item:hover {
  background-color: var(--hover-color);
}

.item-content {
  flex: 1;
  cursor: pointer;
}

.item-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-meta {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 16px;
}

.meta-tag {
  background-color: rgba(110, 118, 129, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.status-text {
  margin-left: auto;
  font-weight: 500;
}
.status-0 { color: #e3b341; }
.status-1 { color: #7ee787; }
.status-2 { color: #f85149; }

.item-actions {
  margin-left: 20px;
  display: flex;
  gap: 8px;
}
</style>
