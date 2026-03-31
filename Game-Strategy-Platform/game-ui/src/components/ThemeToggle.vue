<template>
  <div class="theme-toggle">
    <el-tooltip :content="isDark ? '切换到白天模式' : '切换到黑夜模式'" placement="bottom">
      <button 
        class="toggle-btn"
        @click="toggleTheme"
        :class="{ 'dark-mode': isDark }"
      >
        <transition name="icon-fade" mode="out-in">
          <el-icon v-if="isDark" key="sun" class="sun-icon">
            <Sunny />
          </el-icon>
          <el-icon v-else key="moon" class="moon-icon">
            <Moon />
          </el-icon>
        </transition>
      </button>
    </el-tooltip>
  </div>
</template>

<script setup>
import { useTheme } from '../composables/useTheme'
import { Sunny, Moon } from '@element-plus/icons-vue'

const { theme, toggleTheme, isDark } = useTheme()
</script>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
}

.toggle-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--border-color);
  background: var(--bg-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.toggle-btn:hover {
  transform: scale(1.1);
  border-color: var(--accent-color);
  box-shadow: 0 0 12px rgba(88, 166, 255, 0.3);
}

.toggle-btn.dark-mode {
  background: linear-gradient(135deg, #1e3a8a, #312e81);
  border-color: #58a6ff;
}

.sun-icon {
  color: #fbbf24;
  font-size: 18px;
  animation: rotate 20s linear infinite;
}

.moon-icon {
  color: #6366f1;
  font-size: 18px;
}

.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: all 0.3s ease;
}

.icon-fade-enter-from {
  opacity: 0;
  transform: rotate(-180deg) scale(0.5);
}

.icon-fade-leave-to {
  opacity: 0;
  transform: rotate(180deg) scale(0.5);
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 添加光晕效果 */
.toggle-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(88, 166, 255, 0.3), transparent);
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.toggle-btn:hover::before {
  opacity: 1;
}
</style>
