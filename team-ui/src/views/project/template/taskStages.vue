<script setup name="templateTaskStages">
import {ref} from "vue";
import router from "@/router/index.js";
import {Delete, Edit, Plus} from "@element-plus/icons-vue";
import {
  listTaskStagesTemplate,
  getTaskStagesTemplate,
  addTaskStagesTemplate,
  updateTaskStagesTemplate,
  delTaskStagesTemplate
} from '@/api/project/template'

const {proxy} = getCurrentInstance();

const title = ref('')
const templateName = ref('')
const templateId = ref()
const showDialog = ref(false)
const showDeleteDialog = ref(false)
const taskStagesList = ref([])
const loading = ref(true)
const taskStageName = ref('')
const taskStageId = ref(-1)
const data = reactive({
  form: {},
  rules: {
    name: [
      {required: true, message: "项目模版名称不能为空", trigger: "blur"}
    ],
    sortNum: [
      {required: true, message: "项目模版描述不能为空", trigger: "blur"}
    ]
  }
});
const {form, rules} = toRefs(data);

function reset() {
  form.value = {
    name: "",
    sortNum: 0
  };
  proxy.resetForm("taskStagesRef");
}

function getList() {
  loading.value = true
  listTaskStagesTemplate(templateId.value).then(response => {
    taskStagesList.value = response.data
    loading.value = false
  })
}

function init() {
  templateName.value = router.currentRoute.value.query.name
  templateId.value = router.currentRoute.value.query.id
  getList()
}

function addTask() {
  reset()
  showDialog.value = true
  title.value = '添加任务列表模版'
}

function updateTask(row) {
  reset()
  const _id = row.id || ids.value
  getTaskStagesTemplate(_id).then(response => {
    form.value = response.data
    showDialog.value = true
    title.value = '修改任务列表模版'
  })
}

function cancel() {
  showDialog.value = false;
  reset();
}

function submitForm() {
  proxy.$refs["taskStagesRef"].validate(valid => {
    if (valid) {
      form.value.pjTemplateId = templateId.value
      if (form.value.id != null) {
        updateTaskStagesTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          showDialog.value = false;
          getList();
        });
      } else {
        addTaskStagesTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          showDialog.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  taskStageName.value = row.name
  taskStageId.value = row.id
  showDeleteDialog.value = true
}

function cancelDelete() {
  taskStageName.value = ''
  taskStageId.value = -1
  showDeleteDialog.value = false
}

function confirmDelete() {
  delTaskStagesTemplate(taskStageId.value).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
    showDeleteDialog.value = false
  }).catch(() => {

  });
}

function toLast(){
  router.back()
}
init()
</script>
<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <el-row :gutter="24" >
          <el-col :span="12">
            <el-button size="large" type="primary" :icon="Plus" @click="addTask">添加任务列表</el-button>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-button type="warning" size="large" @click="toLast">返回</el-button>
          </el-col>
        </el-row>

        <div class="header-text">
          <el-text size="large">「{{ templateName }}」任务列表</el-text>
        </div>
      </el-header>
      <el-main style="margin-top: 20px">
        <div v-if="taskStagesList.length > 0">
          <div v-for="item in taskStagesList" :key="item.id" class="container">
            <div class="top">
              <div class="left">
                <div class="top-left">
                  <!-- 左上部分内容 -->
                  <span class="left-label">{{ item.name }}</span>
                </div>
                <div class="bottom-left">
                  <!-- 左下部分内容 -->
                  排序：{{ item.sortNum }}
                </div>
              </div>
              <div class="right">
                <!-- 右边部分内容 -->
                <el-button type="primary" :icon="Edit" @click="updateTask(item)" style="font-size: 18px" text/>
                <el-divider direction="vertical" class="separator"/>
                <el-button type="danger" :icon="Delete" @click="handleDelete(item)" style="font-size: 18px" text/>
              </div>
            </div>
            <div class="bottom">
              <el-divider style="margin-top: 10px"/>
            </div>
          </div>
        </div>
        <div v-else>
          <el-empty>
          </el-empty>
        </div>
      </el-main>
    </el-container>
    <el-dialog :title="title" v-model="showDialog" append-to-body width="500px" overflow :close-on-click-modal="false">
      <el-form ref="taskStagesRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称"/>
        </el-form-item>
        <el-form-item label="排序" prop="sortNum">
          <el-input-number v-model="form.sortNum" :min="1"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="showDeleteDialog" title="提醒" top="300px" width="500" center :close-on-click-modal="false" >
      <span>
        确定删除该《{{ taskStageName }}》任务列表模版吗？
      </span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDelete">取消</el-button>
          <el-button type="primary" @click="confirmDelete">
            确认删除
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.app-container {
  background-color: #f5f5f5;
  width: 95%;
  margin: 30px auto;
}

.header-text {
  margin-top: 10px;
  margin-bottom: 10px;
  font-size: 16px;
}

.container {
  display: flex;
  flex-direction: column; /* 更新：使容器内部垂直布局 */
  align-items: stretch;
}

.top {
  display: flex;
  align-items: center;
}

.left {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.right {
  width: 120px; /* 右边部分固定宽度 */
  display: flex;
  align-items: center;
}

.left-label {
  font-weight: bold;
}

.top-left {
  padding-bottom: 10px;
}

.bottom-left {
  font-size: 14px; /* 设置字体大小 */
  font-weight: normal; /* 设置字体粗细 */
}

.separator {
  margin: 0 5px; /* 调整竖线与按钮之间的间距 */
  color: #999; /* 设置竖线颜色 */
}


</style>
