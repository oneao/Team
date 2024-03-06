<script setup>
import {getCollectList,updateCollection} from "@/api/project/project.js";
import ImagePreview from "@/components/ImagePreview/index.vue";
import router from "@/router/index.js";
const {proxy} = getCurrentInstance();
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: null,
  beginTime: null,
  endTime: null,
  status: null,
})

const projectStatusQuery = [
  {label: "全部", value: -1},
  {label: "未开始", value: 0},
  {label: "进行中", value: 1},
  {label: "已结束", value: 2},
]

const tableData = ref([]);
const total = ref(0)
function handleQuery() {
  if (queryParams.value.endTime < queryParams.value.beginTime){
    proxy.$message.warning('结束时间不能小于开始时间')
    loading.value = false;
    return
  }
  getCollectList(queryParams.value).then(res => {
    tableData.value = res.data.rows;
    total.value = res.data.total;
  })
}
function handleReset() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: null,
    beginTime: null,
    endTime: null,
    status: null,
  }
  handleQuery()
}
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleCancelCollect(item) {
  const _id = item.id || ids.value;
  proxy.$confirm('确定取消收藏选中的项目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateCollection(_id).then(res => {
      proxy.$message.success('取消收藏成功')
      handleQuery()
    })
  }).catch(() => {
  })
}

function handleToTask(row) {
  router.push({
    path: '/project/task',
    query: {
      projectId: row.id,
    }
  })
}
function init() {
  handleQuery()
}
init()
</script>
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
      <el-form-item label="项目名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入项目名称"
            clearable
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker clearable
                        v-model="queryParams.beginTime"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker clearable
                        v-model="queryParams.endTime"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="项目状态" prop="status">
        <el-select v-model="queryParams.status" style="width: 120px">
          <el-option
              v-for="item in projectStatusQuery"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleCancelCollect"
        >取消收藏
        </el-button>
      </el-col>
    </el-row>

    <el-table :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="项目名称" align="center" prop="name" width="100"/>
      <el-table-column label="项目描述" align="center" prop="description">
        <template #default="scope">
          <el-popover
              placement="right"
              :title="scope.row.description"
              :width="200"
              trigger="hover"
          >
            <template #reference>
              <span v-if="scope.row.description === null">{{ scope.row.description }}</span>
              <span
                  v-else-if="scope.row.description.toString().length > 20">{{
                  scope.row.description.toString().substring(0, 19)
                }}...</span>
              <span v-else>{{ scope.row.description }}</span>
            </template>
          </el-popover>

        </template>
      </el-table-column>
      <el-table-column label="模板封面" align="center" prop="cover" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.cover" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="计划开始时间" align="center" prop="beginTime" width="140">
        <template #default="scope">
          <span>{{ parseTime(scope.row.beginTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计划结束时间" align="center" prop="endTime" width="140">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际结束时间" align="center" prop="realEndTime" width="140">
        <template #default="scope">
          <span v-if="scope.row.realEndTime !== null">{{ parseTime(scope.row.realEndTime, '{y}-{m}-{d}') }}</span>
          <el-tag v-else type="warning" effect="dark">尚未结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目状态" align="center" prop="status" width="140">
        <template #default="scope">
          <el-tag type="info" effect="dark" v-if="scope.row.status === 0">未开始</el-tag>
          <el-tag type="success" effect="dark" v-else-if="scope.row.status === 1">进行中</el-tag>
          <el-tag type="danger" effect="dark" v-else>已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目进度" align="center" prop="schedule">
        <template #default="scope">
          <el-progress :percentage="scope.row.schedule"
                       :stroke-width="15"
                       striped
                       striped-flow/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" @click="handleCancelCollect(scope.row)">取消收藏</el-button>
          <el-button type="warning" @click="handleToTask(scope.row)">跳转</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
        layout="total,prev, pager, next, jumper"
    />
  </div>
</template>

<style scoped>
</style>