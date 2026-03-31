import { ref, watchEffect } from 'vue'

const THEME_KEY = 'app-theme'
const themes = {
  light: 'light',
  dark: 'dark'
}

const theme = ref(localStorage.getItem(THEME_KEY) || themes.dark)

export function useTheme() {
  
  const toggleTheme = () => {
    theme.value = theme.value === themes.dark ? themes.light : themes.dark
    localStorage.setItem(THEME_KEY, theme.value)
    applyTheme(theme.value)
  }

  const setTheme = (newTheme) => {
    theme.value = newTheme
    localStorage.setItem(THEME_KEY, theme.value)
    applyTheme(theme.value)
  }

  const applyTheme = (themeName) => {
    const root = document.documentElement
    root.setAttribute('data-theme', themeName)
    
    // 应用主题变量
    const themeVars = getThemeVariables(themeName)
    Object.entries(themeVars).forEach(([key, value]) => {
      root.style.setProperty(key, value)
    })
  }

  const getThemeVariables = (themeName) => {
    if (themeName === themes.light) {
      return {
        '--bg-color': '#ffffff',
        '--bg-secondary': '#f8f9fa',
        '--bg-tertiary': '#e9ecef',
        '--text-primary': '#212529',
        '--text-secondary': '#6c757d',
        '--text-muted': '#adb5bd',
        '--accent-color': '#0066cc',
        '--accent-hover': '#0052a3',
        '--border-color': '#dee2e6',
        '--border-light': '#e9ecef',
        '--card-bg': '#ffffff',
        '--card-border': '#dee2e6',
        '--card-shadow': '0 2px 8px rgba(0,0,0,0.1)',
        '--hover-color': '#f8f9fa',
        '--success-color': '#28a745',
        '--warning-color': '#ffc107',
        '--danger-color': '#dc3545',
        '--info-color': '#17a2b8',
        '--header-bg': 'rgba(255, 255, 255, 0.95)',
        '--hero-overlay': 'linear-gradient(to bottom, rgba(255, 255, 255, 0.1), rgba(248, 249, 250, 0.95))'
      }
    } else {
      return {
        '--bg-color': '#0d1117',
        '--bg-secondary': '#161b22',
        '--bg-tertiary': '#21262d',
        '--text-primary': '#f0f6fc',
        '--text-secondary': '#8b949e',
        '--text-muted': '#484f58',
        '--accent-color': '#58a6ff',
        '--accent-hover': '#79c0ff',
        '--border-color': '#30363d',
        '--border-light': '#21262d',
        '--card-bg': '#161b22',
        '--card-border': '#30363d',
        '--card-shadow': '0 4px 16px rgba(0,0,0,0.4)',
        '--hover-color': '#1f242c',
        '--success-color': '#3fb950',
        '--warning-color': '#d29922',
        '--danger-color': '#f85149',
        '--info-color': '#58a6ff',
        '--header-bg': 'rgba(22, 27, 34, 0.95)',
        '--hero-overlay': 'linear-gradient(to bottom, rgba(13, 17, 23, 0.3), rgba(13, 17, 23, 0.95))'
      }
    }
  }

  const isDark = () => theme.value === themes.dark
  const isLight = () => theme.value === themes.light

  // 初始化主题
  watchEffect(() => {
    applyTheme(theme.value)
  })

  return {
    theme,
    toggleTheme,
    setTheme,
    isDark,
    isLight,
    themes
  }
}
