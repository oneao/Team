<script setup>
import {getRecycleFile} from "@/api/project/taskFile.js";
import {recoverFile, removeRecycleFile, removeRecycleTask} from "@/api/project/task.js";
import {ref} from "vue";
import {listProjectAll} from "@/api/project/project.js";
import {parseTime} from "@/utils/ruoyi.js";

const {proxy} = getCurrentInstance()
const queryParams = ref({
  start: 1,
  end: 10,
  fileName: '',
  pjProjectId: null,
  fileSuffix: '',
  startTime: null,
  endTime: null,
})
const fileSuffixOptions = [
  {label:'doc',value:'doc'},
  {label:'xls',value:'xls'},
  {label:'ppt',value:'ppt'},
  {label:'pdf',value:'pdf'},
  {label:'txt',value:'txt'}
]
const tableData = ref([])
const total = ref(0)
const timeRange = ref([])
const projectOptions = ref([])

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleProjectOptions() {
  listProjectAll().then(res => {
    projectOptions.value = res.data
  })
}

function handleTimeRange() {
  if (timeRange.value !== null) {
    queryParams.value.startTime = timeRange.value[0]
    queryParams.value.endTime = timeRange.value[1]
  } else {
    queryParams.value.startTime = null
    queryParams.value.endTime = null
  }
}

function handleQuery() {
  console.log(queryParams.value)
  getRecycleFile(queryParams.value).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
    console.log(tableData.value)
  })
}


function resetQuery() {
  proxy.resetForm('queryFormRef')
  queryParams.value = {
    start: 1,
    end: 10,
    fileName: '',
    pjProjectId: null,
    fileSuffix: '',
    startTime: null,
    endTime: null,
  };
  handleQuery()
}

function handleRecovery(row) {
  const _id = row.id || ids.value
  recoverFile(_id).then(res => {
    if (res.code === 200) {
      proxy.$message.success('恢复成功')
      handleQuery()
    }
  })
}

function handleDelete(row) {
  const _id = row.id || ids.value
  proxy.$confirm('确定删除选中的任务么？该任务下关联的文件等都将会被删除', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeRecycleFile(_id).then(res => {
      if (res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
}
function getFilePath(name) {
  return import.meta.env.VITE_APP_BASE_API + name
}
function downloadFile(item) {
  window.open(getFilePath(item.filePath))
}
function init() {
  handleProjectOptions()
  handleQuery()
}
init()
</script>
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true">

      <el-form-item label="名称">
        <el-input v-model="queryParams.fileName" placeholder="请输入名称"></el-input>
      </el-form-item>
      <el-form-item label="文件类型">
        <el-select v-model="queryParams.fileSuffix" filterable>
          <el-option v-for="item in fileSuffixOptions" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="删除时间范围">
        <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="To"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            @update:model-value="handleTimeRange"
            :clearable="true"
        />
      </el-form-item>
      <el-form-item label="项目">
        <el-select v-model="queryParams.pjProjectId" value-key="id" filterable>
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Plus"
            @click="handleRecovery"
        >恢复
        </el-button>
        <el-button
            type="danger"
            plain
            icon="Delete"
            @click="handleDelete"
        >彻底删除
        </el-button>
      </el-col>
    </el-row>
    <el-table
        :data="tableData"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column prop="id" label="id" align="center" width="100"></el-table-column>
      <el-table-column prop="fileName" label="文件名称" align="center"></el-table-column>
      <el-table-column prop="fileSuffix" label="文件类型" align="center" width="100"></el-table-column>
      <el-table-column prop="fileSizeStr" label="文件大小" align="center" width="100"></el-table-column>
      <el-table-column prop="taskName" label="所属任务" align="center"></el-table-column>
      <el-table-column prop="createBy" label="创建人" align="center"></el-table-column>
      <el-table-column prop="updateTime" label="删除时间" align="center">
        <template #default="scope">
          {{parseTime(scope.row.updateTime)}}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" @click="downloadFile(scope.row)">下载</el-button>
          <el-button type="success" @click="handleRecovery(scope.row)">还原</el-button>
          <el-button type="danger" @click="handleDelete(scope.row)">彻底删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.start"
        v-model:limit="queryParams.end"
        @pagination="handleQuery"
        layout="total,prev, pager, next, jumper"
    />
  </div>
</template>

<style scoped>
</style>
