<script setup>
const {proxy} = getCurrentInstance();
import {getOption, addBug} from "@/api/project/bug.js";
const showAdd = ref(true)
const form = ref({
  name:'',
  pjProjectId: null,
  pjTaskId: null,
  severity: null,
  priority: null,
  description: '',
})
const rules = {
  name: [
    {required: true, message: '请输入bug名称', trigger: 'change'}
  ],
  pjProjectId: [
    {required: true, message: '请选择项目', trigger: 'change'}
  ],
  severity: [
    {required: true, message: '请选择严重程度', trigger: 'change'}
  ],
  priority: [
    {required: true, message: '请选择紧急程度', trigger: 'change'}
  ],
  description: [
    {required: true, message: '请输入bug描述', trigger: 'change'}
  ],

}

const projectList = ref([])
const taskList = ref([])

function handleGetOption() {
  getOption().then(res => {
    projectList.value = res.data
  })
}

function submit() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      addBug(form.value).then(res => {
        if (res.code === 200) {
          proxy.$message.success('添加成功')
          cancel()
          showAdd.value = false
        }
      })
    }
  })
}

watch(() => form.value.pjProjectId, (newVal, oldVal) => {
  if(oldVal !== null){
    form.value.pjTaskId = null
  }
  const selectedProject = projectList.value.find(item => item.id === newVal)
  if (selectedProject) {
    taskList.value = selectedProject.pjTaskList
  } else {
    taskList.value = [] // 如果没有找到匹配的项目，则将任务列表清空
  }
})

function cancel() {
  form.value = {
    name:'',
    pjProjectId: null,
    pjTaskId: null,
    severity: null,
    priority: null,
    description: '',
  }
  proxy.resetForm("formRef");
}
function init() {
  handleGetOption()
}

init()
</script>

<template>
  <div v-if="showAdd">
    <div class="flex items-center justify-center bg-gray-100  h-screen ">
      <section class="p-6 bg-gray-50 rounded-md shadow-md" style="max-width: 500px">
        <h2 class="text-lg font-semibold text-gray-700 capitalize dark:text-white">Bug申报</h2>
        <div style="margin-top: 12px">
          <el-form ref="formRef" :model="form" :rules="rules">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="输入BUG名称"></el-input>
            </el-form-item>
            <el-form-item label="项目" prop="pjProjectId">
              <el-select v-model="form.pjProjectId">
                <el-option v-for="item in projectList" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="任务" prop="pjTaskId">
              <el-select v-model="form.pjTaskId" :clearable="true">
                <el-option v-for="item in taskList" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
              <span v-if="form.pjProjectId === null" style="margin-left: 5px;color: red">请先选择项目，任务可为空。</span>
            </el-form-item>
            <el-form-item label="严重程度" prop="severity">
              <el-radio-group v-model="form.severity" style="margin-top: -3.5px">
                <el-radio :label=0 la size="large">低</el-radio>
                <el-radio :label=1 size="large">中</el-radio>
                <el-radio :label=2 size="large">高</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="优先级" prop="priority">
              <el-radio-group v-model="form.priority" style="margin-top: -3.5px">
                <el-radio :label=0 la size="large">低</el-radio>
                <el-radio :label=1 size="large">中</el-radio>
                <el-radio :label=2 size="large">高</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="bug描述" prop="description">
              <div style="max-width: 100%;">
                <Editor v-model="form.description"/>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button style="width: 48%" @click="cancel"> 重置</el-button>
              <el-button type="primary" style="width: 48%" @click="submit">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </section>
    </div>
  </div>
  <div v-else class="flex items-center justify-center   h-screen">
    <section class="bg-white dark:bg-gray-900">
      <div class="container flex flex-col items-center px-4 py-12 mx-auto text-center">
        <h2 class="max-w-2xl mx-auto text-2xl font-semibold tracking-tight text-gray-800 xl:text-3xl dark:text-white">
          添加BUG <span class="text-blue-500">成功</span>
        </h2>

        <p class="max-w-4xl mt-6 text-center text-gray-500 dark:text-gray-300">
          Bug添加成功，您可以继续添加或者返回列表查看。
        </p>

        <div class="inline-flex w-full mt-6 sm:w-auto">
          <a href="#"
             @click="showAdd = true"
             class="inline-flex items-center justify-center w-full px-6 py-2 text-white duration-300 bg-blue-600 rounded-lg hover:bg-blue-500 focus:ring focus:ring-blue-300 focus:ring-opacity-80">
            继续添加
          </a>
        </div>

      </div>
    </section>
  </div>
</template>


<!-- component -->
<!-- Code on GiHub: https://github.com/vitalikda/form-floating-labels-tailwindcss -->
<style scoped>

</style>