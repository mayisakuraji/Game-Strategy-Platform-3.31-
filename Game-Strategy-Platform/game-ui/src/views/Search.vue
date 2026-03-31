<template>
  <div class="search-container">
    <h2>搜索结果: "{{ keyword }}"</h2>
    <div v-loading="loading">
      <div v-if="strategies.length > 0" class="strategy-list">
        <el-card
          v-for="item in strategies"
          :key="item.id"
          class="strategy-card"
          shadow="hover"
          @click="$router.push(`/strategy/${item.id}`)"
        >
          <template #header>
            <div class="card-header">
              <span class="title">{{ item.title }}</span>
              <el-tag size="small">{{ item.gameTag }}</el-tag>
            </div>
          </template>
          <div class="card-content">
            <p class="summary">{{ item.content.substring(0, 100) }}...</p>
            <div class="card-footer">
              <span>By User {{ item.authorId }}</span>
              <span>{{ formatDate(item.createTime) }}</span>
              <span>👁️ {{ item.viewCount }}</span>
              <span>👍 {{ item.likeCount }}</span>
            </div>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="未找到相关攻略" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '../utils/request'

const route = useRoute()
const keyword = ref('')
const strategies = ref([])
const loading = ref(false)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const fetchSearchResults = async () => {
  keyword.value = route.query.keyword || ''
  if (!keyword.value) return
  
  loading.value = true
  try {
    const res = await request.get('/strategy/search', {
      params: { keyword: keyword.value }
    })
    if (res.code === 200) {
      strategies.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

watch(() => route.query.keyword, () => {
  fetchSearchResults()
})

onMounted(() => {
  fetchSearchResults()
})
</script>

<style scoped>
.search-container {
  padding: 20px;
}
.strategy-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.strategy-card:hover {
  transform: translateY(-2px);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-weight: bold;
  font-size: 1.1em;
}
.card-footer {
  margin-top: 10px;
  color: #999;
  font-size: 0.9em;
  display: flex;
  gap: 15px;
}
</style>
