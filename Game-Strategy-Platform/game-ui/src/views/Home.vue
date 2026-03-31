<template>
  <div class="home-container">
    <!-- Hero Banner -->
    <div class="hero-section">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">GAME STRATEGY</h1>
        <p class="hero-subtitle">汇聚全球顶尖玩家智慧 · 打造最强游戏攻略库</p>
        
        <div class="search-wrapper">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索游戏、攻略、技巧..." 
            class="hero-search"
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-container">
      <el-row :gutter="24">
        <!-- Left Content -->
        <el-col :span="18">
          <div class="section-header">
            <h2 class="section-title">
              <el-icon><Menu /></el-icon> 攻略中心
            </h2>
            <el-tabs v-model="activeTab" class="custom-tabs" @tab-click="handleTabClick">
              <el-tab-pane label="热门推荐" name="hot"></el-tab-pane>
              <el-tab-pane label="最新发布" name="new"></el-tab-pane>
              <el-tab-pane label="MOBA" name="MOBA"></el-tab-pane>
              <el-tab-pane label="FPS" name="FPS"></el-tab-pane>
              <el-tab-pane label="RPG" name="RPG"></el-tab-pane>
            </el-tabs>
          </div>

          <div class="strategy-grid" v-loading="loading">
            <template v-if="strategies.length > 0">
              <div v-for="item in strategies" :key="item.id" class="grid-item">
                <StrategyCard :strategy="item" @click="goToDetail(item.id)" />
              </div>
            </template>
            <el-empty v-else description="暂无相关攻略" />
          </div>
        </el-col>
        
        <!-- Right Sidebar -->
        <el-col :span="6">
          <div class="sidebar-wrapper">
            <el-card class="sidebar-card">
              <template #header>
                <div class="card-header">
                  <span>📢 平台公告</span>
                </div>
              </template>
              <ul class="announcement-list">
                <li><span class="dot"></span> 欢迎来到 Game Strategy Platform</li>
                <li><span class="dot"></span> 管理员审核通道已开启</li>
                <li><span class="dot"></span> 只有登录后才能发布内容</li>
                <li><span class="dot"></span> 严禁发布违规信息</li>
              </ul>
            </el-card>

            <el-card class="sidebar-card" style="margin-top: 20px">
              <template #header>
                <div class="card-header">
                  <span>🔥 热门标签</span>
                </div>
              </template>
              <div class="tags-cloud">
                <el-tag v-for="tag in ['黑神话', '原神', 'CS2', 'Valorant', 'LoL', 'Dota2']" 
                       :key="tag" 
                       effect="plain" 
                       class="hover-tag"
                       @click="handleTagClick(tag)">
                  {{ tag }}
                </el-tag>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import StrategyCard from '../components/StrategyCard.vue'
import { ElMessage } from 'element-plus'
import { Search, Menu } from '@element-plus/icons-vue'

const router = useRouter()
const strategies = ref([])
const loading = ref(true)
const searchKeyword = ref('')
const activeTab = ref('hot')

const fetchStrategies = async () => {
  loading.value = true
  try {
    let url = '/strategy/public/list'
    if (searchKeyword.value) {
      url = `/strategy/search?keyword=${searchKeyword.value}`
    }
    
    const res = await request.get(url)
    if (res.code === 200) {
      let data = res.data
      
      // 前端简单过滤 (实际项目应由后端过滤)
      if (activeTab.value !== 'hot' && activeTab.value !== 'new' && !searchKeyword.value) {
        data = data.filter(s => s.gameTag === activeTab.value)
      }
      
      strategies.value = data
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchStrategies()
}

const handleTabClick = () => {
  searchKeyword.value = ''
  fetchStrategies()
}

const handleTagClick = (tag) => {
  searchKeyword.value = tag
  fetchStrategies()
}

const goToDetail = (id) => {
  router.push(`/strategy/${id}`)
}

onMounted(() => {
  fetchStrategies()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.hero-section {
  position: relative;
  height: 300px;
  background: url('https://w.wallhaven.cc/full/l8/wallhaven-l83o92.jpg') center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--hero-overlay);
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  width: 100%;
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  letter-spacing: 4px;
  background: linear-gradient(120deg, var(--text-primary), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 10px;
  text-transform: uppercase;
  transition: all var(--transition-normal);
}

.hero-subtitle {
  color: var(--text-secondary);
  font-size: 1.2rem;
  margin-bottom: 40px;
  transition: color var(--transition-normal);
}

.hero-search {
  max-width: 600px;
  margin: 0 auto;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 10px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.5rem;
}

.strategy-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

@media (max-width: 992px) {
  .strategy-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .strategy-grid {
    grid-template-columns: 1fr;
  }
}

.sidebar-wrapper {
  position: sticky;
  top: 80px;
}

.announcement-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.announcement-list li {
  padding: 10px 0;
  border-bottom: 1px solid var(--border-light);
  color: var(--text-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  transition: color var(--transition-fast);
}

.announcement-list li:last-child {
  border-bottom: none;
}

.dot {
  width: 6px;
  height: 6px;
  background-color: var(--accent-color);
  border-radius: 50%;
  margin-right: 10px;
  display: inline-block;
  transition: background-color var(--transition-fast);
}

.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hover-tag {
  cursor: pointer;
  transition: all var(--transition-fast);
  background-color: var(--bg-tertiary) !important;
  border-color: var(--border-color) !important;
  color: var(--text-secondary) !important;
}

.hover-tag:hover {
  color: var(--accent-color) !important;
  border-color: var(--accent-color) !important;
  transform: translateY(-1px);
}
</style>
