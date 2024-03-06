<script setup>
import {getFileList, renameFile, deleteFile, deleteFileByIds} from '@/api/project/taskFile.js'
import {ref} from "vue";
import {listProjectAll, removeProject} from "@/api/project/project.js";
import {ElNotification} from "element-plus";

const {proxy} = getCurrentInstance()

const queryParams = ref({
  start: 1,
  end: 10,
  pjProjectId: null,
  pjTaskId: null,
  fileSuffix: null,
  fileName: ''
})
const tableData = ref([])
const total = ref(0)
const projectOptions = ref([])
const taskOptions = ref([])
const fileSuffixOptions = [
  {label: 'doc', value: 'doc'},
  {label: 'xls', value: 'xls'},
  {label: 'ppt', value: 'ppt'},
  {label: 'pdf', value: 'pdf'},
  {label: 'txt', value: 'txt'}
]

function handleProjectOptions() {
  listProjectAll().then(res => {
    projectOptions.value = res.data
  })
}

function handleQuery() {
  getFileList(queryParams.value).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
    console.log(tableData.value)
  })
}

function resetQuery() {
  queryParams.value = {
    start: 1,
    end: 10,
    pjProjectId: null,
    pjTaskId: null,
    fileSuffix: null,
    fileName: ''
  }
  handleQuery()
}

function init() {
  handleProjectOptions()
  handleQuery()
}

import {listByProjectId} from "@/api/project/task.js";

function projectSelectChange(val) {
  if (val) {
    queryParams.value.pjTaskId = null
    taskOptions.value = []
  }
}

function taskSelectChange(val) {
  if (val) {
    if (queryParams.value.pjProjectId === null) {
      ElNotification({
        title: 'Tip',
        type: 'warning',
        message: '请先选择项目',
        duration: 2000,
      })
    } else {
      listByProjectId(queryParams.value.pjProjectId).then(res => {
        taskOptions.value = res.data
      })
    }
  }
}

function getFilePath(name) {
  return import.meta.env.VITE_APP_BASE_API + name
}

function downloadFile(item) {
  window.open(getFilePath(item.filePath))
}

function handleSingleRenameFile() {
  console.log(ids.value)
  if (ids.value.length !== 1) {
    ElNotification({
      title: 'Tip',
      type: 'warning',
      message: '请选择一个文件进行修改',
      duration: 2000,
    })
  } else {
    var id = ids.value[0]
    tableData.value.forEach(item => {
      if (item.id === id) {
        renameFileForm.value.fileId = item.id
        renameFileForm.value.fileName = handleGetFileName(item)
        renameFileForm.value.projectId = item.pjProjectId
      }
    })
    showRenameDialog.value = true
  }
}

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleGetFileName(item) {
  // 获取文件名
  let fileName = item.fileName;

  // 使用 lastIndexOf() 方法找到最后一个点的索引
  let lastDotIndex = fileName.lastIndexOf('.');

  // 如果找到了点
  if (lastDotIndex !== -1) {
    // 使用 substring() 方法提取最后一个点之前的内容
    let extractedContent = fileName.substring(0, lastDotIndex);
    // 设置 renameFileForm.value.fileName 的值为提取的内容
    return extractedContent;
  } else {
    // 如果没有找到点，则直接使用原始文件名
    return fileName;
  }
}

const handleDeleteFile = (row) => {
  const _id = row.id || ids.value
  proxy.$confirm('确定删除该文件么？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteFileByIds(_id).then(res => {
      if (res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
}
const showRenameDialog = ref(false)
const renameFileForm = ref({
  fileId: null,
  fileName: '',
  projectId: null,
})
const handleRenameFile = (row) => {
  renameFileForm.value.fileId = row.id
  renameFileForm.value.fileName = handleGetFileName(row)
  renameFileForm.value.projectId = row.pjProjectId
  console.log(renameFileForm.value)
  showRenameDialog.value = true
}

function confirmRenameFile() {
  console.log(renameFileForm.value)
  renameFile(renameFileForm.value).then(res => {
    if (res.code === 200) {
      proxy.$message.success('修改成功')
      showRenameDialog.value = false
      handleQuery()
    }
  })
}

function cancelRenameFile() {
  showRenameDialog.value = false
  renameFileForm.value = {
    fileId: null,
    fileName: '',
    projectId: null
  }
}

init()
</script>

<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" class="demo-form-inline">
      <el-form-item label="项目">
        <el-select v-model="queryParams.pjProjectId" @visible-change="projectSelectChange" value-key="id" filterable
                   style="width: 150px">
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item label="任务">
        <el-select v-model="queryParams.pjTaskId" @visible-change="taskSelectChange" placeholder="请选择任务"
                   style="width: 150px">
          <el-option
              v-for="item in taskOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="文件类型">
        <el-select v-model="queryParams.fileSuffix" placeholder="请选择文件类型" style="width: 150px">
          <el-option
              v-for="item in fileSuffixOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="文件名">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名" style="width: 150px"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleSingleRenameFile"
        >修改名称
        </el-button>

        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDeleteFile"
        >删除
        </el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData"
              @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column label="id" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="名称" prop="fileName" width="200" align="center">
        <template #default="scope">
          <span>{{ handleGetFileName(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件类型" prop="fileSuffix" width="150" align="center"></el-table-column>
      <el-table-column label="文件大小" prop="fileSizeStr" width="150" align="center"></el-table-column>
      <el-table-column label="关联任务" prop="taskName" width="200" align="center"></el-table-column>
      <el-table-column label="上传人" prop="createBy" width="200" align="center"></el-table-column>
      <el-table-column label="上传时间" prop="createTime" width="300" align="center">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" @click="downloadFile(scope.row)">下载</el-button>
          <el-button type="success" @click="handleRenameFile(scope.row)">更新名称</el-button>
          <el-button type="danger" @click="handleDeleteFile(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <pagination
          v-if="total > 0"
          :total="total"
          v-model:page="queryParams.start"
          v-model:limit="queryParams.end"
          @pagination="handleQuery"
          layout="total,prev, pager, next, jumper"
      />
    </el-table>
    <el-dialog v-model="showRenameDialog" title="修改文件名称" width="400">
      <el-form :model="renameFileForm" label-width="80px">
        <el-form-item label="文件名称" prop="fileName">
          <el-input v-model="renameFileForm.fileName" placeholder="请输入文件名称"></el-input>
          <span style="color: red">注：只会修改展示文件名，不会修改实际文件名。</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer" style="margin-top: -50px">
          <el-button @click="cancelRenameFile">取 消</el-button>
          <el-button type="primary" @click="confirmRenameFile">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
</style>