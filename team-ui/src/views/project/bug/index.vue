<script setup>
import {ref} from 'vue'
import {getBugList} from '@/api/project/bug'
import {listProjectAll} from "@/api/project/project.js";
import {parseTime} from "@/utils/ruoyi.js";
const {proxy} = getCurrentInstance();
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name:'',
    pjProjectId: null,
    severity: null,
    priority: null,
    status: null,
  },
  updateForm:{},
  updateFormRules:{
    name: [
      {required: true, message: '请填写项目名称', trigger: 'change'}
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
    status: [
      {required: true, message: '请输入bug描述', trigger: 'change'}
    ]
  }
})
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
const {queryParams,updateForm,updateFormRules} = toRefs(data);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}


const projectOptions = ref([])
const loading = ref(true)
const bugList = ref([])
const total = ref(0)
function list() {
  getBugList(queryParams.value).then(res => {
    bugList.value = res.data.rows
    total.value = res.data.total
    loading.value = false
  })
}
function handleProjectOptions(){
  listProjectAll().then(res => {
    projectOptions.value = res.data
  })
}

function resetQuery(){
  proxy.resetForm("queryRef");
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name:'',
    pjProjectId: null,
    severity: null,
    priority: null,
    status: null
  }
  handleQuery()
}
function handleQuery(){
  queryParams.value.pageNum = 1
  list()
}
import {getAllOption,getBugDetail,updateBug,removeBug} from "@/api/project/bug";
// 更新
const projectList = ref([])
const taskList = ref([])
const userList = ref([])
const openUpdate = ref(false)
function handleUpdate(row){
  resetUpdateForm()
  const _id = row.id || ids.value
  getBugDetail(_id).then(res => {
    updateForm.value = res.data
  })
  getAllOption().then(res => {
    projectList.value = res.data
    const selectedProject = projectList.value.find(item => item.id === updateForm.value.pjProjectId)
    if (selectedProject) {
      taskList.value = selectedProject.pjTaskList
      userList.value = selectedProject.userList
    } else {
      taskList.value = [] // 如果没有找到匹配的项目，则将任务列表清空
      userList.value = []
    }
  })
  openUpdate.value = true
}

watch(() => updateForm.value.pjProjectId, (newVal, oldVal) => {
  if(oldVal === undefined){
    return
  }
  updateForm.value.pjTaskId = null
  updateForm.value.assignTo = null
  const selectedProject = projectList.value.find(item => item.id === newVal)
  if (selectedProject) {
    taskList.value = selectedProject.pjTaskList
    userList.value = selectedProject.userList
  } else {
    taskList.value = [] // 如果没有找到匹配的项目，则将任务列表清空
    userList.value = []
  }
})
function cancelUpdate() {
  openUpdate.value = false;
  resetUpdateForm();
}
function confirmUpdate() {
  proxy.$refs["updateFormRef"].validate(valid => {
    if (valid) {
      updateBug(updateForm.value).then(res => {
        if (res.code === 200) {
          proxy.$message.success('修改成功')
          openUpdate.value = false
          resetUpdateForm()
          list()
        }
      })
    }
  })
}
function handleDelete(row){
  const _ids = row.id || ids.value
  proxy.$confirm('确定删除选中的数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeBug(_ids).then(res => {
      if (res.code === 200) {
        proxy.$message.success('删除成功')
        list()
      }
    })
  }).catch(() => {
  });
}
function resetUpdateForm() {
  updateForm.value = {};
  taskList.value = []
  userList.value = []
  projectList.value = []
  proxy.resetForm("updateFormRef");
}
function init() {
  list()
  handleProjectOptions()
}
init()
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
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
      <el-form-item label="项目">
        <el-select v-model="queryParams.pjProjectId" value-key="id" filterable>
          <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
    </el-row>
    <el-table v-loading="loading" :data="bugList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="id" align="center" prop="id" width="50"></el-table-column>
      <el-table-column label="名称" align="center" prop="name"></el-table-column>
      <el-table-column label="Bug描述" align="center" prop="description">
        <template v-slot="{row}">
          <span v-html="row.description"></span>
        </template>
      </el-table-column>
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
      <el-table-column label="关联项目" align="center" prop="pjProjectName" ></el-table-column>
      <el-table-column label="关联任务" align="center" prop="pjProjectName" >
        <template v-slot="{row}">
          <span v-if="row.pjTaskName !== '' && row.pjTaskName !== null">{{row.pjTaskName}}</span>
          <span v-else>未关联任务</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" >
        <template v-slot="{row}">
          <el-tag v-if="row.status === 0" type="danger">未解决</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已解决</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="解决时间" align="center" prop="solveTime" >
        <template v-slot="{row}">
          <span v-if="row.status === 0" type="danger">未解决</span>
          <span v-else-if="row.status === 1" type="success">{{ parseTime(row.solveTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="指定人" align="center" prop="assignToName" >
        <template v-slot="{row}">
          <span v-if="row.assignToName !== null && row.assignToName !== ''">{{row.assignToName}}</span>
          <span v-else>未指定</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" ></el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" >
        <template v-slot="{row}">
          <span>{{parseTime(row.createTime)}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template v-slot="{row}">
          <el-button type="primary" @click="handleUpdate(row)">编辑</el-button>
          <el-button  type="danger" @click="handleDelete(row)" >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="list"
        layout="total,prev, pager, next, jumper"
    />

    <el-dialog title="Bug修改" v-model="openUpdate">
      <el-form ref="updateFormRef" :model="updateForm" :rules="updateFormRules">
        <el-form-item label="名称" prop="name">
          <el-input v-model="updateForm.name" placeholder="输入bug名称"></el-input>
        </el-form-item>
        <el-form-item label="项目" prop="pjProjectId">
          <el-select v-model="updateForm.pjProjectId">
            <el-option v-for="item in projectList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务" prop="pjTaskId">
          <el-select v-model="updateForm.pjTaskId" :clearable="true">
            <el-option v-for="item in taskList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
          <span v-if="updateForm.pjProjectId === null" style="margin-left: 5px;color: red">请先选择项目，任务可为空。</span>
        </el-form-item>
        <el-form-item label="严重程度" prop="severity">
          <el-radio-group v-model="updateForm.severity" style="margin-top: -3.5px">
            <el-radio :label=0 la size="large">低</el-radio>
            <el-radio :label=1 size="large">中</el-radio>
            <el-radio :label=2 size="large">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="updateForm.priority" style="margin-top: -3.5px">
            <el-radio :label=0 la size="large">低</el-radio>
            <el-radio :label=1 size="large">中</el-radio>
            <el-radio :label=2 size="large">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="bug描述" prop="description">
          <div style="max-width: 100%;">
            <Editor v-model="updateForm.description"/>
          </div>
        </el-form-item>
        <el-form-item label="是否解决" prop="status">
          <el-select v-model="updateForm.status">
            <el-option label="未解决" :value="0"></el-option>
            <el-option label="已解决" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="指定人" prop="assignTo">
          <el-select v-model="updateForm.assignTo" value-key="userId">
            <el-option v-for="item in userList" :key="item.userId" :label="item.nickName" :value="item.userId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建议" prop="opinion">
          <el-input type="textarea" v-model="updateForm.suggestion"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="confirmUpdate" style="width: 49%" >保存</el-button>
          <el-button @click="cancelUpdate" style="width: 49%">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>