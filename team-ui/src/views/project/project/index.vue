<script setup name="Project">
import {listProject, getProject, delProject, addProject, updateProject} from "@/api/project/project";
import {
  listUser,
  deptTreeSelect
} from "@/api/system/user";

import {listTemplateAll} from "@/api/project/template";
import ImageUpload from "@/components/ImageUpload/index.vue";
import router from "@/router/index.js";
import ImagePreview from "@/components/ImagePreview/index.vue";

const {proxy} = getCurrentInstance();

const projectList = ref([]);

const openAdd = ref(false);// 添加项目管理对话框
const openUpdate = ref(false);// 修改项目管理对话框

const deptOptions = ref(undefined);// 部门下拉树结构
const userList = ref([]);// 用户列表
const userTotal = ref(0);// 用户总数
/** 查询部门下拉树结构 */
function getDeptTree() {
  deptTreeSelect().then(response => {
    deptOptions.value = response.data;
  });
};
getDeptTree()
/** 通过条件过滤节点  */
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 节点单击事件 */
function handleNodeClick(data) {
  userQueryParams.value.deptId = data.id;
  handleUserQuery();
};

function handleUserQuery() {
  userQueryParams.value.pageNum = 1;
  getUserList();
}

function getUserList() {
  listUser(userQueryParams.value).then(response => {
    userList.value = response.rows;
    userTotal.value = response.total;
    console.log(response)
  });
}

const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);


const templateList = ref([]);
const showTemplateNotExist = ref(false);

const projectStatusQuery = [
  {label: "全部", value: -1},
  {label: "未开始", value: 0},
  {label: "进行中", value: 1},
  {label: "已结束", value: 2},
]
const projectStatusSelect = [
  {label: "未开始", value: 0},
  {label: "进行中", value: 1},
  {label: "已结束", value: 2},
];

const data = reactive({
  addForm: {},
  updateForm: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    beginTime: null,
    endTime: null,
    status: null,
  },
  userQueryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    phonenumber: undefined,
    status: undefined,
    deptId: undefined
  },
  addRules: {
    pjTemplateId: [
      {required: true, message: "项目模版不能为空", trigger: "blur"}
    ],
    name: [
      {required: true, message: "项目名称不能为空", trigger: "blur"}
    ],
    cover: [
      {required: true, message: "项目封面不能为空", trigger: "blur"}
    ],
    timeRange: [
      {required: true, message: "项目时间范围不能为空", trigger: "blur"}
    ],
  },
  updateRules: {
    name: [
      {required: true, message: "项目名称不能为空", trigger: "blur"}
    ],
    cover: [
      {required: true, message: "项目封面不能为空", trigger: "blur"}
    ],
    timeRange: [
      {required: true, message: "项目时间范围不能为空", trigger: "blur"}
    ],
    status: [
      {required: true, message: "项目状态不能为空", trigger: "blur"}
    ],
    schedule: [
      {required: true, message: "项目进度不能为空", trigger: "blur"}
    ]
  }
});

const {queryParams, addForm, addRules, updateForm, updateRules, userQueryParams} = toRefs(data);

/** 查询项目管理列表 */
function getList() {
  loading.value = true;
  if (queryParams.value.endTime < queryParams.value.beginTime) {
    proxy.$message.error('结束时间不能小于开始时间')
    loading.value = false;
    return
  }
  listProject(queryParams.value).then(response => {
    projectList.value = response.data.rows;
    total.value = response.data.total;
    loading.value = false;
  });
}

// 取消添加按钮
function cancelAdd() {
  openAdd.value = false;
  resetAddForm();
}

// 取消修改按钮
function cancelUpdate() {
  openUpdate.value = false;
  resetUpdateForm();
}

// 表单重置
function resetAddForm() {
  addForm.value = {
    pjTemplateId: null,
    name: null,
    description: null,
    cover: null,
    beginTime: null,
    endTime: null,
    timeRange: ''
  };
  proxy.resetForm("addProjectRef");
}

function resetUpdateForm() {
  updateForm.value = {
    name: null,
    description: null,
    cover: null,
    beginTime: null,
    endTime: null,
    status: null,
    timeRange: ''
  };
  proxy.resetForm("updateProjectRef");
}

