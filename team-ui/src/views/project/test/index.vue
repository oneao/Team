<script setup>
import {listPost} from "@/api/system/post.js";
import {listDept} from "@/api/system/dept.js";
import {listUser} from "@/api/system/user.js";
import {deleteReport, listReport} from "@/api/project/report.js";
import {parseTime} from "@/utils/ruoyi.js";

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
const {proxy} = getCurrentInstance()
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  startTime: null,
  endTime: null,
  name: '',
  postId: null,
  deptId: null,
  userId: null
})
const tableData = ref([])
const total = ref(0)

const postOptions = ref([])
const deptOptions = ref([])
const userOptions = ref([])

function getPostOptions() {
  listPost().then(res => {
    postOptions.value = res.rows
  })
}

function handleUserOptionsVisible(val) {
  if (val) {
    var obj = {
      deptId: queryParams.value.deptId,
      postId: queryParams.value.postId
    }
    listUser(obj).then(res => {
      userOptions.value = res.rows
    })
  } else {
    userOptions.value = []
  }
}

function getDeptOptions() {
  listDept().then(res => {
    deptOptions.value = res.data
  })
}

const timeRange = ref([])

const shortcuts = [
  {
    text: '今天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setHours(0, 0, 0, 0) // 设置为当天的开始时间
      end.setHours(23, 59, 59, 999) // 设置为当天的结束时间
      return [start, end]
    },
  },
  {
    text: '本周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - start.getDay()) // 设置为本周的开始时间
      end.setDate(start.getDate() + 6) // 设置为本周的结束时间
      end.setHours(23, 59, 59, 999) // 设置为本周的最后一天的结束时间
      return [start, end]
    },
  },
  {
    text: '本月',
    value: () => {
      const end = new Date()
      const start = new Date(end.getFullYear(), end.getMonth(), 1) // 设置为本月的开始时间
      end.setMonth(start.getMonth() + 1, 0) // 设置为本月的最后一天的结束时间
      end.setHours(23, 59, 59, 999) // 设置为本月的最后一天的结束时间
      return [start, end]
    },
  },
]

function handleTimeRange(val) {
  if (val !== null) {
    queryParams.value.startTime = val[0]
    queryParams.value.endTime = val[1]
  } else {
    queryParams.value.startTime = null
    queryParams.value.endTime = null
  }
}

function handleQuery() {
  listReport(queryParams.value).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  })
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    startTime: null,
    endTime: null,
    name: '',
    postId: null,
    deptId: null
  }
  postOptions.value = []
  deptOptions.value = []
  userOptions.value = []
  handleQuery()
}

function handelDelete(row) {
  const _id = row.id || ids.value
  proxy.$confirm('是否删除选中日志?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteReport(_id).then(res => {
      if (res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {})
}
function init() {
  handleQuery()
  getPostOptions()
  getDeptOptions()
}

init()
</script>
<template>
  <div class="app-container">
    <el-form :inline="true">
      <el-form-item label="名称">
        <el-input placeholder="请输入名称" style="width: 150px"></el-input>
      </el-form-item>
      <el-form-item label="岗位">
        <el-select v-model="queryParams.postId" value-key="postId" style="width: 150px">
          <el-option
              v-for="item in postOptions"
              :key="item.postId"
              :label="item.postName"
              :value="item.postId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="部门">
        <el-select v-model="queryParams.deptId" value-key="deptId " style="width: 150px">
          <el-option
              v-for="item in deptOptions"
              :key="item.deptId"
              :label="item.deptName"
              :value="item.deptId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="用户">
        <el-select v-model="queryParams.userId" value-key="deptId" @visible-change="handleUserOptionsVisible" style="width: 200px">
          <el-option
              v-for="item in userOptions"
              :key="item.userId"
              :label="item.nickName"
              :value="item.userId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="shortcuts"
            @change="handleTimeRange"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handelDelete"
        >删除
        </el-button>
      </el-col>
    </el-row>
    <el-table
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection"></el-table-column>
      <el-table-column prop="id" label="id" width="55"></el-table-column>
      <el-table-column prop="name" label="名称" align="center"> </el-table-column>
      <el-table-column prop="description" label="描述" align="center">
        <template #default="{row}">
          <span v-html="row.description"></span>
        </template>
      </el-table-column>
      <el-table-column prop="createBy" label="创建人" align="center">
        <template #default="{row}">
          {{row.sysUser.nickName}}
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="部门" align="center"></el-table-column>
      <el-table-column prop="postName" label="岗位" align="center"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template #default="{row}">
          {{parseTime(row.createTime)}}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{row}">
          <el-button type="danger" @click="handelDelete(row)">删除</el-button>
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

