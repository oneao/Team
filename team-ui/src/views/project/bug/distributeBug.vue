<script setup>
import {getBugDeclare, getBugDistribute, getBugDetail, updateBug,removeBug} from "@/api/project/bug.js";
import {ElNotification} from "element-plus";

const {proxy} = getCurrentInstance();

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name:'',
  severity: null,
  priority: null,
  status: null,
})

const declareList = ref([])
const total = ref(0)
//申报
function getDeclareList() {
  getBugDistribute(queryParams.value).then(res => {
    declareList.value = res.data.rows
    total.value = res.data.total
  })
}

function showNotification() {
  ElNotification({
    type: 'warning',
    title: '提示',
    message: h('i', {style: 'color: teal'}, '该BUG已被派发，无法修改。'),
  })
}

import {getOption} from "@/api/project/bug.js";
import {ref} from "vue";
import {useRouter} from "vue-router";

const showUpdate = ref(false)
const updateForm = ref({})
const updateFormRules = {
  name: [
    {required: true, message: '请输入名称', trigger: 'blur'},
  ],
  pjProjectId: [
    {required: true, message: '请选择项目', trigger: 'change'}
  ],
  severity: [
    {required: true, message: '请选择严重程度', trigger: 'change'}
  ],
  priority: [
    {required: true, message: '请选择优先级', trigger: 'change'}
  ],
  description: [
    {required: true, message: '请输入描述', trigger: 'blur'}
  ]
}
const severityOptions = [
  {label: '低', value: 0},
  {label: '中', value: 1},
  {label: '高', value: 2},
  {label: '全部', value: -1},
]
const priorityOptions = [
  {label: '低', value: 0},
  {label: '中', value: 1},
  {label: '高', value: 2},
  {label: '全部', value: -1},

]
const statusOptions = [
  {label: '未解决', value: 0},
  {label: '已解决', value: 1},
  {label: '全部', value: -1},
]
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

//编辑
const projectList = ref([])
const taskList = ref([])

function handleGetOption() {
  getOption().then(res => {
    projectList.value = res.data
    const selectedProject = projectList.value.find(item => item.id === updateForm.value.pjProjectId)
    if (selectedProject) {
      taskList.value = selectedProject.pjTaskList
    } else {
      taskList.value = [] // 如果没有找到匹配的项目，则将任务列表清空
    }
  })
}
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name:'',
    severity: null,
    priority: null,
    status: null,
  }
  getDeclareList()
}

watch(() => updateForm.value.pjProjectId, (newVal, oldVal) => {
  if (oldVal !== null) {
    updateForm.value.pjTaskId = null
  }
  const selectedProject = projectList.value.find(item => item.id === newVal)
  if (selectedProject) {
    taskList.value = selectedProject.pjTaskList
  } else {
    taskList.value = [] // 如果没有找到匹配的项目，则将任务列表清空
  }
})

function handleUpdate(row) {
    cancelUpdate()
    const _id = row.id || ids.value
    getBugDetail(_id).then(res => {
      updateForm.value = res.data
      handleGetOption()
      showUpdate.value = true
    })
}

function confirmUpdate() {
  proxy.$refs["updateFormRef"].validate(valid => {
    if (valid) {
      updateBug(updateForm.value).then(res => {
        if (res.code === 200) {
          proxy.$message.success('更新成功')
          cancelUpdate()
          getDeclareList()
        }
      })
    }
  })
}

function cancelUpdate() {
  projectList.value = []
  taskList.value = []
  showUpdate.value = false
  updateForm.value = {}
  proxy.resetForm("updateFormRef");
}

//删除


function init() {
  getDeclareList()
}

init()
const router = useRouter();

