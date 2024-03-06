<script setup>
import {listRecycleProject,recoveryProject,removeProject} from '@/api/project/project'
import {parseTime} from "@/utils/ruoyi.js";
import ImagePreview from "@/components/ImagePreview/index.vue";
import {ref} from "vue";
import {removeBug} from "@/api/project/bug.js";

const {proxy} = getCurrentInstance()

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}


const timeRange = ref([])

function handleTimeRange() {
  if (timeRange.value !== null) {
    queryParams.value.startTime = parseTime(timeRange.value[0])
    queryParams.value.endTime = parseTime(timeRange.value[1])
  } else {
    queryParams.value.startTime = null
    queryParams.value.endTime = null
  }
}

const queryParams = ref({
  start: 1,
  end: 10,
  startTime: null,
  endTime: null,
  name: '',
})
const projectList = ref([])
const total = ref(0)
const handleQuery = () => {
  console.log(queryParams.value)
  listRecycleProject(queryParams.value).then(res => {
    projectList.value = res.data.rows
    total.value = res.data.total
  })
}
const resetQuery = () => {
  proxy.resetForm('queryRef')
  queryParams.value = {
    start: 1,
    end: 10,
    startTime: null,
    endTime: null,
    name: ''
  };
  handleQuery()
}
function handleRecovery(row){
  const _id = row.id || ids.value
  recoveryProject(_id).then(res => {
    if(res.code === 200) {
      proxy.$message.success('恢复成功')
      handleQuery()
    }
  })
}
function handleDelete(row){
  const _id = row.id || ids.value
  proxy.$confirm('确定删除该项目么？该项目下关联的文件、任务等都将会被删除', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeProject(_id).then(res => {
      if(res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
}
function init() {
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
        >恢复
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            @click="handleDelete"
        >彻底删除
        </el-button>
      </el-col>
    </el-row>
    <el-table :data="projectList"  @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>
      <el-table-column prop="id" label="id" align="center"></el-table-column>
      <el-table-column prop="cover" label="封面" align="center">
        <template #default="scope">
          <image-preview style="width: 60px;height: 60px" :src="scope.row.cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" align="center"></el-table-column>
      <el-table-column prop="description" label="描述" align="center"></el-table-column>
      <el-table-column prop="isArchive" label="是否归档" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.isArchive === 1">是</el-tag>
          <el-tag v-else>否</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="删除时间" align="center">
        <template #default="scope">
          {{parseTime(scope.row.createTime)}}
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