<template>
  <div class="create-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发布新攻略</span>
        </div>
      </template>
      
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入攻略标题" />
        </el-form-item>
        
        <el-form-item label="游戏类型" prop="gameTag">
          <el-select v-model="form.gameTag" placeholder="选择游戏类型">
            <el-option label="MOBA" value="MOBA" />
            <el-option label="RPG" value="RPG" />
            <el-option label="FPS" value="FPS" />
            <el-option label="独立游戏" value="INDIE" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <div class="avatar-wrapper">
             <el-upload
              v-if="!form.coverImage"
              class="avatar-uploader"
              action=""
              :http-request="customUpload"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
            >
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div v-else class="image-preview">
              <img :src="form.coverImage" class="avatar" />
              <div class="image-actions">
                <el-icon class="delete-icon" @click="handleRemoveImage"><Delete /></el-icon>
              </div>
            </div>
          </div>
          <div class="el-upload__tip">只能上传jpg/png文件，且不超过20MB</div>
        </el-form-item>
        
        <el-form-item label="攻略内容" prop="content">
          <MdEditor 
            v-model="form.content" 
            :onUploadImg="onUploadImg" 
            placeholder="支持 Markdown 格式，支持粘贴图片"
            style="height: 500px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="submitting">立即发布</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  title: '',
  gameTag: '',
  content: '',
  coverImage: ''
})

const headers = computed(() => {
  return {
    Authorization: `Bearer ${userStore.token}`
  }
})

const customUpload = async (options) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)
  
  // Use a fresh axios instance to avoid interceptor issues with Content-Type
  try {
    const res = await axios.post('/api/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${userStore.token}`
      }
    })
    
    // Axios response structure: res.data is the actual payload
    const responseData = res.data
    
    if (responseData.code === 200) {
      form.coverImage = responseData.data
      onSuccess(responseData.data)
      ElMessage.success('上传成功')
    } else {
      onError(new Error(responseData.message))
      ElMessage.error(responseData.message || '上传失败')
    }
  } catch (error) {
    onError(error)
    ElMessage.error('上传失败: ' + (error.message || '未知错误'))
  }
}

const handleRemoveImage = () => {
  form.coverImage = ''
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('Avatar picture must be JPG/PNG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 20) {
    ElMessage.error('Avatar picture size can not exceed 20MB!')
    return false
  }
  return true
}

const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
    files.map(async (file) => {
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await axios.post('/api/upload', formData, {
           headers: {
             'Content-Type': 'multipart/form-data',
             'Authorization': `Bearer ${userStore.token}`
           }
        })
        const data = response.data
        if (data.code === 200) {
          return data.data
        }
        throw new Error(data.message)
      } catch (e) {
        ElMessage.error('上传图片失败')
        throw e
      }
    })
  )

  callback(res)
};

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  gameTag: [{ required: true, message: '请选择游戏类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const onSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await request.post('/strategy/publish', form)
        if (res.code === 200) {
          ElMessage.success('发布成功，已提交审核')
          router.push('/profile') // Go to user center to see status
        }
      } catch (error) {
        console.error(error)
        ElMessage.error(error.message || '发布失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    } else {
        ElMessage.warning('请检查表单是否填写完整')
        return false
    }
  })
}
</script>

<style scoped>
.create-container {
  max-width: 800px;
  margin: 0 auto;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
}

.image-preview {
  position: relative;
  width: 178px;
  height: 178px;
  border-radius: 6px;
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-actions {
  opacity: 1;
}

.delete-icon {
  color: #fff;
  font-size: 24px;
  cursor: pointer;
}
</style>
