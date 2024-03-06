<script setup>
import {listReportMy,addReport,getReport,updateReport,deleteReport} from "@/api/project/report.js";
import {parseTime} from "@/utils/ruoyi.js";

const {proxy} = getCurrentInstance()

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}


const tableData = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  startTime: null,
  endTime: null,
  name: '',
})
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
  listReportMy(queryParams.value).then(res => {
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
  }
  timeRange.value = []
  handleQuery()
}
const showAddDialog = ref(false)
const addForm = ref({
  name: '',
  description: ''
})
const addRules = ref({
  name: [
    {required: true, message: "名称不能为空", trigger: "blur"}
  ],
  description: [
    {required: true, message: "描述不能为空", trigger: "blur"}
  ]
})
function handleAdd() {
  showAddDialog.value = true
}
function resetAddForm() {
  addForm.value = {
    name: '',
    description: ''
  }
  proxy.resetForm("addFormRef")
}
function confirmAdd() {
  proxy.$refs["addFormRef"].validate(valid => {
    if (valid){
      addReport(addForm.value).then(res => {
        if (res.code === 200) {
          showAddDialog.value = false
          proxy.$message.success('添加成功')
          resetAddForm()
          handleQuery()
        }
      })
    }
  })
}
function cancelAdd() {
  showAddDialog.value = false
  resetAddForm()
}

const showEditDialog = ref(false)
const editForm = ref({
  name: '',
  description: ''
})
const editRules = ref({
  name: [
    {required: true, message: "名称不能为空", trigger: "blur"}
  ],
  description: [
    {required: true, message: "描述不能为空", trigger: "blur"}
  ]
})
function handleEdit(item) {
  const _id = item.id || ids.value[0]
  getReport(_id).then(res => {
    editForm.value = res.data
    showEditDialog.value = true
  })
}
function confirmEdit() {
  proxy.$refs["editFormRef"].validate(valid => {
    if (valid){
      updateReport(editForm.value).then(res => {
        if (res.code === 200) {
          showEditDialog.value = false
          proxy.$message.success('编辑成功')
          resetEditForm()
          handleQuery()
        }
      })
    }
  })
}
function cancelEdit() {
  showEditDialog.value = false
  resetEditForm()
}
function resetEditForm() {
  editForm.value = {
    name: '',
    description: ''
  }
  proxy.resetForm("editFormRef")
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
}

init()
</script>

<template>
  <div class="app-container">
    <el-form :inline="true">
      <el-form-item label="名称">
        <el-input v-model="queryParams.name" placeholder="请输入名称"></el-input>
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
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
        >新增
        </el-button>
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleEdit"
        >更新
        </el-button>
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
      <el-table-column
          type="selection"
          width="55"
          align="center"
      ></el-table-column>
      <el-table-column
          prop="name"
          label="名称"
          align="center"
      ></el-table-column>
      <el-table-column
          prop="description"
          label="描述"
          align="center"
      >
        <template #default="{row}">
          <span v-html="row.description"></span>
        </template>
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="创建时间"
          align="center"
      >
        <template #default="{row}">
          <span>{{ parseTime(row.createTime)}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{row}">
          <el-button
              type="primary"
              @click="handleEdit(row)"
          >编辑
          </el-button>
          <el-button
              type="danger"
              @click="handelDelete(row)"
          >删除
          </el-button>
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

    <el-dialog v-model="showAddDialog" title="新增日志">
      <el-form ref="addFormRef" :rules="addRules" :model="addForm">
        <el-form-item label="名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <Editor v-model="addForm.description"></Editor>
        </el-form-item>
      </el-form>
      <template #footer>
          <el-button type="primary" @click="confirmAdd">提交</el-button>
          <el-button @click="cancelAdd">取消</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="showEditDialog" title="编辑日志">
      <el-form ref="editFormRef" :rules="editRules" :model="editForm">
        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <Editor v-model="editForm.description"></Editor>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="confirmEdit">提交</el-button>
        <el-button @click="cancelEdit">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<style scoped>
</style>
