<template>
  <el-card class="strategy-card" :body-style="{ padding: '0px' }" shadow="hover">
    <div class="card-image-wrapper">
      <img v-if="strategy.coverImage" :src="strategy.coverImage" class="image" />
      <div v-else class="image-placeholder">
        <el-icon :size="40"><Picture /></el-icon>
      </div>
      <div class="card-overlay">
        <el-tag size="small" effect="dark" class="game-tag">{{ strategy.gameTag }}</el-tag>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="title" :title="strategy.title">{{ strategy.title }}</h3>
      
      <div class="meta-row">
        <span class="author">
           <el-icon><User /></el-icon> {{ strategy.authorName || '玩家' }}
        </span>
        <span class="time">{{ formatDate(strategy.createTime) }}</span>
      </div>
      
      <div class="stats-row">
        <div class="stat-item">
          <el-icon><View /></el-icon> {{ strategy.viewCount || 0 }}
        </div>
        <div class="stat-item">
          <el-icon><Star /></el-icon> {{ strategy.likeCount || 0 }}
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Picture, User, View, Star } from '@element-plus/icons-vue'

defineProps({
  strategy: {
    type: Object,
    required: true
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style scoped>
.strategy-card {
  width: 100%;
  border: none !important;
  background-color: var(--card-bg) !important;
  border-radius: 12px !important;
  overflow: hidden;
  transition: all var(--transition-normal);
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-sm);
}

.strategy-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--accent-color) !important;
}

.card-image-wrapper {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: var(--bg-tertiary);
  display: flex;
  justify-content: center;
  align-items: center;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: contain; /* Changed from cover to contain */
  transition: transform 0.5s ease;
}

.strategy-card:hover .image {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, var(--bg-tertiary), var(--hover-color));
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--text-muted);
}

.card-overlay {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
}

.game-tag {
  background-color: rgba(0, 0, 0, 0.6) !important;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
}

.card-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 16px;
  margin: 0 0 12px 0;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--text-primary);
  transition: color var(--transition-normal);
}

.meta-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.author {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stats-row {
  margin-top: auto;
  display: flex;
  gap: 16px;
  border-top: 1px solid var(--border-color);
  padding-top: 12px;
  color: var(--text-secondary);
  font-size: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
