<script setup>
import {getRecycleTaskAll,recoverTask,removeRecycleTask} from "@/api/project/task.js";
import {parseTime} from "@/utils/ruoyi.js";
import {ref} from "vue";
import {listProjectAll, removeProject} from "@/api/project/project.js";

const {proxy} = getCurrentInstance()
const queryParams = ref({
  start: 1,
  end: 10,
  name: '',
  pjProjectId: '',
  startTime: null,
  endTime: null,
})
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
  getRecycleTaskAll(queryParams.value).then(res => {
    console.log(res)
    tableData.value = res.data.rows
    total.value = res.total
    console.log(tableData.value)
  })
}

function resetQuery() {
  proxy.resetForm('queryFormRef')
  queryParams.value = {
    start: 0,
    end: 0,
    name: '',
    pjProjectId: '',
    startTime: null,
    endTime: null,
  }
  handleQuery()
}
function handleRecovery(row){
  const _id = row.id || ids.value
  recoverTask(_id).then(res => {
    if(res.code === 200) {
      proxy.$message.success('恢复成功')
      handleQuery()
    }
  })
}
function handleDelete(row){
  const _id = row.id || ids.value
  proxy.$confirm('确定删除选中的任务么？该任务下关联的文件等都将会被删除', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeRecycleTask(_id).then(res => {
      if(res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
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
        <el-input v-model="queryParams.name" placeholder="请输入名称"></el-input>
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
    <el-table :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column prop="id" label="id" align="center" width="100"></el-table-column>
      <el-table-column prop="name" label="名称" align="center"></el-table-column>
      <el-table-column prop="parentName" label="父任务" align="center">
        <template #default="scope">
          <span v-if="scope.row.parentName !== null">{{ scope.row.parentName }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center">
        <template #default="scope">
          <span v-html="scope.row.description"></span>
        </template>
      </el-table-column>
      <el-table-column prop="pjProjectName" label="项目" align="center">
        <template #default="scope">
          <span>{{ scope.row.pjProjectName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="点赞数" prop="likeNum" align="center" width="100">
      </el-table-column>
      <el-table-column label="评论数" prop="logAndCommentSize" align="center" width="100">
      </el-table-column>
      <el-table-column label="文件数" prop="fileNum" align="center" width="100">
      </el-table-column>
      <el-table-column label="删除时间" prop="updateTime" align="center">
        <template #default="scope">
          {{parseTime(scope.row.updateTime)}}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
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