<template>
  <div class="detail-container" v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="18">
        <template v-if="strategy">
          <el-card class="strategy-card">
            <div v-if="strategy.coverImage" class="cover-image-container">
              <img :src="strategy.coverImage" alt="封面图" class="cover-image" />
            </div>
            
            <div class="header">
              <h1>{{ strategy.title }}</h1>
              <div class="meta">
                <el-tag>{{ strategy.gameTag }}</el-tag>
                <span class="date">发布于 {{ formatDate(strategy.createTime) }}</span>
                <span class="views"><el-icon><View /></el-icon> {{ strategy.viewCount }} 阅读</span>
              </div>
            </div>

            <el-divider />

            <div class="content">
              <MdPreview :modelValue="strategy.content" />
            </div>

            <div class="actions">
              <el-button 
                :type="isLiked ? 'primary' : 'default'" 
                circle 
                size="large"
                @click="toggleLike"
              >
                <el-icon><ThumbsUp /></el-icon>
              </el-button>
              <span class="count">{{ strategy.likeCount }}</span>

              <el-button 
                :type="isFavorited ? 'warning' : 'default'" 
                circle 
                size="large"
                @click="toggleFavorite"
                style="margin-left: 20px"
              >
                <el-icon><Star /></el-icon>
              </el-button>
              <span class="count">收藏</span>
            </div>
          </el-card>

          <!-- 评论区 -->
          <el-card class="comments-card">
            <h3>评论与问答</h3>
            <div class="comment-input" v-if="userStore.isLoggedIn">
              <el-input
                v-model="newComment"
                type="textarea"
                placeholder="写下你的看法或提问..."
                :rows="3"
              />
              <el-button type="primary" style="margin-top: 10px" @click="submitComment(null)">发表评论</el-button>
            </div>
            <div v-else class="login-tip">
              <el-button type="text" @click="$router.push('/login')">登录</el-button> 后参与讨论
            </div>

            <div class="comment-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-main">
                  <div class="comment-avatar">
                    <el-avatar :size="40" :src="comment.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                  </div>
                  <div class="comment-body">
                    <div class="comment-user">{{ comment.username || '用户' + comment.userId }}</div>
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-meta">
                      <span>{{ formatDate(comment.createTime) }}</span>
                      <el-button type="text" size="small" @click="showReplyInput(comment.id)">回复</el-button>
                    </div>
                    
                    <!-- 回复输入框 -->
                    <div v-if="activeReplyId === comment.id" class="reply-input">
                      <el-input v-model="replyContent" placeholder="回复..." size="small">
                        <template #append>
                          <el-button @click="submitComment(comment.id)">发送</el-button>
                        </template>
                      </el-input>
                    </div>
                  </div>
                </div>

                <!-- 子评论 -->
                <div v-if="comment.children && comment.children.length > 0" class="sub-comments">
                  <div v-for="child in comment.children" :key="child.id" class="sub-comment-item">
                     <div class="comment-user">{{ child.username || '用户' + child.userId }}</div>
                     <div class="comment-content">回复 @{{ comment.username }}: {{ child.content }}</div>
                     <div class="comment-time">{{ formatDate(child.createTime) }}</div>
                  </div>
                </div>
              </div>
              <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发" />
            </div>
          </el-card>
        </template>
        <el-empty v-else-if="!loading" description="攻略不存在或已被删除" />
      </el-col>
      
      <el-col :span="6">
        <el-card class="author-card" v-if="strategy">
          <template #header>
            <div class="card-header">
              <span>关于作者</span>
            </div>
          </template>
          <div class="author-info">
            <el-avatar :size="64" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <h3>User {{ strategy.authorId }}</h3>
            <p>游戏爱好者，攻略达人</p>
            <el-button type="primary" plain size="small">关注作者</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { View, Pointer, Star } from '@element-plus/icons-vue'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const strategy = ref(null)
const comments = ref([])
const loading = ref(true)
const newComment = ref('')
const replyContent = ref('')
const activeReplyId = ref(null)
const isLiked = ref(false)
const isFavorited = ref(false)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const fetchDetail = async () => {
  try {
    const res = await request.get(`/strategy/${route.params.id}`)
    if (res.code === 200) {
      strategy.value = res.data
      fetchStatus()
      fetchComments()
    }
  } catch (error) {
    ElMessage.error('获取攻略详情失败')
  } finally {
    loading.value = false
  }
}

const fetchStatus = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await request.get(`/strategy/${route.params.id}/status`)
    if (res.code === 200) {
      isLiked.value = res.data.liked
      isFavorited.value = res.data.favorited
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchComments = async () => {
  try {
    const res = await request.get(`/comment/list/${route.params.id}`)
    if (res.code === 200) {
      comments.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const toggleLike = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    const res = await request.post(`/strategy/like/${strategy.value.id}`)
    if (res.code === 200) {
      isLiked.value = !isLiked.value
      strategy.value.likeCount += isLiked.value ? 1 : -1
      ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    const res = await request.post(`/strategy/favorite/${strategy.value.id}`)
    if (res.code === 200) {
      isFavorited.value = !isFavorited.value
      ElMessage.success(isFavorited.value ? '收藏成功' : '取消收藏')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const showReplyInput = (commentId) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  if (activeReplyId.value === commentId) {
    activeReplyId.value = null
  } else {
    activeReplyId.value = commentId
    replyContent.value = ''
  }
}

const submitComment = async (parentId) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  
  const content = parentId ? replyContent.value : newComment.value
  if (!content.trim()) return ElMessage.warning('请输入评论内容')

  try {
    const res = await request.post('/comment/add', {
      strategyId: strategy.value.id,
      content: content,
      parentId: parentId
    })
    if (res.code === 200) {
      ElMessage.success('评论成功')
      if (parentId) {
        activeReplyId.value = null
        replyContent.value = ''
      } else {
        newComment.value = ''
      }
      fetchComments()
    }
  } catch (e) {
    ElMessage.error('评论失败')
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.detail-container {
  padding: 20px;
}
.strategy-card {
  min-height: 400px;
  border-radius: 12px;
  overflow: hidden;
  /* Removed dark theme overrides */
}
.cover-image-container {
  margin: -20px -20px 20px -20px;
  max-height: 500px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}
.cover-image {
  width: 100%;
  height: auto;
  object-fit: contain;
}
.header {
  padding: 0 10px;
}
.header h1 {
  margin-bottom: 15px;
  font-size: 2em;
  /* Removed white text color */
}
.meta {
  color: #999;
  font-size: 0.9em;
  display: flex;
  gap: 20px;
  align-items: center;
}
.content {
  margin: 30px 0;
  line-height: 1.8;
  font-size: 16px;
  /* Removed text color override */
}
.actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
  gap: 10px;
}
.count {
  font-size: 1.2em;
  font-weight: bold;
  color: #666;
}
.comments-card {
  margin-top: 20px;
}
.comment-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 15px 0;
}
.comment-main {
  display: flex;
  gap: 15px;
}
.comment-body {
  flex: 1;
}
.comment-user {
  font-weight: bold;
  margin-bottom: 5px;
}
.comment-meta {
  font-size: 0.85em;
  color: #999;
  margin-top: 5px;
}
.sub-comments {
  margin-left: 55px;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}
.sub-comment-item {
  margin-bottom: 10px;
  font-size: 0.9em;
}
.author-info {
  text-align: center;
}
.author-info h3 {
  margin: 10px 0 5px;
}
.author-info p {
  color: #999;
  margin-bottom: 15px;
}
</style>