function toAddBug(){
  router.push('/bug/reportBug')
}
</script>
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" >
      <el-form-item label="名称">
        <el-input v-model="queryParams.name" placeholder="请输入bug名称"></el-input>
      </el-form-item>
      <el-form-item label="严重程度">
        <el-select v-model="queryParams.severity">
          <el-option v-for="item in severityOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="优先级">
        <el-select v-model="queryParams.priority">
          <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getDeclareList" icon="Search">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery" >重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="toAddBug"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
        >修改
        </el-button>
      </el-col>
    </el-row>
    <el-table :data="declareList">
      <el-table-column type="selection" width="55" align="center"
                       @selection-change="handleSelectionChange"></el-table-column>
      <el-table-column label="名称" prop="name" width="100"></el-table-column>
      <el-table-column label="严重程度" align="center" prop="severity" width="100">
        <template v-slot="{row}">
          <el-tag v-if="row.severity === 0" type="success">低</el-tag>
          <el-tag v-else-if="row.severity === 1" type="warning">中</el-tag>
          <el-tag v-else-if="row.severity === 2" type="danger">高</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="severity" width="100">
        <template v-slot="{row}">
          <el-tag v-if="row.priority === 0" type="success">低</el-tag>
          <el-tag v-else-if="row.priority === 1" type="warning">中</el-tag>
          <el-tag v-else-if="row.priority === 2" type="danger">高</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Bug描述" prop="description" align="center">
        <template v-slot="{row}">
          <span v-html="row.description"></span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template v-slot="{row}">
          <el-tag v-if="row.status === 0" type="danger">未解决</el-tag>
          <el-tag v-else type="success">已解决</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联项目" align="center" prop="pjProjectName" width="100"></el-table-column>
      <el-table-column label="关联任务" align="center" prop="pjProjectName" width="100">
        <template v-slot="{row}">
          <span v-if="row.pjTaskName !== '' && row.pjTaskName !== null">{{ row.pjTaskName }}</span>
          <span v-else>未关联任务</span>
        </template>
      </el-table-column>
      <el-table-column label="派发人" prop="updateBy" width="100">

      </el-table-column>
      <el-table-column label="解决时间" width="170" prop="solveTime" align="center">
        <template v-slot="{row}">
          <span v-if="row.status === 1">{{ parseTime(row.solveTime) }}</span>
          <span v-else>未解决</span>
        </template>
      </el-table-column>
      <el-table-column label="建议" prop="suggestion" align="center">

      </el-table-column>
      <el-table-column label="操作" align="center">
        <template v-slot="{row}">
          <el-button type="primary" @click="handleUpdate(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getDeclareList"
        layout="total,prev, pager, next, jumper"
    />
    <el-dialog title="更新BUG" v-model="showUpdate" width="550">
      <el-form ref="updateFormRef" :model="updateForm" :rules="updateFormRules">
        <el-form-item label="名称" prop="name">
          <el-input v-model="updateForm.name" :disabled="true" placeholder="输入BUG名称"></el-input>
        </el-form-item>
        <el-form-item label="项目" prop="pjProjectId">
          <el-select v-model="updateForm.pjProjectId" :disabled="true">
            <el-option v-for="item in projectList"  :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务" prop="pjTaskId">
          <el-select v-model="updateForm.pjTaskId" :disabled="true" :clearable="true">
            <el-option v-for="item in taskList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <span v-if="updateForm.pjProjectId === null"
                style="margin-left: 5px;color: red">请先选择项目，任务可为空。</span>
        </el-form-item>
        <el-form-item label="严重程度" prop="severity">
          <el-radio-group v-model="updateForm.severity" :disabled="true" style="margin-top: -3.5px">
            <el-radio :label=0 la size="large">低</el-radio>
            <el-radio :label=1 size="large">中</el-radio>
            <el-radio :label=2 size="large">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="updateForm.priority" :disabled="true" style="margin-top: -3.5px">
            <el-radio :label=0 la size="large">低</el-radio>
            <el-radio :label=1 size="large">中</el-radio>
            <el-radio :label=2 size="large">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="bug描述" prop="description">
          <div style="max-width: 100%;">
            <Editor v-model="updateForm.description"  :read-only="true"/>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="updateForm.status" >
            <el-option label="未解决" :value="0"></el-option>
            <el-option label="已解决" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 48%" @click="cancelUpdate"> 取消</el-button>
          <el-button type="primary" @click="confirmUpdate" style="width: 48%">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<style scoped>
.custom-tabs:deep(el-tabs__content) {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.custom-tabs:deep( .el-tabs__item) {

  font-size: 24px;
  width: 700px;
}
</style>