// 获取项目模版
function getTemplateList() {
  listTemplateAll().then(response => {
    templateList.value = response.data;
    if (templateList.value.length === 0) {
      showTemplateNotExist.value = true
    } else {
      templateList.value = response.data
      addForm.value.cover = response.data[0].cover
      addForm.value.pjTemplateId = response.data[0].value
      openAdd.value = true;
    }
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  resetAddForm();
  getTemplateList()
}

/** 修改按钮操作 */
function handleUpdate(row) {
  resetUpdateForm()
  const _id = row.id || ids.value
  getProject(_id).then(response => {
    updateForm.value = response.data;

    updateForm.value.timeRange = []

    updateForm.value.timeRange.push(updateForm.value.beginTime)
    updateForm.value.timeRange.push(updateForm.value.endTime)

    openUpdate.value = true;
  });
}

/** 提交添加按钮 */
function submitAddForm() {
  proxy.$refs["addProjectRef"].validate(valid => {
    if (valid) {
      addForm.value.beginTime = addForm.value.timeRange[0]
      addForm.value.endTime = addForm.value.timeRange[1]
      addProject(addForm.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        openAdd.value = false;
        getList();
      });
    }
  });
}

/** 提交修改按钮 */
function submitUpdateForm() {
  proxy.$refs["updateProjectRef"].validate(valid => {
    if (valid) {
      updateForm.value.beginTime = updateForm.value.timeRange[0]
      updateForm.value.endTime = updateForm.value.timeRange[1]
      updateProject(updateForm.value).then(response => {
        proxy.$modal.msgSuccess("修改成功");
        openUpdate.value = false;
        getList();
      });
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除项目管理编号为"' + _ids + '"的数据项？').then(function () {
    return delProject(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('project/project/export', {
    ...queryParams.value
  }, `project_${new Date().getTime()}.xlsx`)
}

// 项目模版改变
function handleTemplateChange() {
  templateList.value.forEach(item => {
    if (item.value === form.value.pjTemplateId) {
      form.value.cover = item.cover
    }
  })
}

// 项目模版不存在
function handleTemplateNotExistConfirm() {
  router.push('/project/template')
}

// 项目模版不存在取消
function handleTemplateNotExistCancel() {
  showTemplateNotExist.value = false
}

// 添加项目参与人员
function handleGetUser(row) {
  router.push({
    path: '/project/pjuser',
    query: {
      projectId: row.id,
      projectName:row.name
    }
  })
}

// 处理跳转任务管理
function handleToTask(row) {
  router.push({
    path: '/project/task',
    query: {
      projectId: row.id,
    }
  })
}
import {updateArchive} from "@/api/project/project";
function handleArchive(row) {
  const _id = row.id || ids.value
  proxy.$confirm('确定将项目选中的项目归档么？归档后在归档列表查询。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateArchive(_id).then(res => {
      if (res.code === 200) {
        proxy.$message.success('归档成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
}
getList();
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['project:project:add']"
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
            v-hasPermi="['project:project:edit']"
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
            v-hasPermi="['project:project:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange">
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
          <el-button link icon="User" @click="handleGetUser(scope.row)">
            成员
          </el-button>
          <el-button link type="success" @click="handleToTask(scope.row)" icon="SetUp">
            任务
          </el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['project:project:edit']">修改
          </el-button>
          <el-button link type="info"  @click="handleArchive(scope.row)" >
            <template #icon>
              <svg t="1709276818859" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3545" width="16" height="16"><path d="M768 1024H256C155.177143 1024 73.142857 941.965714 73.142857 841.142857V182.857143C73.142857 82.034286 155.177143 0 256 0h365.714286c100.822857 0 182.857143 82.034286 182.857143 182.857143v585.142857a109.84 109.84 0 0 1-109.714286 109.714286H329.142857a36.571429 36.571429 0 0 0 0 73.142857h438.857143a109.84 109.84 0 0 0 109.714286-109.714286V182.857143a36.571429 36.571429 0 0 1 73.142857 0v658.285714c0 100.822857-82.034286 182.857143-182.857143 182.857143zM256 73.142857a109.84 109.84 0 0 0-109.714286 109.714286v658.285714a109.897143 109.897143 0 0 0 77.862857 104.994286A109.714286 109.714286 0 0 1 329.142857 804.571429h365.714286a36.571429 36.571429 0 0 0 36.571428-36.571429V182.857143a109.84 109.84 0 0 0-109.714285-109.714286z" fill="#1296db" p-id="3546"></path><path d="M292.571429 292.571429m36.571428 0l219.428572 0q36.571429 0 36.571428 36.571428l0 0q0 36.571429-36.571428 36.571429l-219.428572 0q-36.571429 0-36.571428-36.571429l0 0q0-36.571429 36.571428-36.571428Z" fill="#1296db" p-id="3547"></path><path d="M292.571429 513.142857m36.571428 0l219.428572 0q36.571429 0 36.571428 36.571429l0 0q0 36.571429-36.571428 36.571428l-219.428572 0q-36.571429 0-36.571428-36.571428l0 0q0-36.571429 36.571428-36.571429Z" fill="#1296db" p-id="3548"></path></svg>
            </template>
            归档
          </el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['project:project:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加项目管理对话框 -->
    <el-dialog title="添加项目" v-model="openAdd" width="500px" append-to-body>
      <el-form ref="addProjectRef" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="项目模版" prop="pjTemplateId">
          <el-select v-model="addForm.pjTemplateId"
                     @change="handleTemplateChange"
                     style="width: 240px">
            <el-option
                v-for="item in templateList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入项目名称"/>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input v-model="addForm.description" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="项目封面" prop="cover">
          <template #default="scope">
            <image-upload v-model="addForm.cover" :limit="1"/>
          </template>
        </el-form-item>
        <el-form-item label="时间范围" prop="timeRange">
          <el-date-picker
              v-model="addForm.timeRange"
              type="daterange"
              range-separator="➡️"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD"
              style="width: 220px"
          />
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAddForm">确 定</el-button>
          <el-button @click="cancelAdd">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 修改项目管理对话框  -->
    <el-dialog title="修改项目" v-model="openUpdate" width="500px" append-to-body>
      <el-form ref="updateProjectRef" :model="updateForm" :rules="updateRules" label-width="80px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="updateForm.name" placeholder="请输入项目名称"/>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input v-model="updateForm.description" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="项目封面" prop="cover">
          <template #default="scope">
            <image-upload v-model="updateForm.cover" :limit="1"/>
          </template>
        </el-form-item>
        <el-form-item label="时间范围" prop="timeRange">
          <el-date-picker
              v-model="updateForm.timeRange"
              type="daterange"
              range-separator="➡️"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD"
              style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <div class="custom-div">
            <el-select v-model="updateForm.status"
                       style="width: 140px">
              <el-option
                  v-for="item in projectStatusSelect"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
            <span class="custom-span">注：已结束将自动设置进度为100%</span>
          </div>
        </el-form-item>
        <el-form-item label="项目进度" prop="schedule">
          <div class="custom-div">
            <el-slider v-model="updateForm.schedule"/>
            <!--            <el-input-number v-model="updateForm.schedule" :min="0" :max="100"></el-input-number>-->
            <span class="custom-span">注：项目进度范围为0~100</span>
          </div>
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitUpdateForm">确 定</el-button>
          <el-button @click="cancelUpdate">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--  无项目模版的处理方法  -->
    <el-dialog title="暂无项目模版" v-model="showTemplateNotExist" width="500px">
      <span>暂无项目模版，请先添加项目模版</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleTemplateNotExistConfirm">跳 转</el-button>
          <el-button @click="handleTemplateNotExistCancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 更多操作 -->

  </div>
</template>

<style scoped>
.custom-div {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.custom-span {
  font-size: smaller;
  color: grey;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.custom-span {
  font-size: 14px;
  font-weight: bold;
}

.top-container {
  padding: 10px;
  border-bottom: 1px solid #ccc;
}

.middle-container {
  padding: 10px;
  border-bottom: 1px solid #ccc;
}

.middle-top {
  margin-bottom: 10px;
}

.search-container {
  display: flex;
}

.search-container .el-input {
  margin-right: 20px; /* 控制输入框与按钮之间的间距 */
}

.middle-bottom {
  display: flex;
  align-items: flex-start;
}

.middle-left {
  width: 30%;
  padding-right: 10px;
}

.middle-right {
  width: 70%;
  padding-left: 10px;
}

.bottom-container {
  padding: 10px;
}

</style>