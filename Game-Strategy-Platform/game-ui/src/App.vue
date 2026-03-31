<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <h2>GAME STRATEGY</h2>
          </div>
          
          <div class="nav-links">
             <router-link to="/">首页</router-link>
          </div>

          <div class="header-right">
            <!-- 主题切换按钮 -->
            <ThemeToggle />
            
            <!-- 搜索框移到右侧 -->
            <div class="search-area">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索..."
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>

            <div class="user-area">
              <template v-if="userStore.isLoggedIn">
                <!-- 管理员快捷入口 -->
                <el-button 
                  v-if="userStore.user && userStore.user.role === 'ROLE_ADMIN'" 
                  type="danger" 
                  size="small"
                  class="admin-btn"
                  @click="$router.push('/admin')"
                >
                  <el-icon><Warning /></el-icon> 审核
                </el-button>

                <el-button type="primary" size="small" @click="$router.push('/create')">
                  <el-icon><Edit /></el-icon> 投稿
                </el-button>

                <el-dropdown trigger="click">
                  <div class="user-avatar-wrapper">
                    <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                    <span class="username">{{ userStore.user.username }}</span>
                    <el-icon><ArrowDown /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu class="user-dropdown">
                      <el-dropdown-item @click="$router.push('/profile')">
                        <el-icon><User /></el-icon> 个人中心
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="handleLogout">
                        <el-icon><SwitchButton /></el-icon> 退出登录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
              <template v-else>
                <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
                <el-button type="primary" @click="$router.push('/register')">注册</el-button>
              </template>
            </div>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer>
        <div class="footer-content">
          <p>© 2024 Game Strategy Platform. Designed for Gamers.</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from './stores/user'
import { useRouter } from 'vue-router'
import { Search, ArrowDown, Edit, Warning, User, SwitchButton } from '@element-plus/icons-vue'
import ThemeToggle from './components/ThemeToggle.vue'

const userStore = useUserStore()
const router = useRouter()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value }
    })
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  background-color: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  height: 64px;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(12px);
  transition: all 0.3s ease;
}

.header-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo h2 {
  font-family: 'Segoe UI', sans-serif;
  font-weight: 800;
  font-size: 20px;
  letter-spacing: 1px;
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.nav-links {
  display: flex;
  gap: 24px;
  margin-right: auto;
}

.nav-links a {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 14px;
  transition: color 0.2s;
}

.nav-links a:hover, .nav-links a.router-link-active {
  color: var(--accent-color);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-area {
  width: 200px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-btn {
  background-color: #da3633 !important;
  border-color: #da3633 !important;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.user-avatar-wrapper:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
}

.el-main {
  padding: 0;
  min-height: calc(100vh - 64px - 60px);
}

.el-footer {
  background-color: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding: 20px 0;
  text-align: center;
  color: var(--text-secondary);
  font-size: 12px;
  transition: all 0.3s ease;
}
</style>
