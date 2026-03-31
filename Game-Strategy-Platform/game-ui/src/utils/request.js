import axios from 'axios'
import { useUserStore } from '../stores/user'

const request = axios.create({
  baseURL: '/api', // Vite proxy will handle this
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    // 假设后端返回格式 { code: 200, msg: "success", data: ... }
    if (res.code !== 200) {
      // 可以结合 Element Plus ElMessage 提示错误
      console.error(res.message || res.msg)
      return Promise.reject(new Error(res.message || res.msg || 'Error'))
    }
    return res
  },
  error => {
    return Promise.reject(error)
  }
)

export default request
