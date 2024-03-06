<script setup>
import {ref, reactive, watch} from 'vue'
import router from "@/router/index.js";
import {listTask, addTask, getTask, updateTask, removeTask} from '@/api/project/task'
import {addTaskStage, updateTaskStage, removeTaskStage} from "@/api/project/taskStages.js";
import {list as getUserList} from '@/api/project/user'
// 公共==========================================
const {proxy} = getCurrentInstance();

import {getProject} from "@/api/project/project.js";

const currentProjectId = ref(null)
const currentProject = ref({})
import {setProjectToken, removeProjectToken} from "@/utils/projectAuth.js";

// 获取当前项目
function getCurrentProject() {
  getProject(currentProjectId.value).then(res => {
    currentProject.value = res.data
  })
}

// 任务类型
const taskType = {
  NAME: 0,
  STATUS: 1,
  URGENCY: 2,
  ORDER: 3,
  DESCRIPTION: 4,
  ASSIGN: 5,
  TIME: 6,
  TAG: 7,
  CHILD_TASK: 8,
  FILE: 9
}
// 任务列表相关==========================================
import {ArrowRight, Delete, More, RefreshRight} from "@element-plus/icons-vue";
import {parseTime} from "@/utils/ruoyi.js";
import Editor from "@/components/Editor/index.vue";

const taskList = ref([]) // 任务列表
// 添加任务阶段===================
const showAddTaskStageDialog = ref(false) // 是否显示添加任务阶段弹窗
const addTaskStageForm = ref({
  taskStageName: '',
  sortNum: 0
}) // 添加任务阶段表单
const addTaskStageRules = {
  taskStageName: [{required: true, message: "任务列表名称不能为空", trigger: "blur"}],
  sortNum: [{required: true, message: "排序顺序不能为空", trigger: "blur"}]
} // 添加任务阶段表单验证规则
// 添加任务阶段
function handleAddTaskStage() {
  showAddTaskStageDialog.value = true
}

function confirmAddTaskStage() {
  proxy.$refs["addTaskStageRef"].validate(valid => {
    if (valid) {
      const obj = {
        projectId: currentProjectId.value,
        taskStageName: addTaskStageForm.value.taskStageName,
        sortNum: addTaskStageForm.value.sortNum
      }
      addTaskStage(obj).then(res => {
        showAddTaskStageDialog.value = false
        cancelAddTaskStage() // 取消添加任务阶段
        proxy.$message.success('添加成功')
        getTaskList()
      })
    }
  })
}// 确认添加任务阶段
function cancelAddTaskStage() {
  showAddTaskStageDialog.value = false
  addTaskStageForm.value.taskStageName = ''
  addTaskStageForm.value.sortNum = 0
  proxy.resetForm("addTaskStageRef");
}// 取消添加任务阶段
// 修改项目阶段===================
const showUpdateTaskStageDialog = ref(false) // 是否显示修改任务阶段弹窗
const updateTaskStageForm = ref({
  taskStageId: null,
  taskStageName: '',
  sortNum: 1
}) // 修改任务阶段表单
const updateTaskStageRules = {
  taskStageId: [{required: true, message: "任务列表id不能为空", trigger: "blur"}],
  taskStageName: [{required: true, message: "任务列表名称不能为空", trigger: "blur"}],
  sortNum: [{required: true, message: "排序顺序不能为空", trigger: "blur"}]
} // 修改任务阶段表单验证规则
function handleUpdateTaskStage(item) {
  updateTaskStageForm.value.taskStageId = item.taskStageId
  updateTaskStageForm.value.taskStageName = item.taskStageName
  updateTaskStageForm.value.sortNum = item.taskStageSortNum
  console.log(updateTaskStageForm.value)
  showUpdateTaskStageDialog.value = true
}// 修改任务阶段
function confirmUpdateTaskStage() {
  proxy.$refs["updateTaskStageRef"].validate(valid => {
    if (valid) {
      const obj = {
        taskStageId: updateTaskStageForm.value.taskStageId,
        taskStageName: updateTaskStageForm.value.taskStageName,
        sortNum: updateTaskStageForm.value.sortNum
      }
      updateTaskStage(obj).then(res => {
        showUpdateTaskStageDialog.value = false
        cancelUpdateTaskStage() // 取消修改任务阶段
        proxy.$message.success('修改成功')
        getTaskList()
      })
    }
  })
}// 确认修改任务阶段
function cancelUpdateTaskStage() {
  showUpdateTaskStageDialog.value = false
  updateTaskStageForm.value.taskStageId = null
  updateTaskStageForm.value.taskStageName = ''
  updateTaskStageForm.value.sortNum = 0
  proxy.resetForm("updateTaskStageRef");
}// 取消修改任务阶段
// 删除项目阶段===================
const showDeleteTaskStageDialog = ref(false) // 是否显示删除任务阶段弹窗
const deleteTaskStageForm = ref({
  taskStageId: null,
  taskStageName: ''
}) // 删除任务阶段表单
const deleteTaskStageRules = {
  taskStageId: [{required: true, message: "任务列表id不能为空", trigger: "blur"}],
} // 删除任务阶段表单验证规则
function handleDeleteTaskStage(item) {
  deleteTaskStageForm.value.taskStageId = item.taskStageId
  deleteTaskStageForm.value.taskStageName = item.taskStageName
  showDeleteTaskStageDialog.value = true
}// 删除任务阶段
function confirmDeleteTaskStage() {
  proxy.$refs["deleteTaskStageRef"].validate(valid => {
    if (valid) {
      removeTaskStage(deleteTaskStageForm.value.taskStageId).then(res => {
        showDeleteTaskStageDialog.value = false
        cancelDeleteTaskStage() // 取消删除任务阶段
        proxy.$message.success('删除成功')
        getTaskList()
      })
    }
  })
}// 确认删除任务阶段
// 取消删除任务阶段
function cancelDeleteTaskStage() {
  showDeleteTaskStageDialog.value = false
  deleteTaskStageForm.value.taskStageId = null
  deleteTaskStageForm.value.taskStageName = ''
  proxy.resetForm("deleteTaskStageRef");
}

// 任务相关==========================================
// 新增任务====================
const currentTaskStageName = ref('') // 当前任务列表名称
const addTaskForm = ref({
  taskStageId: null,
  name: '',
  sortNum: 1
})// 新增任务表单
const addTaskRules = {
  taskStageId: [{required: true, message: "任务列表id不能为空", trigger: "blur"}],
  name: [{required: true, message: "任务名称不能为空", trigger: "blur"}],
  sortNum: [{required: true, message: "排序顺序不能为空", trigger: "blur"}]
}// 新增任务表单验证规则
function confirmAddTask() {
  // 多个表单for循环验证，使用[0]解决
  proxy.$refs["addTaskRef"][0].validate(valid => {
    if (valid) {
      addTask(addTaskForm.value).then(res => {
        proxy.$message.success('添加成功')
        cancelAddTask()
        getTaskList()
      })
    }
  })
} // 确认添加任务
function cancelAddTask() {
  taskList.value.forEach(item => {
    if (item.showAddTask) {
      item.showAddTask = false
    }
  })
  addTaskForm.value.taskStageId = null
  addTaskForm.value.name = ''
  addTaskForm.value.sortNum = 1
} // 取消添加任务
function handleAddTask(item) {
  addTaskForm.value.name = ''
  addTaskForm.value.sortNum = 1
  addTaskForm.value.taskStageId = item.taskStageId
  taskList.value.forEach(item => {
    if (item.taskStageId === addTaskForm.value.taskStageId) {
      item.showAddTask = true
    } else {
      item.showAddTask = false
    }
  })
}// 添加任务
// 获取单个任务==================
const currentTask = ref({}) // 任务
const currentTaskId = ref(null)
let oldTask = {
  description: '',
  beginTime: '',
  endTime: '',
  status: -1
}
const showTaskDialog = ref(false) // 是否显示任务弹窗
function handleGetTask(item) {
  getTask(item.id).then(res => {
    currentTask.value = res.data
    oldTask.description = currentTask.value.description
    oldTask.status = currentTask.value.status
    disabledTaskDescription.value = true
    // 获取当前任务的开始时间和结束时间
    taskDateRange.value = []
    taskDateRange.value.push(currentTask.value.beginTime)
    taskDateRange.value.push(currentTask.value.endTime)
    // 获取当前任务所在的任务列表名称
    taskList.value.forEach(s => {
      if (s.taskStageId === item.pjTaskStagesId) {
        currentTaskStageName.value = s.taskStageName
      }
    })
    // 设置显示添加子任务按钮
    showAddChildTask.value = true
    // 获取项目用户列表
    getUserListByProjectId()
  })
  showTaskDialog.value = true
}

const isExecuteGetTaskByDescription = ref(false)

// 根据id获取任务
function getTaskByTaskId() {
  getTask(currentTask.value.id).then(res => {
    currentTask.value = res.data
    oldTask.description = currentTask.value.description
    oldTask.status = currentTask.value.status
    disabledTaskDescription.value = true

    taskDateRange.value = []
    taskDateRange.value.push(currentTask.value.beginTime)
    taskDateRange.value.push(currentTask.value.endTime)

    showAddChildTask.value = true

    isExecuteGetTaskByDescription.value = true

  })
}

function handleUpdateTask() {
  updateTask(currentTask.value).then(res => {
    proxy.$message.success('更新任务成功')
    getTaskByTaskId()
  })
}

// 更新任务名称
function handleUpdateTaskName() {
  if (currentTask.value.name === '') {
    proxy.$message.error('任务名称不能为空')
    return;
  }
  currentTask.value.description = oldTask.description
  currentTask.value.taskType = taskType.NAME // 操作类型：名称
  handleUpdateTask()
}

// 更新任务状态
function handleUpdateTaskByStatus() {
  currentTask.value.description = oldTask.description
  currentTask.value.taskType = taskType.STATUS // 操作类型：状态
  updateTask(currentTask.value).then(res => {
    proxy.$message.success('更新任务状态成功')
    getTaskByTaskId()
  }).catch(err => {
    getTaskByTaskId()
  })
}

const currentTaskChange = ref(false)
watch(() => currentTask.value.id, (newVal, oldVal) => {
  currentTaskChange.value = !currentTaskChange.value
})

// 更新任务紧急状态
watch(() => currentTask.value.urgency, (newVal, oldVal) => {
  if (oldVal === undefined || newVal === undefined) {
    return;
  }
  if (currentTaskChange) {
    return;
  }
  currentTask.value.description = oldTask.description
  currentTask.value.taskType = taskType.URGENCY // 操作类型：紧急状态
  handleUpdateTask();
})

// 更新任务排序
function handleUpdateTaskSortNum() {
  if (currentTask.value.sortNum === '') {
    proxy.$message.error('任务排序不能为空')
    return;
  }
  currentTask.value.description = oldTask.description
  currentTask.value.taskType = taskType.ORDER
  handleUpdateTask();
}

// 更新任务描述
const disabledTaskDescription = ref(true)

function confirmUpdateTaskDescription() {
  currentTask.value.taskType = taskType.DESCRIPTION
  handleUpdateTask()
}

function cancelUpdateTaskDescription() {
  disabledTaskDescription.value = true
  getTaskByTaskId()
}

watch(() => currentTask.value.description, (newVal, oldVal) => {
  if (oldVal === undefined || newVal === undefined) {
    return
  }
  if (isExecuteGetTaskByDescription.value) {
    isExecuteGetTaskByDescription.value = false
    currentTask.value.description = oldTask.description
    return
  }
  disabledTaskDescription.value = false
})
// 更新任务日期
const taskDateRange = ref([])

function handleUpdateTaskDate() {
  // 获取当前项目的开始时间和结束时间
  const projectBeginTime = new Date(currentProject.value.beginTime);
  const projectEndTime = new Date(currentProject.value.endTime);
  // 获取任务的开始时间和结束时间
  if (taskDateRange.value !== null) {
    const taskBeginTime = new Date(taskDateRange.value[0]);
    const taskEndTime = new Date(taskDateRange.value[1]);
    // 检查任务时间是否在项目时间范围内
    if (taskBeginTime < projectBeginTime || taskEndTime > projectEndTime) {
      proxy.$message.error('任务时间不在项目时间范围内');
      getTaskByTaskId()
      return;
    }
    currentTask.value.beginTime = taskDateRange.value[0]
    currentTask.value.endTime = taskDateRange.value[1]
  } else {
    currentTask.value.beginTime = null
    currentTask.value.endTime = null
  }
  currentTask.value.taskType = taskType.TIME
  handleUpdateTask()
}

// 项目用户相关==========================================
const userList = ref([]) // 项目用户列表
// 获取项目用户列表
function getUserListByProjectId() {
  getUserList({projectId: currentProjectId.value}).then(res => {
    userList.value = res.data
  })
}

// 计算用户岗位
function computedUserPost(item) {
  if (item.role === 1) {
    return item.nickName + '(项目经理)'
  } else {
    return item.nickName + '(项目成员)'
  }
}

// 更新项目指派人员
function handleUpdateProjectUser() {
  currentTask.value.description = oldTask.description
  currentTask.value.taskType = taskType.ASSIGN
  handleUpdateTask()
}

// 标签
import {addTaskTag, deleteTaskTag} from "@/api/project/taskTag.js";

const tagStyleList = [
  {name: '开发', type: 'primary', bgColor: 'background-color:#409EFF'},
  {name: '成功', type: 'success', bgColor: 'background-color:#67C23A'},
  {name: '警告', type: 'warning', bgColor: 'background-color:#E6A23C'},
  {name: '错误', type: 'danger', bgColor: 'background-color:#F56C6C'},
  {name: '信息', type: 'info', bgColor: 'background-color:#909399'},
];

const inputVisible = ref(false)
const inputValue = ref('')
const InputRef = ref(null);

const tagColorValue = ref('')


const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    InputRef.value.input.focus();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value && tagColorValue.value) {
    const obj = {
      pjTaskId: currentTask.value.id,
      name: inputValue.value,
      type: tagColorValue.value
    }
    addTaskTag(obj).then(res => {
      proxy.$message.success('添加成功')
      getTaskByTaskId()
    })
    // console.log(inputValue.value)
    // currentTask.tagList.value.push(inputValue.value);
  }
  tagColorValue.value = ''
  inputVisible.value = false;
  inputValue.value = '';
};

function handleCloseTag(tag) {
  deleteTaskTag(tag.id).then(res => {
    proxy.$message.success('删除成功')
    getTaskByTaskId()
  })
  tagColorValue.value = ''
  inputVisible.value = false;
  inputValue.value = '';
}

// 子任务相关================================
import {addChildTask, getTaskComment, getTaskLog, updateTaskLike} from "@/api/project/task";

const childTaskName = ref('')
const showAddChildTask = ref(true) // 是否显示添加子任务弹窗
function handleAddChildTask() {
  showAddChildTask.value = false
}

function confirmAddChildTask() {
  if (childTaskName.value === '') {
    proxy.$message.error('子任务名称不能为空')
    return;
  }
  const obj = {
    parentId: currentTask.value.id,
    taskName: childTaskName.value,
  }
  addChildTask(obj).then(res => {
    proxy.$message.success('添加成功')
    cancelAddChildTask()
    getTaskByTaskId()
  })
}

// 取消添加子任务
function cancelAddChildTask() {
  childTaskName.value = ''
  showAddChildTask.value = true
}

// 更新子任务状态
function updateChildTaskStatus(task) {
  task.status = task.status === 2 ? 0 : 2
  task.taskType = taskType.CHILD_TASK
  updateTask(task).then(res => {
    proxy.$message.success('更新成功')
    getTaskByTaskId()
  })
}

// 显示子任务
function toChildTask(task) {
  getTaskStageByTaskId(task.id).then(res => {
    currentTaskStageName.value = res.msg
  })
  // currentTaskStageName.value = currentTaskStageName.value + '->' + currentTask.value.name
  currentTask.value = task
}

// 任务日志相关
import {addTaskComment, removeTaskComment} from "@/api/project/taskComment.js";

const disabledTaskComment = ref(true)
const taskCommentValue = ref('')
watch(() => taskCommentValue.value, (newVal, oldVal) => {
  if (oldVal === undefined || newVal === undefined) {
    return;
  }
  if (newVal === '') {
    disabledTaskComment.value = true
    return;
  }
  disabledTaskComment.value = false
})

// 添加评论
function handleAddTaskComment() {
  if (taskCommentValue.value === '') {
    proxy.$message.error('评论内容不能为空')
    return;
  }
  const obj = {
    pjTaskId: currentTask.value.id,
    content: taskCommentValue.value
  }
  addTaskComment(obj).then(res => {
    proxy.$message.success('添加评论成功')
    taskCommentValue.value = ''
    getTaskByTaskId()
  })
}

// 获取任务日志
function handleGetTaskLog() {
  getTaskLog({taskId: currentTask.value.id}).then(res => {
    currentTask.value.logAndCommentList = res.data
  })
}

// 获取任务评论
function handleGetTaskComment() {
  getTaskComment({taskId: currentTask.value.id}).then(res => {
    currentTask.value.logAndCommentList = res.data
  })
}

// 删除任务相关
const showContextMenu = ref(false)
const dropdownTop = ref(0)
const dropdownLeft = ref(0)
const commentId = ref(null)

function handleShowContextMenu(event, comment) {
  event.preventDefault();
  dropdownTop.value = event.clientY - 70;
  dropdownLeft.value = event.clientX - 305;
  // 显示下拉菜单
  showContextMenu.value = true;
  commentId.value = comment.pjTaskCommentId
}

watch(() => showContextMenu.value, (newVal, oldVal) => {
  if (newVal) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

// 关闭下拉菜单
function closeMenu() {
  commentId.value = null
  showContextMenu.value = false;
}

// 删除评论
function handleRemoveTaskComment() {
  removeTaskComment(commentId.value).then(res => {
    proxy.$message.success('删除成功')
    getTaskByTaskId()
  })
}

// 任务弹窗关闭
watch(() => showTaskDialog.value, (newVal, oldVal) => {
  if (oldVal === undefined) {
    return;
  }
  if (!newVal) {
    currentTask.value = {}
    getTaskList()
  }
})

// 获取任务列表==================
function getTaskList() {
  listTask({projectId: currentProjectId.value}).then(res => {
    taskList.value = res.data
  })
}

function getFilePath(name) {
  return import.meta.env.VITE_APP_BASE_API + name
}

// 项目日志
import {getProjectLogListByProjectId} from "@/api/project/projectLog.js";

const projectLogObj = ref({
  projectId: null,
  pageSize: 5,
  pageNum: 1
})
const projectType = {
  TASK_STAGE: 3,
  TASK: 4,
  PROJECT_NAME: 5,
  USER_SE: 6
}
const projectLogList = ref({})
const projectLogTotal = ref(0)

function getProjectLogList() {
  projectLogObj.value.projectId = currentProjectId.value
  getProjectLogListByProjectId(projectLogObj.value).then(res => {
    projectLogList.value = res.data.rows
    projectLogTotal.value = res.data.total
  })
}

function loadMoreProjectLog() {
  projectLogObj.value.pageNum = projectLogObj.value.pageNum + 1
  getProjectLogListByProjectId(projectLogObj.value).then(res => {
    console.log(res.data.rows)
    projectLogList.value = projectLogList.value.concat(res.data.rows)
  })
}

// 项目收藏
import {queryNowUser, updateProjectUser} from "@/api/project/user";

const currentProjectUser = ref({})

function getNowUser() {
  queryNowUser(currentProjectId.value).then(res => {
    currentProjectUser.value = res.data
  })
}

import {updateCollection} from "@/api/project/project.js";

function handleUpdateProjectUserCollection() {
  currentProjectUser.value.isCollection = currentProjectUser.value.isCollection === 1 ? 0 : 1
  updateCollection(currentProjectId.value,).then(res => {
    proxy.$message.success('操作成功')
    getNowUser()
  })
}

import {getTaskStageByTaskId} from "@/api/project/task.js";

// 初始方法
function init() {
  // 获取路由参数：项目id
  currentProjectId.value = router.currentRoute.value.query.projectId
  currentTaskId.value = router.currentRoute.value.query.taskId
  if (currentTaskId.value !== undefined) {
    currentTask.value.id = currentTaskId.value
    getTaskStageByTaskId(currentTask.value.id).then(res => {
      currentTaskStageName.value = res.msg
    })
    setTimeout(() => {
      handleGetTask({id: currentTaskId.value})
    }, 1000)
  }
  // 设置token
  setProjectToken(currentProjectId.value)
  // 获取项目名称
  // 获取任务列表
  getTaskList()
  // 获取当前项目
  getCurrentProject()
  // 获取项目日志
  getProjectLogList()
  // 获取当前用户
  getNowUser()
}

router.beforeEach((to, from, next) => {
  removeProjectToken() // 删除token
  next() // 继续执行
})
init()
import {getDateDiff, formatDateToEnglish} from "@/utils/ruoyi";
import FileUpload from "@/components/FileUpload/index.vue";
import {removeTaskFile} from "@/api/project/taskFile.js";

// 上传文件成功后的回调
function handleUploadTaskFile(item) {
  if (item) {
    getTaskByTaskId()
  }
}

// 复制任务文件路径
function handleCopyTaskFilePath(item) {
  var content = item.filePath
  // 创建一个临时的 textarea 元素
  var textarea = document.createElement('textarea');
  // 将内容设置为要复制的文本
  textarea.value = content;
  // 将 textarea 元素添加到 DOM 中
  document.body.appendChild(textarea);
  // 选中 textarea 中的内容
  textarea.select();
  // 执行复制命令
  document.execCommand('copy');
  // 移除 textarea 元素
  document.body.removeChild(textarea);

  // 提示用户已经复制内容
  proxy.$message.success('复制成功')
}

function handleCancelTaskFile(item) {
  removeTaskFile(item.id).then(res => {
    proxy.$message.success('移除成功')
    getTaskByTaskId()
  })
}

// 计算百分比
function computedFinishCount(item) {
  var finishCount = 0
  item.taskList.forEach(s => {
        if (s.status === 2) {
          finishCount = finishCount + 1;
        }
      }
  )
  return finishCount
}

const removeTaskId = ref(null)
const showRemoveTask = ref(false)

// 删除任务
function handleRemoveTask(id) {
  showRemoveTask.value = true
  removeTaskId.value = id
}

function confirmRemoveTask() {
  removeTask(removeTaskId.value).then(res => {
    proxy.$message.success('删除成功')
    showRemoveTask.value = false
    showTaskDialog.value = false
    getTaskList()
  })
}

function cancelRemoveTask() {
  showRemoveTask.value = false
  removeTaskId.value = null
}

function downloadFile(item) {
  window.open(getFilePath(item.filePath))
}

// 计算日期差
function computedDateDiff(date) {
  date = parseTime(date, "{yyyy}-{mm}-{dd} {hh}:{ii}:{ss}")
  let diff = getDateDiff(date)
  if (diff === '') {
    return formatDateToEnglish(date)
  } else {
    return diff
  }
}

function handleUpdateTaskLike() {
  updateTaskLike(currentTask.value.id).then(res => {
    getTaskByTaskId()
  })
}

// 回收站
import {
  getRecycleTask,
  recoverTask,
  removeRecycleTask,
  getRecycleFile,
  recoverFile,
  removeRecycleFile
} from "@/api/project/task";

const showRecycleBin = ref(false)
const recycleBinActiveName = ref('task')
const recycleTaskList = ref([])
const showRealDeleteTask = ref(false)

function handleShowRecycleBin() {
  showRecycleBin.value = true
  getRecycleTask(currentProjectId.value).then(res => {
    recycleTaskList.value = res.data
  })
}

function handleRecoverTask(item) {
  recoverTask(item.id).then(res => {
    proxy.$message.success('恢复成功')
    handleShowRecycleBin()
    getTaskList()
  })
}

const realDeleteTaskId = ref(null)

function handleRealDeleteTask(item) {
  showRealDeleteTask.value = true
  realDeleteTaskId.value = item.id
}

function confirmRealDeleteTask() {
  removeRecycleTask(realDeleteTaskId.value).then(res => {
    proxy.$message.success('删除成功')
    showRealDeleteTask.value = false
    handleShowRecycleBin()
  })
}

function cancelRealDeleteTask() {
  showRealDeleteTask.value = false
  realDeleteTaskId.value = null
}

const recycleFileList = ref([])

function handleGetRecycleFile() {
  getRecycleFile(currentProjectId.value).then(res => {
    recycleFileList.value = res.data
  })
}

function handleRecoverFile(item) {
  recoverFile(item.id).then(res => {
    proxy.$message.success('恢复成功')
    handleGetRecycleFile()
  })
}

const showRealDeleteFile = ref(false)
const realDeleteFileId = ref(null)

function handleRealDeleteFile(item) {
  showRealDeleteFile.value = true
  realDeleteFileId.value = item.id
}

function confirmRealDeleteFile() {
  removeRecycleFile(realDeleteFileId.value).then(res => {
    proxy.$message.success('删除成功')
    showRealDeleteFile.value = false
    handleGetRecycleFile()
  })
}

function cancelRealDeleteFile() {
  showRealDeleteFile.value = false
  realDeleteFileId.value = null
}

import {getTaskFileList} from "@/api/project/taskFile.js";

const showFileDrawer = ref(false)
const fileList = ref([])

function handleShowFileDrawer() {
  showFileDrawer.value = true
  getTaskFileList(currentProjectId.value).then(res => {
    fileList.value = res.data
  })
}

watch(() => recycleBinActiveName.value, (newValue, oldValue) => {
  if (newValue === 'task') {
    recycleTaskList.value = []
    handleShowRecycleBin()
  } else if (newValue === 'file') {
    recycleFileList.value = []
    handleGetRecycleFile()
  }
})

import {renameFile, deleteFile} from "@/api/project/taskFile.js";
import {getReportByProjectId} from "@/api/project/report.js";
import ImagePreview from "@/components/ImagePreview/index.vue";
import {ElNotification} from "element-plus";

const showRenameFileDiaLog = ref(false)
const renameFileForm = ref({
  projectId: null,
  fileId: null,
  fileName: ''
})

function handleRenameFile(item) {
  // 获取文件名
  let fileName = item.fileName;

  // 使用 lastIndexOf() 方法找到最后一个点的索引
  let lastDotIndex = fileName.lastIndexOf('.');

  // 如果找到了点
  if (lastDotIndex !== -1) {
    // 使用 substring() 方法提取最后一个点之前的内容
    let extractedContent = fileName.substring(0, lastDotIndex);
    // 设置 renameFileForm.value.fileName 的值为提取的内容
    renameFileForm.value.fileName = extractedContent;
  } else {
    // 如果没有找到点，则直接使用原始文件名
    renameFileForm.value.fileName = fileName;
  }

  renameFileForm.value.projectId = currentProjectId.value
  renameFileForm.value.fileId = item.id
  showRenameFileDiaLog.value = true
}

function confirmRenameFile() {
  renameFile(renameFileForm.value).then(res => {
    proxy.$message.success('修改成功')
    cancelRenameFile()
    handleShowFileDrawer()
  })
}

function cancelRenameFile() {
  showRenameFileDiaLog.value = false
  renameFileForm.value.projectId = null
  renameFileForm.value.fileId = null
  renameFileForm.value.fileName = ''
}

const showDeleteFileDiaLog = ref(false)
const deleteFileForm = ref({
  fileId: null,
  projectId: null
})

function handleDeleteFile(item) {
  showDeleteFileDiaLog.value = true
  deleteFileForm.value.fileId = item.id
  deleteFileForm.value.projectId = currentProjectId.value
}

function confirmDeleteFile() {
  deleteFile(deleteFileForm.value).then(res => {
    proxy.$message.success('删除成功')
    cancelDeleteFile()
    handleShowFileDrawer()
  })
}

function cancelDeleteFile() {
  showDeleteFileDiaLog.value = false
  deleteFileForm.value.fileId = null
  deleteFileForm.value.projectId = null
}

// 日志相关
import {list as listPjUser} from "@/api/project/user.js";
const pjUserOptions = ref([])

function handleGetPjUserOptions() {
  listPjUser({projectId: currentProjectId.value}).then(res => {
    pjUserOptions.value = res.data
    console.log(pjUserOptions.value)
  })
}

const showReport = ref(false)
const reportList = ref([])

const shortcuts = [

  {
    text: '昨天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 1) // 获取昨天的日期
      start.setHours(0, 0, 0, 0) // 设置为昨天的开始时间
      end.setDate(end.getDate() - 1) // 获取昨天的日期
      end.setHours(23, 59, 59, 999) // 设置为昨天的结束时间
      return [start, end]
    },
  },
  {
    text: '近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      // 如果开始时间小于 2024 年 3 月 1 日，则设置开始时间为 2024 年 3 月 1 日
      if (start < new Date(2024, 2, 1)) {
        start.setTime(new Date(2024, 2, 1).getTime());
      }
      return [start, end]
    },
  },
  {
    text: '近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      // 如果开始时间小于 2024 年 3 月 1 日，则设置开始时间为 2024 年 3 月 1 日
      if (start < new Date(2024, 2, 1)) {
        start.setTime(new Date(2024, 2, 1).getTime());
      }
      return [start, end]
    },
  },
  {
    text: '全部',
    value: () => {
      const end = new Date()
      const start = new Date(2024, 2, 1) // 月份是从0开始计数的，所以3月对应的是2
      return [start, end]
    },
  },
]
const timeRange = ref([])
const reportQueryForm = ref({
  startTime: null,
  endTime: null,
  userId: null,
  projectId: currentProjectId.value
})

function handleTimeRange(val) {
  if (val === null) {
    reportQueryForm.value.startTime = null
    reportQueryForm.value.endTime = null
  }else {
    if (val[0] < new Date(2024, 2, 1) || val[1] < new Date(2024, 2, 1) ) {
      ElNotification({
        title: '提示',
        message: '选择时间范围不能小于2024年3月1日',
        type: 'warning',
      })
      return;
    }
    reportQueryForm.value.startTime = val[0]
    reportQueryForm.value.endTime = val[1]
  }
  handleGetReportInTask()
}

function handleGetReportInTask() {
  showReport.value = true
  getReportByProjectId(reportQueryForm.value).then(res => {
    reportList.value = res.data
  })
  handleGetPjUserOptions()
}
</script>

<template>
  <div class='app-task'>
    <main class='project'>
      <div class='project-info'>
        <h1 style="font-weight: bold">{{ currentProject.name }}</h1>
        <div class='project-participants'>
          <el-tooltip
              effect="dark"
              content="添加任务列表"
              placement="top"
          >
            <el-button @click="handleAddTaskStage" v-hasPermi="['project:taskStages:add']"
                       class='project-participants__add'></el-button>
          </el-tooltip>
          <el-tooltip
              effect="dark"
              content="取消收藏"
              placement="top"
              v-if="currentProjectUser.isCollection === 1"
          >
            <el-button @click="handleUpdateProjectUserCollection" class='project-participants_item'>
              <template #icon>
                <svg t="1708702066060" class="icon" viewBox="0 0 1025 1024" version="1.1"
                     xmlns="http://www.w3.org/2000/svg" p-id="15447" width="16" height="16">
                  <path
                      d="M512 0c-27.733333 0-53.333333 14.933333-66.133333 42.666667l-96 206.933333c-12.8 25.6-36.266667 44.8-64 49.066667L64 330.666667c-59.733333 10.666667-85.333333 83.2-42.666667 125.866666l166.4 170.666667c19.2 19.2 27.733333 46.933333 23.466667 72.533333L170.666667 936.533333c-6.4 49.066667 29.866667 87.466667 72.533333 87.466667 12.8 0 23.466667-2.133333 36.266667-8.533333l196.266666-108.8c10.666667-6.4 23.466667-8.533333 36.266667-8.533334 12.8 0 25.6 2.133333 36.266667 8.533334l196.266666 108.8c10.666667 6.4 23.466667 8.533333 36.266667 8.533333 42.666667 0 81.066667-38.4 72.533333-87.466667l-38.4-236.8c-4.266667-25.6 4.266667-53.333333 23.466667-72.533333l166.4-170.666667c42.666667-42.666667 17.066667-115.2-42.666667-125.866666L740.266667 298.666667c-27.733333-4.266667-53.333333-23.466667-64-49.066667L578.133333 42.666667C565.333333 14.933333 539.733333 0 512 0z"
                      fill="#d81e06" p-id="15448"></path>
                </svg>
                <!--              <svg t="1708701945136" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10005" width="16" height="16"><path d="M563.83 796.296l150.362 79.05a32 32 0 0 0 20.3 3.215c17.419-2.987 29.118-19.53 26.13-36.949l-28.716-167.43a112 112 0 0 1 32.21-99.134l121.645-118.574a32 32 0 0 0 9.331-18.313c2.542-17.49-9.576-33.728-27.066-36.27l-168.108-24.427a112 112 0 0 1-84.33-61.269l-75.18-152.332a32 32 0 0 0-14.533-14.534c-15.849-7.821-35.037-1.314-42.858 14.534l-75.18 152.332a112 112 0 0 1-84.33 61.269L155.4 401.892a32 32 0 0 0-18.314 9.33c-12.336 12.656-12.077 32.916 0.579 45.252l121.644 118.574a112 112 0 0 1 32.211 99.135l-28.716 167.43a32 32 0 0 0 3.215 20.3c8.224 15.643 27.572 21.657 43.215 13.433l150.361-79.05a112 112 0 0 1 104.237 0z m-67.008 70.81l-150.362 79.05c-54.75 28.785-122.468 7.735-151.252-47.016a112 112 0 0 1-11.254-71.051l28.717-167.43a32 32 0 0 0-9.204-28.324L81.823 513.76c-44.294-43.176-45.2-114.085-2.025-158.38a112 112 0 0 1 64.097-32.658l168.108-24.428a32 32 0 0 0 24.094-17.505l75.181-152.332c27.375-55.469 94.533-78.243 150.002-50.867a112 112 0 0 1 50.867 50.867l75.18 152.332a32 32 0 0 0 24.094 17.505l168.11 24.428c61.212 8.895 103.625 65.728 94.73 126.941a112 112 0 0 1-32.659 64.097L819.957 632.335a32 32 0 0 0-9.203 28.324l28.717 167.43c10.456 60.965-30.49 118.864-91.455 129.32a112 112 0 0 1-71.052-11.253l-150.36-79.05a32 32 0 0 0-29.782 0z" fill="#333333" p-id="10006"></path></svg>-->
              </template>
            </el-button>
          </el-tooltip>
          <el-tooltip
              effect="dark"
              content="收藏项目"
              placement="top"
              v-else
          >
            <el-button @click="handleUpdateProjectUserCollection" class='project-participants_item'>
              <template #icon>
                <svg t="1708701945136" class="icon" viewBox="0 0 1024 1024" version="1.1"
                     xmlns="http://www.w3.org/2000/svg" p-id="10005" width="16" height="16">
                  <path
                      d="M563.83 796.296l150.362 79.05a32 32 0 0 0 20.3 3.215c17.419-2.987 29.118-19.53 26.13-36.949l-28.716-167.43a112 112 0 0 1 32.21-99.134l121.645-118.574a32 32 0 0 0 9.331-18.313c2.542-17.49-9.576-33.728-27.066-36.27l-168.108-24.427a112 112 0 0 1-84.33-61.269l-75.18-152.332a32 32 0 0 0-14.533-14.534c-15.849-7.821-35.037-1.314-42.858 14.534l-75.18 152.332a112 112 0 0 1-84.33 61.269L155.4 401.892a32 32 0 0 0-18.314 9.33c-12.336 12.656-12.077 32.916 0.579 45.252l121.644 118.574a112 112 0 0 1 32.211 99.135l-28.716 167.43a32 32 0 0 0 3.215 20.3c8.224 15.643 27.572 21.657 43.215 13.433l150.361-79.05a112 112 0 0 1 104.237 0z m-67.008 70.81l-150.362 79.05c-54.75 28.785-122.468 7.735-151.252-47.016a112 112 0 0 1-11.254-71.051l28.717-167.43a32 32 0 0 0-9.204-28.324L81.823 513.76c-44.294-43.176-45.2-114.085-2.025-158.38a112 112 0 0 1 64.097-32.658l168.108-24.428a32 32 0 0 0 24.094-17.505l75.181-152.332c27.375-55.469 94.533-78.243 150.002-50.867a112 112 0 0 1 50.867 50.867l75.18 152.332a32 32 0 0 0 24.094 17.505l168.11 24.428c61.212 8.895 103.625 65.728 94.73 126.941a112 112 0 0 1-32.659 64.097L819.957 632.335a32 32 0 0 0-9.203 28.324l28.717 167.43c10.456 60.965-30.49 118.864-91.455 129.32a112 112 0 0 1-71.052-11.253l-150.36-79.05a32 32 0 0 0-29.782 0z"
                      fill="#333333" p-id="10006"></path>
                </svg>
              </template>
            </el-button>
          </el-tooltip>
          <!--          <el-tooltip-->
          <!--              effect="dark"-->
          <!--              content="回收站"-->
          <!--              placement="top"-->
          <!--          >-->
          <!--            <el-button @click="handleShowRecycleBin" class='project-participants_item'>-->
          <!--              <el-icon>-->
          <!--                <svg t="1708755525942" class="icon" viewBox="0 0 1024 1024" version="1.1"-->
          <!--                     xmlns="http://www.w3.org/2000/svg" p-id="3285" width="16" height="16">-->
          <!--                  <path-->
          <!--                      d="M379.904 621.568l73.216-1.024c3.584 0 6.656-2.048 8.704-5.12l50.688-87.552 15.872 27.136c2.048 3.072 5.12 5.12 8.704 5.12s7.168-2.048 8.704-5.12l35.328-64c1.536-3.072 1.536-7.168 0-10.24l-32.768-56.832c-3.584-6.144-8.704-11.776-15.36-15.36-19.456-10.752-44.032-4.608-55.808 14.336 0 0.512-0.512 0.512-0.512 1.024L371.2 606.72c-2.048 3.072-2.048 7.168 0 10.24s5.12 4.608 8.704 4.608z m184.32-4.096l50.688 87.552H583.68c-3.584 0-7.168 2.048-8.704 5.12-2.048 3.072-1.536 7.168 0 10.24l37.888 62.464c2.048 3.072 5.12 5.12 8.704 5.12h65.536c7.168 0 14.336-2.048 20.48-5.632 9.728-5.632 16.384-14.336 19.456-25.088 3.072-10.752 1.536-22.016-4.096-31.744L617.472 543.232c-2.048-3.072-5.12-5.12-8.704-5.12s-7.168 2.048-8.704 5.12l-35.328 64c-2.56 3.584-2.56 7.168-0.512 10.24z"-->
          <!--                      fill="#1296db" p-id="3286"></path>-->
          <!--                  <path-->
          <!--                      d="M948.736 251.904h-194.56C742.4 135.68 647.168 43.008 529.92 34.816c-67.072-5.12-133.632 18.432-182.784 64-43.52 40.448-70.656 94.72-76.288 153.088H75.264c-20.48 1.536-36.864 17.92-38.4 38.4-1.536 23.04 15.872 42.496 38.4 44.544h63.488l57.344 551.424c6.144 58.88 52.224 102.912 108.032 102.912h417.28c55.296 0 101.888-44.544 108.032-102.912l56.832-551.424h63.488c21.504-1.024 38.4-17.92 39.424-39.424 0.512-22.528-16.896-42.496-40.448-43.52zM512.512 116.736c0.512 0 0.512 0 0 0 79.872 0 145.92 57.344 157.696 135.168H354.304c12.288-77.312 78.848-135.168 158.208-135.168zM802.304 335.36l-56.32 542.208c-1.024 13.824-11.776 24.064-25.6 24.576H303.616c-13.312-0.512-24.576-10.752-25.6-24.576v-0.512l-56.32-541.696h580.608z"-->
          <!--                      fill="#1296db" p-id="3287"></path>-->
          <!--                  <path-->
          <!--                      d="M337.92 788.48h210.944c3.584 0 7.168-2.048 8.704-5.12 2.048-3.072 1.536-7.168 0-10.24l-37.376-62.464c-2.048-3.072-5.12-5.12-8.704-5.12H410.112l15.872-27.136c2.048-3.072 2.048-7.168 0-10.24s-5.632-5.12-9.216-5.12l-73.216 1.024c-3.584 0-6.656 2.048-8.704 5.12l-32.768 56.832c-3.584 6.656-5.632 13.824-5.632 20.992 0 11.264 4.608 21.504 12.288 29.184s17.92 12.288 29.184 12.288z"-->
          <!--                      fill="#1296db" p-id="3288"></path>-->
          <!--                </svg>-->
          <!--              </el-icon>-->
          <!--            </el-button>-->
          <!--          </el-tooltip>-->
          <el-tooltip
              effect="dark"
              content="文件列表"
              placement="top"
          >
            <el-button @click="handleShowFileDrawer" class='project-participants_item'>
              <el-icon>
                <svg t="1708781699878" class="icon" viewBox="0 0 1024 1024" version="1.1"
                     xmlns="http://www.w3.org/2000/svg" p-id="8050" width="16" height="16">
                  <path
                      d="M707.047619 73.142857v243.809524h195.047619v560.761905a73.142857 73.142857 0 0 1-73.142857 73.142857H195.047619a73.142857 73.142857 0 0 1-73.142857-73.142857V146.285714a73.142857 73.142857 0 0 1 73.142857-73.142857h512z m24.380952 633.904762H292.571429a24.380952 24.380952 0 0 0 0 48.761905h438.857142a24.380952 24.380952 0 0 0 0-48.761905z m0-121.904762H292.571429a24.380952 24.380952 0 0 0 0 48.761905h438.857142a24.380952 24.380952 0 0 0 0-48.761905z m0-121.904762H292.571429a24.380952 24.380952 0 0 0 0 48.761905h438.857142a24.380952 24.380952 0 0 0 0-48.761905z m24.380953-390.095238l146.285714 195.047619h-146.285714V73.142857z"
                      fill="#4BB4FF" p-id="8051"></path>
                </svg>
              </el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip
              effect="dark"
              content="团队成员日志"
              placement="top"
          >
            <el-button @click="handleGetReportInTask" class='project-participants_item'>
              <el-icon>
                <svg t="1709376370796" class="icon" viewBox="0 0 1024 1024" version="1.1"
                     xmlns="http://www.w3.org/2000/svg" p-id="4091" width="16" height="16">
                  <path
                      d="M269.844659 81.4308h44.821057v166.626082h-44.821057zM677.140966 491.719232c52.335426 0 102.092273 19.937769 140.105639 56.13883 38.126482 36.31053 60.461599 85.284073 62.891788 137.900467 2.5056 54.276658-16.27424 106.280032-52.881549 146.431672-36.60731 40.15164-86.65972 63.643469-140.936379 66.150285-3.180653 0.147174-6.401444 0.221369-9.576016 0.221369-52.341508 0-102.102004-19.936552-140.114153-56.136398-38.126482-36.309314-60.461599-85.284073-62.891789-137.902899-2.5056-54.276658 16.27424-106.280032 52.88155-146.431672 36.60731-40.15164 86.65972-63.643469 140.936379-66.149069a208.122961 208.122961 0 0 1 9.576016-0.221369h0.008514m-0.00973-44.822274c-3.859355 0-7.746684 0.088791-11.642528 0.268805-136.951744 6.3236-242.847422 122.470346-236.525038 259.422091 6.143586 133.0559 115.942406 236.793842 247.779562 236.793842 3.859355 0 7.747901-0.088791 11.642529-0.268804 136.951744-6.322384 242.847422-122.470346 236.525037-259.422091-6.143586-133.057117-115.942406-236.798708-247.779562-236.793843z"
                      fill="#4E4D4D" p-id="4092"></path>
                  <path
                      d="M490.264524 891.110734a272.361206 272.361206 0 0 1-32.682275-37.369937H180.453104c-20.912034 0-37.927007-17.013757-37.927007-37.92579v-590.263526c0-20.912034 17.013757-37.927007 37.927007-37.927007H732.799354c20.912034 0 37.925791 17.013757 37.925791 37.927007V441.15597a268.605238 268.605238 0 0 1 44.821057 21.463023V225.551481c0-45.70045-37.047614-82.746848-82.746848-82.746849H180.453104c-45.70045 0-82.746848 37.047614-82.746848 82.746849v590.263526c0 45.70045 37.047614 82.746848 82.746848 82.746848h317.980164a273.587248 273.587248 0 0 1-8.168744-7.451121z"
                      fill="#4E4D4D" p-id="4093"></path>
                  <path
                      d="M770.725145 489.61623a225.243754 225.243754 0 0 1 44.821057 27.231985v-0.21407a225.182938 225.182938 0 0 0-44.821057-27.114003v0.096088zM812.590566 778.530212H646.820768V576.105667h44.821057v157.604704h120.948741zM209.55091 380.121489h498.255687v44.821057H209.55091zM600.682445 81.4308h44.821058v166.626082h-44.821058zM406.842623 712.17437H209.55091v44.821057h203.864657a272.351476 272.351476 0 0 1-6.572944-44.821057zM450.941192 546.147929H209.55091v44.821057h217.435038a268.707408 268.707408 0 0 1 23.955244-44.821057z"
                      fill="#4E4D4D" p-id="4094"></path>
                </svg>
              </el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>
      <div class='project-tasks'>
        <div class='project-column' v-for="item in taskList" :key="item.taskStageId">
          <div class='project-column-heading'>
            <h2 class='project-column-heading__title'>{{ item.taskStageName }}</h2>
            <el-dropdown placement="bottom-start" trigger="click">
              <el-button link :icon="More"></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleUpdateTaskStage(item)">
                    <el-icon>
                      <RefreshRight/>
                    </el-icon>
                    修改列表
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleDeleteTaskStage(item)">
                    <el-icon>
                      <Delete/>
                    </el-icon>
                    删除列表
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <!--            <button class=''><i class="fas fa-ellipsis-h"></i></button>-->
          </div>
          <div class='task' @click="handleGetTask(task)" v-for="task in item.taskList">
            <div class='task__tags'><span>
              <el-tag v-if="task.status === 0">未开始</el-tag>
              <el-tag v-else-if="task.status === 1" type="success">进行中</el-tag>
              <el-tag v-else-if="task.status === 2" type="danger">已完成</el-tag>
            </span>
              <el-dropdown trigger="click">
                <button class='task__options' @click.stop><i class="fas fa-ellipsis-h"></i></button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleRemoveTask(task.id)">❌ 删除任务</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <p>{{ task.name }}</p>
            <div class='task__stats'>
              <span v-if="task.realEndTime !== null"><i
                  class="fas fa-flag"></i>{{ parseTime(task.realEndTime, "{y}-{m}-{d}") }}</span>
              <span v-else-if="task.endTime !== null"><i
                  class="fas fa-flag"></i>{{ parseTime(task.endTime, "{y}-{m}-{d}") }}</span>
              <span v-else-if="task.updateTime !== null"><i
                  class="fas fa-flag"></i>{{ parseTime(task.updateTime, "{y}-{m}-{d}") }}</span>
              <span v-else><i class="fas fa-flag"></i>{{ parseTime(task.createTime, "{y}-{m}-{d}") }}</span>
              <span><i class="fas fa-comment"></i>{{ task.logAndCommentSize }}</span>
            </div>
          </div>
          <el-button v-if="!item.showAddTask" @click="handleAddTask(item)"
                     style="width: 100%; height: 50px; background-color: #4caf50; color: #fff; border: none; border-radius: 8px; font-size: 1.2rem;">
            <template #icon>
              <i class="fas fa-plus"></i>
            </template>
            新增任务
          </el-button>
          <div v-else class="task">
            <el-form :model="addTaskForm" :rules="addTaskRules" ref="addTaskRef">
              <el-form-item label="任务名称" prop="name">
                <el-input v-model="addTaskForm.name" placeholder="请输入任务名称"></el-input>
              </el-form-item>
              <el-form-item label="排序" prop="sortNum">
                <el-input-number v-model="addTaskForm.sortNum" :min="1" placeholder="请输入排序数字"></el-input-number>
              </el-form-item>
            </el-form>
            <div style="margin-top: 5px">
              <el-button @click="confirmAddTask" type="success" style="width: 45%">确认</el-button>
              <el-button @click="cancelAddTask" type="info" style="width: 45%">取消</el-button>
            </div>
          </div>
        </div>
      </div>
    </main>
    <aside class='task-details'>
      <div class='task-progress'>
        <h2>任务进程</h2>
        <div class="task-progress-tag">
          <div class='tag-progress' v-for="item in taskList">
            <p>{{ item.taskStageName }}<span> {{ computedFinishCount(item) }} / {{ item.taskList.length }} </span></p>
            <progress class="progress progress--copyright" :max="item.taskList.length"
                      :value="computedFinishCount(item)"></progress>
          </div>
        </div>
      </div>
      <div class='task-activity'>
        <h2>最近活动</h2>
        <div class="task-activity-li">
          <ul>
            <li v-for="item in projectLogList">
              <span class='task-icon '>
                <svg v-if="item.type === projectType.TASK_STAGE" t="1708692754518" class="icon" viewBox="0 0 1024 1024"
                     version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="50742" width="16" height="16"><path
                    d="M829.5 902.07h-635c-41.35 0-75-33.64-75-75v-635c0-41.36 33.65-75 75-75h635c41.35 0 75 33.64 75 75v635c0 41.35-33.65 75-75 75z m-635-735c-13.79 0-25 11.21-25 25v635c0 13.79 11.21 25 25 25h635c13.79 0 25-11.21 25-25v-635c0-13.79-11.21-25-25-25h-635z"
                    fill="#2C5CCB" p-id="50743"></path><path
                    d="M756.07 393.78H512c-13.81 0-25-11.19-25-25s11.19-25 25-25h244.07c13.81 0 25 11.19 25 25s-11.19 25-25 25zM756.07 677.22H512c-13.81 0-25-11.19-25-25s11.19-25 25-25h244.07c13.81 0 25 11.19 25 25s-11.19 25-25 25zM304.37 422.65a25 25 0 0 1-17.68-7.32l-43.4-43.4c-9.76-9.76-9.76-25.59 0-35.36 9.76-9.76 25.59-9.76 35.36 0l25.72 25.72 88.08-88.08c9.76-9.76 25.59-9.76 35.36 0 9.76 9.76 9.76 25.59 0 35.36L322.04 415.33a25.001 25.001 0 0 1-17.67 7.32zM335.55 741.2c-49.06 0-88.98-39.92-88.98-88.98s39.92-88.98 88.98-88.98 88.98 39.92 88.98 88.98c-0.01 49.06-39.92 88.98-88.98 88.98z m0-127.96c-21.49 0-38.98 17.49-38.98 38.98s17.49 38.98 38.98 38.98 38.98-17.49 38.98-38.98c-0.01-21.49-17.49-38.98-38.98-38.98z"
                    fill="#2C5CCB" p-id="50744"></path></svg>
                <svg v-else-if="item.type === projectType.TASK" t="1708693246707" class="icon" viewBox="0 0 1024 1024"
                     version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="54544" width="16" height="16"><path
                    d="M627.6036377 373.57226562h-230.71289063c-51.00402833 0-92.28515625-41.36352539-92.28515625-92.28515624v-69.21386719c0-12.77160645 10.29968262-23.07128906 23.07128907-23.07128906h46.14257812c0-25.46081543 20.6817627-46.14257813 46.14257813-46.14257813h184.57031249c25.46081543 0 46.14257813 20.6817627 46.14257813 46.14257813h46.14257813c12.77160645 0 23.07128906 10.29968262 23.07128906 23.07128906v69.21386719c0 50.92163086-41.28112793 92.28515625-92.28515626 92.28515625z m-115.35644532-184.57031249c-38.23242188 0-69.21386719 30.98144531-69.21386719 69.21386718s30.98144531 69.21386719 69.21386719 69.21386719 69.21386719-30.98144531 69.21386719-69.21386719-30.98144531-69.21386719-69.21386719-69.21386718z"
                    fill="#EB3E35" p-id="54545"></path><path
                    d="M789.10266114 881.22302247H235.30932617c-51.00402833 0-92.28515625-41.36352539-92.28515625-92.28515626V327.4296875c0-51.00402833 41.36352539-92.28515625 92.28515625-92.28515625v69.21386719c0.08239747 50.92163086 41.36352539 92.20275879 92.28515625 92.28515625h369.22302247c51.00402833 0 92.28515625-41.36352539 92.28515624-92.28515625v-69.21386719c51.00402833 0 92.28515625 41.36352539 92.28515625 92.28515625v461.50817872c0.08239747 50.92163086-41.28112793 92.28515625-92.28515625 92.28515624z"
                    fill="#6D6AF7" p-id="54546"></path><path
                    d="M494.76657104 734.14355469c-6.7565918 0-13.51318359-2.55432129-18.62182617-7.74536133-10.29968262-10.29968262-10.29968262-27.02636719 0-37.32604981l195.77636719-195.77636718c10.29968262-10.29968262 27.02636719-10.29968262 37.32604981-1e-8s10.29968262 27.02636719 0 37.32604981l-195.77636719 195.77636719c-5.19104004 5.19104004-11.94763184 7.74536133-18.70422364 7.74536133z"
                    fill="#F0F0F4" p-id="54547"></path><path
                    d="M494.76657104 734.14355469c-6.7565918 0-13.51318359-2.55432129-18.62182617-7.74536133l-97.88818359-97.88818359c-10.29968262-10.29968262-10.29968262-27.02636719 0-37.32604981s27.02636719-10.29968262 37.32604981 0l97.80578613 97.97058106c10.29968262 10.29968262 10.29968262 27.02636719 0 37.3260498-5.10864258 5.10864258-11.86523438 7.66296387-18.62182618 7.66296387z"
                    fill="#F0F0F4" p-id="54548"></path></svg>
                <svg v-else-if="item.type === projectType.PROJECT_NAME" t="1708693316879" class="icon"
                     viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="57394" width="16"
                     height="16"><path d="M0 0m0 0l1024 0q0 0 0 0l0 1024q0 0 0 0l-1024 0q0 0 0 0l0-1024q0 0 0 0Z"
                                       fill="#E8EFF8" p-id="57395"></path><path
                    d="M139.636364 651.636364a23.272727 23.272727 0 0 1 23.272727-23.272728h698.181818a23.272727 23.272727 0 0 1 23.272727 23.272728v116.363636a23.272727 23.272727 0 0 1-23.272727 23.272727H162.909091a23.272727 23.272727 0 0 1-23.272727-23.272727v-116.363636z"
                    fill="#247ADE" p-id="57396"></path><path
                    d="M139.636364 442.181818a23.272727 23.272727 0 0 1 23.272727-23.272727h395.636364a23.272727 23.272727 0 0 1 23.272727 23.272727v116.363637a23.272727 23.272727 0 0 1-23.272727 23.272727H162.909091a23.272727 23.272727 0 0 1-23.272727-23.272727v-116.363637zM581.818182 232.727273a23.272727 23.272727 0 0 0-23.272727-23.272728H162.909091a23.272727 23.272727 0 0 0-23.272727 23.272728v116.363636a23.272727 23.272727 0 0 0 23.272727 23.272727h395.636364a23.272727 23.272727 0 0 0 23.272727-23.272727v-116.363636z"
                    fill="#A0BFF7" p-id="57397"></path><path
                    d="M768 488.727273a116.363636 116.363636 0 1 0 0-232.727273 116.363636 116.363636 0 0 0 0 232.727273z"
                    fill="#69CB91" p-id="57398"></path></svg>
                <svg v-else-if="item.type === projectType.USER_SE" t="1708693348406" class="icon"
                     viewBox="0 0 1095 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="58400" width="16"
                     height="16"><path
                    d="M901.061633 693.651803a217.585111 217.585111 0 0 0 88.542846-175.100426 212.026367 212.026367 0 1 0-423.65568 0 215.996898 215.996898 0 0 0 88.939899 175.100426 357.347809 357.347809 0 0 0-194.953083 293.025204c0 20.249709 10.323381 36.131834 29.778984 36.131834h574.93292c19.455603 0 29.778984-15.882125 29.778984-36.131834a359.730128 359.730128 0 0 0-193.761923-293.025204z"
                    fill="#4E8CEE" p-id="58401"></path><path
                    d="M556.022471 674.990306a299.775107 299.775107 0 0 1-63.131447-227.114385A284.290035 284.290035 0 0 1 603.668845 262.05506a287.46646 287.46646 0 0 1 261.658007-34.543621c77.425359 29.381931 79.410624 60.749128 88.542846 60.749127a35.337728 35.337728 0 0 0 34.940675-35.734781V192.173711A185.026755 185.026755 0 0 0 812.121734 0h-635.284994A185.026755 185.026755 0 0 0 0.148101 192.173711v624.167507a185.026755 185.026755 0 0 0 176.688639 192.17371H317.790598a31.76425 31.76425 0 0 0 35.734781-30.970143 210.438154 210.438154 0 0 1 21.043815-79.410624 490.757658 490.757658 0 0 1 75.837146-123.483521A500.286933 500.286933 0 0 1 556.022471 674.990306zM247.512195 154.453664h256.89337a25.014347 25.014347 0 0 1 24.22024 22.234975 28.190772 28.190772 0 0 1-23.823187 32.955409H249.497461a24.617294 24.617294 0 0 1-27.396665-25.808453 26.205506 26.205506 0 0 1 25.411399-27.793718z m-96.880961-6.749903a34.940675 34.940675 0 0 1 30.57309 17.073284 34.543622 34.543622 0 0 1 0 34.940675 34.940675 34.940675 0 0 1-30.57309 17.073284 34.543622 34.543622 0 0 1 0-69.087243z m201.305932 503.066305H140.307853a30.970143 30.970143 0 1 1 0-61.543234h211.629313a30.970143 30.970143 0 1 1 0 61.543234zM423.009675 476.463746H140.307853c-19.852656 0-35.734781-10.720434-34.940675-30.970144a31.367197 31.367197 0 0 1 34.940675-30.970143h282.701822a31.76425 31.76425 0 0 1 34.940675 30.970143 30.970143 30.970143 0 0 1-34.940675 30.970144z"
                    fill="#A6C5F6" p-id="58402"></path></svg>
                <svg v-else t="1708693385823" class="icon" viewBox="0 0 1024 1024" version="1.1"
                     xmlns="http://www.w3.org/2000/svg" p-id="60433" width="16" height="16"><path
                    d="M512 64a448 448 0 1 0 0 896A448 448 0 0 0 512 64z m-32 232c0-4.416 3.584-8 8-8h48c4.416 0 8 3.584 8 8v272A8 8 0 0 1 536 576h-48a8 8 0 0 1-8-8v-272zM512 736A48 48 0 1 1 512 640a48 48 0 0 1 0 96z"
                    fill="#FF4D4F" p-id="60434"></path></svg>
              </span>
              {{ item.content }}
              <time>{{ computedDateDiff(item.createTime) }}</time>
            </li>
            <li v-if="projectLogTotal > projectLogList.length" @click="loadMoreProjectLog"
                style="display: flex; align-items: center;margin-left: 30px;cursor: pointer">
              <svg t="1708690445110" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="49745" width="16" height="16">
                <path
                    d="M264.52 494.448c0 42.952-34.808 77.752-77.784 77.752-42.952 0-77.76-34.808-77.76-77.752 0-42.984 34.808-77.784 77.76-77.784a77.76 77.76 0 0 1 77.784 77.784zM583.616 494.448c0 42.952-34.808 77.752-77.8 77.752-42.952 0-77.76-34.808-77.76-77.752 0-42.984 34.808-77.784 77.76-77.784a77.76 77.76 0 0 1 77.8 77.784zM902.704 494.448c0 42.952-34.808 77.752-77.792 77.752-42.976 0-77.784-34.808-77.784-77.752a77.76 77.76 0 0 1 77.784-77.784 77.76 77.76 0 0 1 77.792 77.784z"
                    fill="#888888" p-id="49746"></path>
              </svg>
              <span style="margin-left: 10px">加载更多</span>
            </li>
            <li v-else style="display: flex; align-items: center;margin-left: 30px">
              <svg t="1708694284457" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="65351" width="16" height="16">
                <path
                    d="M512 1014.153846C234.673231 1014.153846 9.846154 789.326769 9.846154 512S234.673231 9.846154 512 9.846154 1014.153846 234.673231 1014.153846 512 789.326769 1014.153846 512 1014.153846z m0-59.076923c244.696615 0 443.076923-198.380308 443.076923-443.076923S756.696615 68.923077 512 68.923077 68.923077 267.303385 68.923077 512 267.303385 955.076923 512 955.076923zM275.692308 571.076923a59.076923 59.076923 0 1 1 0-118.153846 59.076923 59.076923 0 0 1 0 118.153846z m236.307692 0a59.076923 59.076923 0 1 1 0-118.153846 59.076923 59.076923 0 0 1 0 118.153846z m236.307692 0a59.076923 59.076923 0 1 1 0-118.153846 59.076923 59.076923 0 0 1 0 118.153846z"
                    fill="#333333" p-id="65352"></path>
              </svg>
              <span style="margin-left: 10px">暂无更多内容</span>
            </li>
          </ul>
        </div>

      </div>
    </aside>
    <!--    新增任务列表-->
    <el-dialog v-model="showAddTaskStageDialog" :close-on-click-modal="false" :close-on-press-escape="false"
               title="添加项目列表" width="450">
      <el-form :model="addTaskStageForm" :rules="addTaskStageRules" ref="addTaskStageRef">
        <el-form-item label="列表名称" prop="taskStageName">
          <el-input v-model="addTaskStageForm.taskStageName" placeholder="请输入列表名称"></el-input>
        </el-form-item>
        <el-form-item label="排序顺序" prop="sortNum">
          <el-input-number v-model="addTaskStageForm.sortNum" :min="1" placeholder="输入排序"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" size="large" @click="confirmAddTaskStage">确认</el-button>
        <el-button type="info" size="large" @click="cancelAddTaskStage">取消</el-button>
      </template>
    </el-dialog>
    <!--    更新任务列表-->
    <el-dialog v-model="showUpdateTaskStageDialog" :close-on-click-modal="false" :close-on-press-escape="false"
               title="修改项目列表" width="450">
      <el-form :model="updateTaskStageForm" :rules="updateTaskStageRules" ref="updateTaskStageRef">
        <el-form-item label="列表名称" prop="taskStageName">
          <el-input v-model="updateTaskStageForm.taskStageName" placeholder="请输入列表名称"></el-input>
        </el-form-item>
        <el-form-item label="排序顺序" prop="sortNum">
          <el-input-number v-model="updateTaskStageForm.sortNum" :min="1" placeholder="输入排序"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" size="large" @click="confirmUpdateTaskStage">确认</el-button>
        <el-button type="info" size="large" @click="cancelUpdateTaskStage">取消</el-button>
      </template>
    </el-dialog>
    <!--    删除任务列表-->
    <el-dialog v-model="showDeleteTaskStageDialog" :close-on-click-modal="false" :close-on-press-escape="false"
               title="删除项目列表" width="450">
      <template #default>
        <el-form :model="deleteTaskStageForm" :rules="deleteTaskStageRules" ref="deleteTaskStageRef">
        </el-form>
        <div style="color: red;font-weight: bold;font-size: 16px">
          是否确定删除项目列表《{{ deleteTaskStageForm.taskStageName }}》?
        </div>
      </template>
      <template #footer>
        <el-button type="primary" size="large" @click="confirmDeleteTaskStage">确认</el-button>
        <el-button type="info" size="large" @click="cancelDeleteTaskStage">取消</el-button>
      </template>
    </el-dialog>
    <!--    任务展示 -->
    <el-dialog v-model="showTaskDialog" :close-on-click-modal="false" :close-on-press-escape="false" width="1300"
               id="taskShow" :show-close="false">
      <template #header="{ close, titleId, titleClass }">
        <div class="my-header">
          <h4 :id="titleId" :class="titleClass">{{ currentTaskStageName }}</h4>
          <div class="buttons-container">
            <el-tooltip
                class="box-item"
                effect="dark"
                content="点个赞"
                placement="top-start"
                v-if="currentTask.isLike === 0"
            >
              <el-button @click="handleUpdateTaskLike" type="primary" link>
                <el-icon>
                  <svg t="1708751771005" class="icon" viewBox="0 0 1024 1024" version="1.1"
                       xmlns="http://www.w3.org/2000/svg" p-id="5398" width="16" height="16">
                    <path
                        d="M889.6 396.8 608 396.8C633.6 262.4 684.8 96 576 44.8 460.8-6.4 454.4 64 441.6 179.2c-12.8 121.6-192 249.6-192 249.6l0 563.2 608 0c83.2-38.4 134.4-377.6 160-499.2C1043.2 371.2 889.6 396.8 889.6 396.8z"
                        p-id="5399" fill="#e6e6e6"></path>
                    <path d="M0 428.8l179.2 0 0 569.6-179.2 0 0-569.6Z" p-id="5400" fill="#e6e6e6"></path>
                  </svg>
                </el-icon>
                <span style="margin-left: 4px">
                {{ currentTask.likeNum }}
              </span>
              </el-button>
            </el-tooltip>
            <el-tooltip
                class="box-item"
                effect="dark"
                content="取消点赞"
                placement="top-start"
                v-else
            >
              <el-button @click="handleUpdateTaskLike" type="primary" link>
                <el-icon>
                  <svg t="1708751771005" class="icon" viewBox="0 0 1024 1024" version="1.1"
                       xmlns="http://www.w3.org/2000/svg" p-id="5398" width="16" height="16">
                    <path
                        d="M889.6 396.8 608 396.8C633.6 262.4 684.8 96 576 44.8 460.8-6.4 454.4 64 441.6 179.2c-12.8 121.6-192 249.6-192 249.6l0 563.2 608 0c83.2-38.4 134.4-377.6 160-499.2C1043.2 371.2 889.6 396.8 889.6 396.8z"
                        p-id="5399" fill="#1296db"></path>
                    <path d="M0 428.8l179.2 0 0 569.6-179.2 0 0-569.6Z" p-id="5400" fill="#1296db"></path>
                  </svg>
                </el-icon>
                <span style="margin-left: 4px">
                {{ currentTask.likeNum }}
              </span>
              </el-button>
            </el-tooltip>
            <el-dropdown trigger="click">
              <el-button type="primary">
                <el-icon>
                  <svg t="1708749787726" class="icon" viewBox="0 0 1024 1024" version="1.1"
                       xmlns="http://www.w3.org/2000/svg" p-id="2626" width="16" height="16">
                    <path
                        d="M17.655172 70.62069m70.62069 0l282.482759 0q70.62069 0 70.620689 70.620689l0 282.482759q0 70.62069-70.620689 70.62069l-282.482759 0q-70.62069 0-70.62069-70.62069l0-282.482759q0-70.62069 70.62069-70.620689Z"
                        fill="#e6e6e6" p-id="2627"></path>
                    <path
                        d="M17.655172 600.09931m70.62069 0l282.482759 0q70.62069 0 70.620689 70.62069l0 282.482759q0 70.62069-70.620689 70.620689l-282.482759 0q-70.62069 0-70.62069-70.620689l0-282.482759q0-70.62069 70.62069-70.62069Z"
                        fill="#e6e6e6" p-id="2628"></path>
                    <path
                        d="M547.310345 600.09931m70.620689 0l282.482759 0q70.62069 0 70.62069 70.62069l0 282.482759q0 70.62069-70.62069 70.620689l-282.482759 0q-70.62069 0-70.620689-70.620689l0-282.482759q0-70.62069 70.620689-70.62069Z"
                        fill="#e6e6e6" p-id="2629"></path>
                    <path
                        d="M751.333517-0.006165m49.936369 49.936369l174.77729 174.77729q49.936369 49.936369 0 99.872737l-174.77729 174.77729q-49.936369 49.936369-99.872737 0l-174.77729-174.77729q-49.936369-49.936369 0-99.872737l174.77729-174.77729q49.936369-49.936369 99.872737 0Z"
                        fill="#e6e6e6" p-id="2630"></path>
                  </svg>
                </el-icon>
                更多
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleRemoveTask(currentTask.id)">❌删除任务</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" @click="showTaskDialog = false">
              <el-icon>
                <CircleCloseFilled/>
              </el-icon>
              关闭
            </el-button>

          </div>
        </div>

      </template>
      <div style="width: 100%; height: 680px; display: flex;">
        <div style="width: 70%; overflow-y: auto;max-height: 680px; background-color: #fff;">
          <!-- 左侧内容 -->
          <el-input v-model="currentTask.name" @blur="handleUpdateTaskName"
                    style="height: 50px; margin-bottom: 10px;width: 99%"
                    placeholder="输入项目名称">
            <template #prefix>
              <svg t="1708494215342" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="25761" width="16" height="16">
                <path
                    d="M193.378462 759.729231c-48.600615-79.084308-30.326154-226.067692-13.154462-279.315693l48.128-11.382153A198.301538 198.301538 0 0 1 271.36 325.316923c66.599385-84.676923 256.078769-253.715692 620.189538-273.723077-132.529231 148.676923-227.682462 401.959385-251.31323 438.941539a815.104 815.104 0 0 1-97.988923 56.477538l44.032 42.968615s-244.224 210.313846-392.900923 169.747693"
                    fill="#49D2FF" p-id="25762"></path>
                <path
                    d="M206.217846 742.518154c33.043692 6.695385 73.964308 0.787692 120.950154-16.344616 40.448-14.769231 83.810462-37.257846 128.433231-65.378461a1128.684308 1128.684308 0 0 0 101.179077-72.073846l-28.317539-27.608616a19.692308 19.692308 0 0 1 5.12-31.783384c31.507692-15.36 62.030769-32.846769 91.254154-52.263385 10.870154-23.748923 89.009231-190.582154 133.080615-267.618461 29.144615-50.963692 58.683077-96.098462 88.851693-134.774154-267.579077 24.654769-457.373538 132.411077-560.128 263.010461a178.609231 178.609231 0 0 0-38.675693 129.417846 19.692308 19.692308 0 0 1-15.084307 21.110154l-37.021539 8.743385c-20.283077 72.940308-20.873846 186.052923 10.358154 245.563077z m369.427692-190.424616l24.379077 23.788308a19.692308 19.692308 0 0 1-0.905846 29.026462c-1.732923 1.457231-4.883692 4.135385-9.412923 7.837538a1167.596308 1167.596308 0 0 1-113.073231 81.368616c-46.828308 29.499077-92.553846 53.208615-135.955692 69.04123-57.698462 21.070769-109.095385 27.411692-152.536615 15.517539a19.692308 19.692308 0 0 1-11.579077-8.664616c-44.110769-71.798154-42.417231-210.865231-15.044923-295.620923a19.692308 19.692308 0 0 1 14.178461-13.115077l32.059077-7.60123a217.796923 217.796923 0 0 1 48.088616-140.524308C371.042462 166.715077 587.067077 48.600615 890.486154 31.901538a19.692308 19.692308 0 0 1 15.753846 32.807385c-38.675692 43.401846-76.603077 98.697846-114.097231 164.233846-46.395077 81.132308-134.616615 271.005538-135.325538 272.147693l-5.592616 5.710769c-24.379077 16.423385-49.624615 31.507692-75.539692 45.292307z"
                    fill="#505050" p-id="25763"></path>
                <path
                    d="M596.795077 246.153846l23.630769 31.547077A1376.689231 1376.689231 0 0 0 147.219692 921.757538l-37.139692-13.075692A1416.073846 1416.073846 0 0 1 596.795077 246.153846zM193.142154 953.344l-21.110154-33.240615c25.009231-15.911385 73.649231-38.872615 108.307692-48.167385 26.742154-7.168 47.497846-7.798154 62.660923 2.048 20.716308 13.390769 21.819077 38.360615 8.073847 70.498462-5.198769 12.209231-7.325538 8.192-2.363077 8.152615 14.060308-0.078769 39.896615-8.073846 73.412923-22.646154 50.609231-21.976615 115.672615-57.501538 158.404923-83.574154a688.324923 688.324923 0 0 1 144.856615-66.402461c57.186462-17.880615 118.941538-16.699077 161.437539 4.52923 51.081846 25.560615 68.923077 76.130462 43.598769 138.909539l-36.548923-14.729846c17.841231-44.110769 7.719385-72.743385-24.654769-88.930462-32.531692-16.265846-83.928615-17.250462-131.859693-2.284307a648.467692 648.467692 0 0 0-136.310154 62.542769c-44.150154 26.939077-110.670769 63.251692-163.24923 86.094769-38.203077 16.580923-67.859692 25.757538-88.851693 25.875692-35.643077 0.196923-49.703385-26.584615-34.067692-63.054769 7.089231-16.620308 6.892308-21.819077 6.695385-21.937231-2.875077-1.890462-14.375385-1.536-31.035077 2.953846-30.404923 8.152615-75.500308 29.459692-97.398154 43.323077z"
                    fill="#505050" p-id="25764"></path>
              </svg>
            </template>
          </el-input>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494309753" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="27291" width="16" height="16">
              <path
                  d="M933.034667 77.653333h-89.6v115.541334c0 58.368-45.568 105.642667-101.546667 105.642666-56.149333 0-101.546667-47.274667-101.546667-105.642666V77.653333H381.098667v115.541334c0 58.368-45.568 105.642667-101.546667 105.642666-56.149333 0-101.546667-47.274667-101.546667-105.642666V77.653333h-87.04C40.789333 77.653333 0 118.954667 0 169.984v761.685333C0 982.698667 40.789333 1024 90.965333 1024h841.898667c50.346667 0 90.965333-41.301333 90.965333-92.330667V169.984c0.170667-51.029333-40.618667-92.330667-90.794666-92.330667zM742.058667 460.8L490.666667 731.306667c-8.192 8.874667-19.456 13.824-31.232 13.824-11.434667 0-22.528-4.778667-30.549334-13.312l-149.162666-158.208c-12.970667-11.264-18.432-29.354667-13.994667-46.421334 4.437333-16.896 17.92-29.696 34.474667-32.597333 16.554667-2.901333 33.450667 4.437333 43.008 18.773333l115.541333 125.098667 222.037333-240.469333c17.066667-13.653333 41.301333-12.117333 56.661334 3.584 15.530667 16.042667 17.408 41.130667 4.608 59.221333z"
                  fill="#2B3F64" p-id="27292"></path>
              <path
                  d="M741.717333 0c25.258667 0 45.909333 21.162667 45.909334 47.445333v142.848c0 26.282667-20.48 47.445333-45.909334 47.445334s-45.909333-21.162667-45.909333-47.445334V47.445333c0-26.282667 20.48-47.445333 45.909333-47.445333zM279.552 0c25.429333 0 45.909333 21.162667 45.909333 47.445333v142.848c0 26.282667-20.48 47.445333-45.909333 47.445334-25.258667 0-45.909333-21.162667-45.909333-47.445334V47.445333c0-26.282667 20.48-47.445333 45.909333-47.445333z"
                  fill="#2B3F64" p-id="27293"></path>
            </svg>
            <span style="margin-left: 5px;">任务状态</span>
            <div style="margin-left: 20px;margin-top: 3px">
              <el-dropdown trigger="click">
                <el-tag v-if="currentTask.status === 0" type="info">未开始</el-tag>
                <el-tag v-else-if="currentTask.status === 1" type="danger">正在进行</el-tag>
                <el-tag v-else-if="currentTask.status === 2" type="success">已完成</el-tag>
                <el-tag v-else type="danger">请重新刷新页面</el-tag>
                <template #dropdown>
                  <el-dropdown-menu @click="handleUpdateTaskByStatus">
                    <el-dropdown-item @click="currentTask.status = 0">未开始</el-dropdown-item>
                    <el-dropdown-item @click="currentTask.status = 1" style="">正在进行</el-dropdown-item>
                    <el-dropdown-item @click="currentTask.status = 2">已完成</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494135801" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="24693" width="16" height="16">
              <path d="M0 0h1024v1024H0z" fill="#FFFFFF" p-id="24694"></path>
              <path
                  d="M707.413333 279.125333a277.333333 277.333333 0 0 0-421.12 237.226667v268.8H785.066667v-315.733333c0-73.813333-29.866667-141.226667-77.653334-190.293334z m-232.106666 403.626667l24.32-149.333333-72.96-21.333334 97.706666-170.666666v128l72.96 21.333333-122.026666 192z"
                  fill="#CCE1FE" p-id="24695"></path>
              <path
                  d="M475.306667 682.666667l24.32-149.333334L426.666667 512l97.706666-170.666667v128l72.96 21.333334-122.026666 192z"
                  fill="#3974C7" p-id="24696"></path>
              <path
                  d="M905.386667 787.2H853.333333V469.333333c0-176.64-134.826667-322.133333-306.773333-339.626666V110.933333h59.306667c18.773333 0 34.133333-15.36 34.133333-34.133333 0-18.773333-15.36-34.133333-34.133333-34.133333H418.133333c-18.773333 0-34.133333 15.36-34.133333 34.133333 0 18.773333 15.36 34.133333 34.133333 34.133333h60.16v18.773334C305.92 146.773333 170.666667 292.266667 170.666667 469.333333v317.866667H118.613333c-18.346667 0-33.28 14.506667-33.28 32.853333s14.933333 33.28 33.28 33.28h786.773334c18.346667 0 33.28-14.933333 33.28-33.28 0-18.346667-14.933333-32.853333-33.28-32.853333zM238.933333 469.333333c0-150.613333 122.453333-273.066667 273.066667-273.066666 76.373333 0 145.92 31.573333 195.413333 82.773333A272.682667 272.682667 0 0 1 785.066667 469.333333v315.733334H238.933333V469.333333zM512 981.333333c46.933333 0 85.333333-38.4 85.333333-85.333333h-170.666666c0 46.933333 38.4 85.333333 85.333333 85.333333z"
                  fill="#303133" p-id="24697"></path>
            </svg>
            <span style="margin-left: 5px;">紧急程度</span>
            <div style="margin-left: 20px;">
              <el-dropdown trigger="click">
                <el-tag v-if="currentTask.urgency === 0" type="info">一般</el-tag>
                <el-tag v-else-if="currentTask.urgency === 1" type="warning">紧急</el-tag>
                <el-tag v-else-if="currentTask.urgency === 2" type="danger">非常紧急</el-tag>
                <el-tag v-else type="danger">请重新刷新页面</el-tag>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="currentTask.urgency = 0">一般</el-dropdown-item>
                    <el-dropdown-item @click="currentTask.urgency = 1">紧急</el-dropdown-item>
                    <el-dropdown-item @click="currentTask.urgency = 2">非常紧急</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708521861918" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="9716" width="16" height="16">
              <path
                  d="M771.8912 108.1344H255.1296c-47.2576 0-85.5552 38.2976-85.5552 85.5552v156.4672h687.872V193.6896c0-47.2576-38.2976-85.5552-85.5552-85.5552z"
                  fill="#ffa115" p-id="9717"></path>
              <path
                  d="M233.0624 970.1888c-15.8208 0-31.6416-4.096-46.1824-12.2368-30.0544-16.896-48.0256-47.5648-48.0256-82.0736V197.4272c0-73.4208 59.6992-133.12 133.12-133.12h485.4272c73.4208 0 133.12 59.6992 133.12 133.12v672.768c0 34.7648-19.0464 66.6112-49.7152 82.9952a93.96736 93.96736 0 0 1-96.6656-4.6592l-197.1712-131.4816a32.75264 32.75264 0 0 0-35.1232-0.7168l-229.888 139.9296c-15.2064 9.2672-32.0512 13.9264-48.896 13.9264z m38.912-844.4416c-39.5264 0-71.68 32.1536-71.68 71.68v678.4c0 17.2544 11.6736 25.7024 16.6912 28.5184 5.0176 2.816 18.2784 8.3968 33.024-0.5632l229.888-139.9296c31.1296-18.944 70.8608-18.1248 101.1712 2.0992L778.24 897.4336c10.1888 6.8096 22.784 7.424 33.5872 1.6384 10.8032-5.7856 17.2544-16.5888 17.2544-28.8256V197.4272c0-39.5264-32.1536-71.68-71.68-71.68H271.9744z"
                  fill="#474A54" p-id="9718"></path>
              <path
                  d="M678.5024 287.0272H350.9248c-16.9472 0-30.72-13.7728-30.72-30.72s13.7728-30.72 30.72-30.72h327.6288c16.9472 0 30.72 13.7728 30.72 30.72s-13.7728 30.72-30.7712 30.72z"
                  fill="#474A54" p-id="9719"></path>
            </svg>
            <span style="margin-left: 5px;">标签</span>
            <div style="margin-left: 20px;margin-top: 5px">
              <el-tag
                  v-for="tag in currentTask.tagList"
                  :key="tag"
                  closable
                  :disable-transitions="false"
                  :type="tag.type"
                  style="margin-right: 5px;"
                  @close="handleCloseTag(tag)"
              >
                {{ tag.name }}
              </el-tag>
              <el-input
                  v-if="inputVisible"
                  ref="InputRef"
                  v-model="inputValue"
                  class="w-20"
                  size="small"
              />
              <el-select v-model="tagColorValue" @change="handleInputConfirm" value-key="type" v-if="inputVisible"
                         style="margin-top: 5px">
                <el-option
                    v-for="item in tagStyleList"
                    :key="item.type"
                    :label="item.name"
                    :value="item.type"
                    :style="item.bgColor"
                />
              </el-select>
              <el-button v-else size="small" @click="showInput" style="margin-left: 5px">
                + New Tag
              </el-button>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494388439" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="29898" width="16" height="16">
              <path
                  d="M358.265 273.813l-111.561 94.165c-19.508 16.466-48.67 14-65.136-5.508-16.466-19.507-14-48.67 5.508-65.135l187.597-158.346c30.052-25.365 76.036-4.003 76.036 35.322v682.667c0 25.528-20.694 46.222-46.222 46.222-25.528 0-46.222-20.694-46.222-46.222V273.813z m410.837 389.409c19.507-16.466 48.67-14 65.135 5.508 16.466 19.507 14 48.67-5.507 65.135L641.132 892.211c-30.05 25.365-76.036 4.003-76.036-35.322V174.222c0-25.528 20.695-46.222 46.223-46.222 25.527 0 46.222 20.694 46.222 46.222v583.165l111.56-94.165z"
                  fill="#1296db" p-id="29899"></path>
            </svg>
            <span style="margin-left: 5px;">排序</span>
            <div style="margin-left: 20px;">
              <el-input-number v-model="currentTask.sortNum" :min="1"
                               @change="handleUpdateTaskSortNum"></el-input-number>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494612416" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="41652" width="16" height="16">
              <path
                  d="M887.466667 120.285867c0-24.9856-23.552-18.7392-30.890667-11.3664-13.994667 15.36-51.063467 46.557867-130.218667 63.010133a159.709867 159.709867 0 0 1-36.488533 3.959467c-38.161067 0-70.178133-12.458667-106.632533-27.2384-42.120533-16.452267-89.838933-35.157333-154.965334-35.157334-5.597867 0-11.195733 0-16.827733 0.546134-70.724267 3.413333-131.925333 34.6112-176.264533 57.890133-13.0048 6.587733-26.112 13.0048-39.253334 19.285333l-3.959466-9.6256-0.546134-0.580266a28.535467 28.535467 0 0 0-37.614933-15.325867 27.989333 27.989333 0 0 0-15.1552 15.906133 30.139733 30.139733 0 0 0 0 22.1184l292.4544 700.3136a28.194133 28.194133 0 0 0 26.385067 17.578667 24.917333 24.917333 0 0 0 11.229866-2.286933 29.0816 29.0816 0 0 0 15.1552-37.9904l-99.9424-239.5136a161.553067 161.553067 0 0 1 96.5632-73.181867 214.801067 214.801067 0 0 1 55.022934-7.406933c37.614933 0 74.069333 8.533333 112.810666 17.6128 35.362133 7.953067 71.304533 16.452267 106.666667 16.452266 53.316267 0 94.3104-18.7392 129.672533-58.4704a28.7744 28.7744 0 0 0 2.798934-9.079466V120.285867z"
                  fill="#85C2FF" p-id="41653"></path>
            </svg>
            <span style="margin-left: 5px;">描述</span>
            <div style="margin-left: 20px;width: 90%;">
              <Editor v-model="currentTask.description"/>
              <div style="float: right;margin-top: 5px">
                <el-button :disabled="disabledTaskDescription" @click="confirmUpdateTaskDescription" type="success">
                  保存
                </el-button>
                <el-button :disabled="disabledTaskDescription" @click="cancelUpdateTaskDescription" type="info">
                  取消
                </el-button>
              </div>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494528945" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="39776" width="16" height="16">
              <path
                  d="M1005.049995 672.258716a66.824088 66.824088 0 0 0-62.432952-38.000218h-154.42163a50.216585 50.216585 0 0 0 5.291883-34.284642 67.837427 67.837427 0 0 0-40.702457-51.398814 73.185606 73.185606 0 0 0-77.182666 12.216366l-12.66674 10.921545c-23.98236 20.66086-46.669898 40.195787-70.201885 61.419612H541.953996a26.065335 26.065335 0 0 0-25.277182 26.740895v248.887358a28.14831 28.14831 0 0 0 8.388196 18.352698 29.668319 29.668319 0 0 0 18.296402 7.037078h53.988458a104.9369 104.9369 0 0 1 11.540808 6.248925 96.436111 96.436111 0 0 0 15.706757 8.050417l10.865247 6.755594a67.555944 67.555944 0 0 0 17.789732 4.053357h145.977137l-0.731856-0.394077a31.751294 31.751294 0 0 0 13.792672-5.235585 57.535146 57.535146 0 0 0 48.696576-63.952961 9.288942 9.288942 0 0 0-1.125932-3.377797 23.475691 23.475691 0 0 1-1.351119-3.490391 52.63734 52.63734 0 0 0 27.585344-32.257963 63.164808 63.164808 0 0 0-2.308161-46.613602 67.555944 67.555944 0 0 0 14.243044-40.871346h41.772093a52.524747 52.524747 0 0 0 21.730495-2.702238 62.601842 62.601842 0 0 0 43.460991-36.592803 58.041816 58.041816 0 0 0 0.056297-51.511408z m-40.871347 28.936463a27.022378 27.022378 0 0 1-16.15713 11.259324h-0.562966a14.186748 14.186748 0 0 1-9.626722 2.364458h-82.981218l-12.891926 22.912725 6.924484 11.259324a19.81641 19.81641 0 0 1 1.238526 2.026678 20.379377 20.379377 0 0 1-8.83857 27.472751l-8.444493 7.037077-2.871127 22.518649 7.205967 7.205967a17.395656 17.395656 0 0 1 3.65928 19.253444 26.065335 26.065335 0 0 1-17.620842 13.680079l-11.259324 4.278543-7.149671 22.856428 7.374858 9.288942a13.286002 13.286002 0 0 1 3.49039 6.755595 17.226766 17.226766 0 0 1-12.891926 18.071215 20.773453 20.773453 0 0 0-3.490391 1.688898h-0.394076v0.394077c-0.844449 1.688899-4.16595 1.688899-6.417815 1.688898h-136.237821L643.287913 906.56525c-5.629662-2.420755-11.82229-5.629662-17.733436-9.120052-3.546687-1.970382-6.980781-3.884467-10.245985-5.629662a94.409432 94.409432 0 0 0-19.084554-4.053357H561.094847v-211.675292h33.777972a31.638701 31.638701 0 0 0 23.757174-8.3319c12.441553-10.808951 24.601623-21.561606 36.761693-32.31426 15.98824-14.130452 32.539447-28.767573 49.484729-43.404695a21.054936 21.054936 0 0 1 26.797191-6.192628c8.275603 5.629662 13.792672 10.245985 13.792672 15.931944a18.859368 18.859368 0 0 1-7.431153 16.888986l-2.364458 2.420755c-18.071215 11.259324-24.545326 23.98236-19.140851 38.000218a30.737955 30.737955 0 0 0 26.515708 15.368978h197.488544c12.891926 0 19.929004 3.99706 23.588284 13.680078a12.32896 12.32896 0 0 1 0.056296 13.060816z"
                  fill="#8BAEF7" p-id="39777"></path>
              <path
                  d="M448.951979 956.444056H103.403324a21.167529 21.167529 0 0 1-15.594164-7.712637 32.14537 32.14537 0 0 1-7.205967-25.558666c29.893505-219.556819 198.952256-386.363705 396.384503-394.076342 4.785213 0 9.626722 0.394076 14.524528 0.394076 145.92084 0 264.594115-133.704473 264.594116-264.594115a264.594115 264.594115 0 1 0-409.895693 220.851641 472.103458 472.103458 0 0 0-172.492844 106.682096C87.47138 675.749107 30.668091 789.749763 13.722808 914.052701a99.645018 99.645018 0 0 0 23.250504 78.815268 88.554584 88.554584 0 0 0 66.486309 30.850548h345.492358a33.777972 33.777972 0 0 0 0-67.555944zM295.994062 264.783779A195.574459 195.574459 0 1 1 491.287038 460.358238 195.799645 195.799645 0 0 1 295.994062 264.783779z"
                  fill="#467CFD" p-id="39778"></path>
            </svg>
            <span style="margin-left: 5px;">指派人</span>
            <div style="margin-left: 20px;">
              <el-select v-model="currentTask.projectUserId" @change="handleUpdateProjectUser" value-key="userId">
                <el-option
                    v-for="item in userList"
                    :key="item.userId"
                    :label="computedUserPost(item) "
                    :value="item.userId"
                >
                    <span style="display: inline-block; vertical-align: middle;">
                        <el-avatar :src="getFilePath(item.avatar)" :size="'small'"></el-avatar>
                      </span>
                  <span style="display: inline-block; vertical-align: middle;margin-bottom: 12px;margin-left: 5px">{{
                      computedUserPost(item)
                    }}</span>
                </el-option>
              </el-select>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px">
            <svg t="1708494693058" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="50650" width="16" height="16">
              <path
                  d="M912.82 342.68A436.48 436.48 0 1 0 947 512a433.41 433.41 0 0 0-34.18-169.32zM752.54 745.77l-3.66 3.89a27.2 27.2 0 0 1-38.28 1.16L560.14 609.21a108.49 108.49 0 1 1-77.89-201.56V253.08A27.11 27.11 0 0 1 509.33 226h5.34a27.11 27.11 0 0 1 27.08 27.08v154.57A108.46 108.46 0 0 1 604.26 569l147.12 138.5a27.11 27.11 0 0 1 1.16 38.27z"
                  fill="#F9DB91" p-id="50651"></path>
              <path
                  d="M541.75 407.65V253.08A27.11 27.11 0 0 0 514.67 226h-5.34a27.11 27.11 0 0 0-27.08 27.08v154.57a109 109 0 0 1 59.5 0zM751.38 707.5L604.26 569a109.27 109.27 0 0 1-44.12 40.18L710.6 750.82a27.2 27.2 0 0 0 38.28-1.16l3.66-3.89a27.11 27.11 0 0 0-1.16-38.27z"
                  fill="#EF6A6A" p-id="50652"></path>
              <path d="M604.26 569a108.48 108.48 0 1 0-44.12 40.18A109.27 109.27 0 0 0 604.26 569z" fill="#AEF0FF"
                    p-id="50653"></path>
              <path
                  d="M512 64C264.58 64 64 264.58 64 512s200.58 448 448 448 448-200.58 448-448S759.42 64 512 64z m298.4 746.4A420.48 420.48 0 0 1 525 933.8V896a13 13 0 0 0-26 0v37.8A421.57 421.57 0 0 1 90.2 525H128a13 13 0 0 0 0-26H90.2A421.57 421.57 0 0 1 499 90.2V128a13 13 0 0 0 26 0V90.2A421.57 421.57 0 0 1 933.8 499H896a13 13 0 0 0 0 26h37.8a420.48 420.48 0 0 1-123.4 285.4z"
                  fill="#512C56" p-id="50654"></path>
              <path
                  d="M760.29 698L620.58 566.54a121.57 121.57 0 0 0-65.83-168.29V253.08A40.2 40.2 0 0 0 514.67 213h-5.34a40.2 40.2 0 0 0-40.08 40.08v145.17a121.52 121.52 0 1 0 88.33 226.4l144.11 135.64a40.2 40.2 0 0 0 56.65-1.72l3.66-3.89a40.19 40.19 0 0 0-1.71-56.68z m-265-444.95A14.27 14.27 0 0 1 509.33 239h5.34a14.27 14.27 0 0 1 14.08 14.08v138.58a122.56 122.56 0 0 0-33.5 0zM416.51 512A95.49 95.49 0 1 1 512 607.49 95.59 95.59 0 0 1 416.51 512z m326.56 224.86l-3.66 3.89a14.21 14.21 0 0 1-19.9 0.61L581.62 611.57a122.35 122.35 0 0 0 24.59-22.85L742.47 717a14.27 14.27 0 0 1 0.6 19.86z"
                  fill="#512C56" p-id="50655"></path>
            </svg>
            <span style="margin-left: 5px;">时间范围</span>
            <div style="margin-left: 20px;">
              <el-date-picker
                  v-model="taskDateRange"
                  @change="handleUpdateTaskDate"
                  type="datetimerange"
                  range-separator="To"
                  start-placeholder="Start date"
                  end-placeholder="End date"
              />
            </div>
          </div>
          <div style="background-color: #fff">注：可选时间范围为：{{ currentProject.beginTime }} -
            {{ currentProject.endTime }}
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px;">
            <svg t="1708525997990" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="23851" width="16" height="16">
              <path
                  d="M624 652.8h280c24 0 43.2-19.2 43.2-41.6V420.8c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6V486.4h-291.2V297.6H400c24 0 43.2-19.2 43.2-41.6V65.6c0-22.4-19.2-41.6-43.2-41.6H120c-24 0-43.2 19.2-43.2 41.6V256c0 22.4 19.2 41.6 43.2 41.6H230.4v595.2h348.8v65.6c0 22.4 19.2 41.6 43.2 41.6H902.4c24 0 43.2-19.2 43.2-41.6V768c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6h-291.2v-288h291.2V611.2c0 22.4 19.2 41.6 43.2 41.6z m12.8-222.4h252.8V601.6H636.8V430.4zM132.8 246.4v-172.8h252.8v171.2h-252.8v1.6z m504 531.2h252.8v171.2H636.8V777.6z"
                  fill="#346FF1" p-id="23852"></path>
            </svg>
            <span style="margin-left: 5px;">子任务</span>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px;width: 99%">
            <div style="border: 1px solid #ccc; border-radius: 5px; padding: 10px;width: 100%">
              <div v-for="(task, index) in currentTask.childTaskList" :key="index" class="task-item">
                <div class="left-box">
                  <!-- 更新子任务 -->
                  <el-button @click="updateChildTaskStatus(task)" type="primary" circle link>
                    <template #icon>
                      <svg v-if="task.status === 2" t="1708527821192" class="icon" viewBox="0 0 1024 1024" version="1.1"
                           xmlns="http://www.w3.org/2000/svg" p-id="31941" width="16" height="16">
                        <path
                            d="M173 137c-19.882 0-36 16.118-36 36v680c0 19.882 16.118 36 36 36h678c19.882 0 36-16.118 36-36V173c0-19.882-16.118-36-36-36H173z m0-72h678c59.647 0 108 48.353 108 108v680c0 59.647-48.353 108-108 108H173c-59.647 0-108-48.353-108-108V173c0-59.647 48.353-108 108-108z m339 524.088l230.544-230.544c14.059-14.059 36.853-14.059 50.912 0 14.059 14.059 14.059 36.853 0 50.912l-256 256c-14.059 14.059-36.853 14.059-50.912 0l-256-256c-14.059-14.059-14.059-36.853 0-50.912 14.059-14.059 36.853-14.059 50.912 0L512 589.088z"
                            fill="#000000" p-id="31942"></path>
                      </svg>
                      <svg v-else t="1708527679068" class="icon" viewBox="0 0 1024 1024" version="1.1"
                           xmlns="http://www.w3.org/2000/svg" p-id="24911" width="32" height="32">
                        <path
                            d="M65.98144 66.09408v891.91936h892.032V66.09408H65.98144z m828.03712 828.65664H130.18112V131.04128h763.83744v763.70944z"
                            p-id="24912"></path>
                      </svg>
                    </template>
                  </el-button>
                </div>
                <div class="task-name-finish" v-if="task.status === 2">{{ task.name }}</div>
                <div class="task-name-not-finish" v-else>{{ task.name }}</div>
                <div class="right-arrow">
                  <!-- 这里可以放置一个按钮箭头 -->
                  <el-button @click="toChildTask(task)" link type="success" :icon="ArrowRight"></el-button>
                </div>
              </div>
              <el-tooltip
                  class="box-item"
                  effect="dark"
                  content="父任务已完成，无法新增子任务"
                  placement="top"
                  v-if="showAddChildTask && currentTask.status === 2"
              >
                <el-button :disabled="currentTask.status === 2" @click="handleAddChildTask"
                           style="width: 100%" type="primary">新增子任务
                  <template #icon>
                    <svg t="1708657269361" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="7497" width="16" height="16">
                      <path
                          d="M624 652.8h280c24 0 43.2-19.2 43.2-41.6V420.8c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6H289.6V297.6H400c24 0 43.2-19.2 43.2-41.6V65.6c0-22.4-19.2-41.6-43.2-41.6H120C96 24 76.8 43.2 76.8 65.6V256c0 22.4 19.2 41.6 43.2 41.6h110.4v595.2h348.8v65.6c0 22.4 19.2 41.6 43.2 41.6h280c24 0 43.2-19.2 43.2-41.6V768c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6H289.6v-288h291.2v65.6c0 22.4 19.2 41.6 43.2 41.6z m12.8-222.4h252.8v171.2H636.8V430.4zM132.8 246.4V73.6h252.8v171.2H132.8z m504 531.2h252.8v171.2H636.8V777.6z"
                          fill="#2C2C2C" p-id="7498"></path>
                    </svg>
                  </template>
                </el-button>
              </el-tooltip>
              <el-button v-else-if="showAddChildTask" @click="handleAddChildTask"
                         style="width: 100%" type="primary">新增子任务
                <template #icon>
                  <svg t="1708657269361" class="icon" viewBox="0 0 1024 1024" version="1.1"
                       xmlns="http://www.w3.org/2000/svg" p-id="7497" width="16" height="16">
                    <path
                        d="M624 652.8h280c24 0 43.2-19.2 43.2-41.6V420.8c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6H289.6V297.6H400c24 0 43.2-19.2 43.2-41.6V65.6c0-22.4-19.2-41.6-43.2-41.6H120C96 24 76.8 43.2 76.8 65.6V256c0 22.4 19.2 41.6 43.2 41.6h110.4v595.2h348.8v65.6c0 22.4 19.2 41.6 43.2 41.6h280c24 0 43.2-19.2 43.2-41.6V768c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6H289.6v-288h291.2v65.6c0 22.4 19.2 41.6 43.2 41.6z m12.8-222.4h252.8v171.2H636.8V430.4zM132.8 246.4V73.6h252.8v171.2H132.8z m504 531.2h252.8v171.2H636.8V777.6z"
                        fill="#2C2C2C" p-id="7498"></path>
                  </svg>
                </template>
              </el-button>
              <div v-else>
                <el-input v-model="childTaskName" placeholder="请输入子任务名称"></el-input>
                <div style="display: flex; justify-content: flex-end;margin-top: 5px">
                  <el-button @click="confirmAddChildTask" type="success">保存</el-button>
                  <el-button @click="cancelAddChildTask" type="info">取消</el-button>
                </div>
              </div>
            </div>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px;">
            <svg t="1708653936999" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="2327" width="16" height="16">
              <path
                  d="M731.648 626.176l67.584-67.584c28.672-31.232 67.584-46.08 107.008-46.08V327.68c0-22.528-9.728-44.544-26.112-59.904L643.072 47.104c-22.528-20.992-52.224-32.768-83.456-32.768H147.456c-45.056 0-81.92 36.864-81.92 81.92v831.488c0 45.056 36.864 81.92 81.92 81.92h346.112c-30.72-58.368-20.48-133.632 30.208-184.832l67.584-67.584"
                  fill="#0067B1" p-id="2328"></path>
              <path
                  d="M530.432 147.968v192c0 19.456 12.8 32.256 32.256 32.256h204.8c0-0.512-237.056-224.256-237.056-224.256zM186.368 377.344h151.04c11.264 0 20.48 9.216 20.48 20.48v55.296c0 11.264-9.216 20.48-20.48 20.48H186.368c-11.264 0-20.48-9.216-20.48-20.48V397.824c0-11.264 9.216-20.48 20.48-20.48zM186.368 574.976h323.584c11.264 0 20.48 9.216 20.48 20.48v55.296c0 11.264-9.216 20.48-20.48 20.48H186.368c-11.264 0-20.48-9.216-20.48-20.48v-55.296c0-11.264 9.216-20.48 20.48-20.48z"
                  fill="#FFFFFF" p-id="2329"></path>
              <path
                  d="M965.632 617.984c-48.128-45.056-124.928-48.128-169.472 0l-51.2 51.2c-12.8 12.8-12.8 38.4 0 51.2s35.328 12.8 51.2 0l51.2-51.2c19.456-19.456 48.128-19.456 67.072 0s19.456 48.128 0 67.072l-51.2 51.2c-12.8 12.8-12.8 38.4 0 51.2s38.4 12.8 51.2 0l51.2-51.2c47.616-48.128 47.616-124.928 0-169.472z"
                  fill="#FF7F00" p-id="2330"></path>
              <path
                  d="M780.8 764.928l-38.4 38.4c-9.728 9.728-9.728 28.672 0 38.4s28.672 9.728 38.4 0l35.328-35.328c9.728-9.728 9.728-28.672 0-38.4-9.728-12.8-26.112-12.8-35.328-3.072z"
                  fill="#FF7F00" p-id="2331"></path>
              <path
                  d="M757.248 886.784l-51.2 51.2c-19.456 19.456-48.128 19.456-67.072 0-18.944-19.456-19.456-48.128 0-67.072l51.2-51.2c12.8-12.8 12.8-38.4 0-51.2s-38.4-12.8-51.2 0l-51.2 51.2c-48.128 48.128-48.128 124.928 0 169.472s124.928 48.128 169.472 0l51.2-51.2c12.8-12.8 12.8-38.4 0-51.2s-34.816-12.8-51.2 0z"
                  fill="#FF7F00" p-id="2332"></path>
            </svg>
            <span style="margin-left: 5px;">关联文件</span>
          </div>
          <div style="display: flex; align-items: center;margin-top: 20px;margin-bottom: 20px;">
            <file-upload @uploadSuccess="handleUploadTaskFile" :limit="1" :is-task-upload="true"
                         :data="{pjTaskId : currentTask.id}"></file-upload>
          </div>
          <div
              style="display: flex; align-items: center; margin-top: 10px; margin-bottom: 20px; border-radius: 5px; overflow: hidden;">
            <div style="width: 99%;">
              <div style="border: 1px solid #ccc; padding: 10px; width: 100%;border-radius: 5px;">
                <div style="font-weight: bold; margin-bottom: 10px;">关联的文件</div>
                <div style="width: 100%">
                  <div v-for="(item, index) in currentTask.fileList" :key="index"
                       style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
                    <div style="display: flex; align-items: center;">
                      <svg v-if="item.fileSuffix === 'doc'" t="1708671882008" class="icon" viewBox="0 0 1024 1024"
                           version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9417" width="16" height="16">
                        <path
                            d="M901.850593 926.476283a48.761858 48.761858 0 0 1-48.761859 48.761859H170.422718a48.761858 48.761858 0 0 1-48.761858-48.761859V48.762834a48.761858 48.761858 0 0 1 48.761858-48.761859h418.864363a48.761858 48.761858 0 0 1 34.620919 14.140939l263.801654 263.801654a48.761858 48.761858 0 0 1 14.140939 34.620919V926.476283z"
                            fill="#EBECF0" p-id="9418"></path>
                        <path
                            d="M901.850593 926.476283v48.761859a48.761858 48.761858 0 0 1-48.761859 48.761858H170.422718a48.761858 48.761858 0 0 1-48.761858-48.761858v-48.761859a48.761858 48.761858 0 0 0 48.761858 48.761859h682.666016a48.761858 48.761858 0 0 0 48.761859-48.761859z"
                            fill="#C1C7D0" p-id="9419"></path>
                        <path
                            d="M24.137143 536.381417h975.237166v243.809291a48.761858 48.761858 0 0 1-48.761858 48.761859H72.899001a48.761858 48.761858 0 0 1-48.761858-48.761859v-243.809291z"
                            fill="#0747A6" p-id="9420"></path>
                        <path
                            d="M121.66086 536.381417V438.8577l-97.523717 97.523717h97.523717zM901.850593 536.381417l0.975237-97.523717 97.036098 97.523717H901.850593z"
                            fill="#063266" p-id="9421"></path>
                        <path
                            d="M236.251227 592.457554H311.832107a71.192313 71.192313 0 0 1 56.563756 24.868548A97.523717 97.523717 0 0 1 389.85108 682.666992a107.763707 107.763707 0 0 1-20.47998 67.778983 69.729457 69.729457 0 0 1-58.51423 26.331403H236.251227zM267.946434 747.032645h41.44758a41.935198 41.935198 0 0 0 35.596157-15.603795 73.142787 73.142787 0 0 0 12.678083-48.761858 68.75422 68.75422 0 0 0-13.653321-45.348528 44.86091 44.86091 0 0 0-36.571393-16.091414H267.946434zM511.755726 585.143275a83.870396 83.870396 0 0 1 65.34089 26.819022 97.523717 97.523717 0 0 1 23.893311 70.704695 103.37514 103.37514 0 0 1-24.380929 70.704694A82.407541 82.407541 0 0 1 511.755726 780.190708a83.870396 83.870396 0 0 1-65.34089-27.30664A100.937047 100.937047 0 0 1 421.546288 682.666992a100.449428 100.449428 0 0 1 24.868548-70.217076A83.382778 83.382778 0 0 1 511.755726 585.143275z m0 163.839844a52.662807 52.662807 0 0 0 42.422817-18.041888 73.142787 73.142787 0 0 0 15.116176-48.761858 70.217076 70.217076 0 0 0-16.091413-48.761858 53.638044 53.638044 0 0 0-41.935199-18.041888 52.662807 52.662807 0 0 0-41.935198 18.041888 71.192313 71.192313 0 0 0-15.603794 48.761858 70.704695 70.704695 0 0 0 16.091413 48.761858 53.150426 53.150426 0 0 0 41.935198 18.529507zM787.260226 701.196498a155.062709 155.062709 0 0 1-28.281878 58.026611 69.241839 69.241839 0 0 1-56.076137 20.967599 74.605643 74.605643 0 0 1-59.977086-25.356166 106.300851 106.300851 0 0 1-22.430455-72.16755 104.837995 104.837995 0 0 1 22.918074-71.679932A78.506592 78.506592 0 0 1 706.803159 585.143275q57.051374 0 76.556118 67.291365h-32.670445a48.761858 48.761858 0 0 0-43.885673-35.596157 48.761858 48.761858 0 0 0-39.009486 17.06665 69.729457 69.729457 0 0 0-14.628558 48.761859q0 66.803746 51.68757 66.803746a40.472342 40.472342 0 0 0 30.719971-11.215228 85.820871 85.820871 0 0 0 17.554269-35.596156z"
                            fill="#FFFFFF" p-id="9422"></path>
                        <path
                            d="M905.263923 312.564487v6.82666h-264.289272a48.761858 48.761858 0 0 1-48.761859-48.761858V0.000975a48.761858 48.761858 0 0 1 34.62092 14.140939l264.289272 263.801654a48.761858 48.761858 0 0 1 14.140939 34.620919z"
                            fill="#C1C7D0" p-id="9423"></path>
                      </svg>
                      <svg v-else t="1708673747146" class="icon" viewBox="0 0 1024 1024" version="1.1"
                           xmlns="http://www.w3.org/2000/svg" p-id="48752" width="16" height="16">
                        <path d="M0 0h1024v1024H0z" fill="#FFFFFF" p-id="48753"></path>
                        <path
                            d="M130.9184 717.42464A87.12192 87.12192 0 0 0 209.92 768h604.16a87.1424 87.1424 0 0 0 87.04-87.04V394.83392z"
                            fill="#E4E7E9" p-id="48754"></path>
                        <path
                            d="M214.26176 388.096a20.54144 20.54144 0 0 0 20.48 20.48h312.32a20.48 20.48 0 0 0 0-40.96h-312.32a20.54144 20.54144 0 0 0-20.48 20.48zM234.74176 488.448a20.48 20.48 0 0 0 0 40.96h166.912a20.48 20.48 0 0 0 0-40.96z"
                            fill="#CCCCCC" p-id="48755"></path>
                        <path
                            d="M814.08 225.28h-97.28a15.36 15.36 0 0 0 0 30.72h97.28a87.1424 87.1424 0 0 1 87.04 87.04v337.92a87.1424 87.1424 0 0 1-87.04 87.04H209.92a87.13216 87.13216 0 0 1-87.04-87.04V343.04a87.1424 87.1424 0 0 1 87.04-87.04h97.28a15.36 15.36 0 0 0 0-30.72h-97.28A117.89312 117.89312 0 0 0 92.16 343.04v337.92a117.76 117.76 0 0 0 117.76 117.76h604.16a117.89312 117.89312 0 0 0 117.76-117.76V343.04a117.89312 117.89312 0 0 0-117.76-117.76z"
                            fill="#CCCCCC" p-id="48756"></path>
                        <path d="M619.25376 684.032m-20.48 0a20.48 20.48 0 1 0 40.96 0 20.48 20.48 0 1 0-40.96 0Z"
                              fill="#CCCCCC" p-id="48757"></path>
                        <path d="M702.84288 684.032m-20.48 0a20.48 20.48 0 1 0 40.96 0 20.48 20.48 0 1 0-40.96 0Z"
                              fill="#CCCCCC" p-id="48758"></path>
                        <path d="M786.432 684.032m-20.48 0a20.48 20.48 0 1 0 40.96 0 20.48 20.48 0 1 0-40.96 0Z"
                              fill="#CCCCCC" p-id="48759"></path>
                      </svg>
                      <span style="margin-left: 20px;cursor: pointer" @click="downloadFile(item)">{{
                          item.fileName
                        }}</span>
                    </div>
                    <div style="display: flex; align-items: center;">
                      {{ currentTask.name }}
                      <div style="border-left: 1px solid #ccc; height: 20px; margin: 0 10px;"></div>
                      <i class="el-icon-arrow-down arrow" style="font-size: 14px;"></i>
                      <el-dropdown trigger="click">
                        <svg t="1708673614126" class="icon" viewBox="0 0 1024 1024" version="1.1"
                             xmlns="http://www.w3.org/2000/svg" p-id="47521" width="16" height="16">
                          <path
                              d="M926.464 390.0928h48.7936v48.7424h-48.7936zM926.464 341.3504h48.7936v48.7424h-48.7936zM926.464 292.5568h48.7936v48.7936h-48.7936zM926.464 243.8144h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47522"></path>
                          <path
                              d="M926.464 195.072h48.7936v48.7424h-48.7936zM926.464 146.2784h48.7936v48.7936h-48.7936zM926.464 97.536h48.7936v48.7424h-48.7936zM877.7216 487.6288h48.7424v48.7424h-48.7424zM877.7216 438.8352h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47523"></path>
                          <path
                              d="M877.7216 390.0928h48.7424v48.7424h-48.7424zM877.7216 341.3504h48.7424v48.7424h-48.7424zM877.7216 292.5568h48.7424v48.7936h-48.7424zM877.7216 243.8144h48.7424v48.7424h-48.7424z"
                              fill="#DEE6FC" p-id="47524"></path>
                          <path
                              d="M877.7216 195.072h48.7424v48.7424h-48.7424zM877.7216 146.2784h48.7424v48.7936h-48.7424zM877.7216 97.536h48.7424v48.7424h-48.7424z"
                              fill="#DEE6FC" p-id="47525"></path>
                          <path
                              d="M877.7216 48.7424h48.7424v48.7936h-48.7424zM828.928 731.4432h48.7936v48.7424h-48.7936zM828.928 682.6496h48.7936v48.7936h-48.7936zM828.928 536.3712h48.7936v48.7936h-48.7936z"
                              fill="#012777" p-id="47526"></path>
                          <path
                              d="M828.928 487.6288h48.7936v48.7424h-48.7936zM828.928 438.8352h48.7936v48.7936h-48.7936z"
                              fill="#DEE6FC" p-id="47527"></path>
                          <path
                              d="M828.928 390.0928h48.7936v48.7424h-48.7936zM828.928 341.3504h48.7936v48.7424h-48.7936zM828.928 292.5568h48.7936v48.7936h-48.7936zM828.928 243.8144h48.7936v48.7424h-48.7936z"
                              fill="#DD75A1" p-id="47528"></path>
                          <path
                              d="M828.928 195.072h48.7936v48.7424h-48.7936zM828.928 146.2784h48.7936v48.7936h-48.7936z"
                              fill="#DD75A1" p-id="47529"></path>
                          <path d="M828.928 97.536h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47530"></path>
                          <path
                              d="M828.928 48.7424h48.7936v48.7936h-48.7936zM780.1856 780.1856h48.7424v48.7424h-48.7424z"
                              fill="#012777" p-id="47531"></path>
                          <path
                              d="M780.1856 731.4432h48.7424v48.7424h-48.7424zM780.1856 682.6496h48.7424v48.7936h-48.7424z"
                              fill="#DEE6FC" p-id="47532"></path>
                          <path
                              d="M780.1856 633.9072h48.7424v48.7424h-48.7424zM780.1856 585.1648h48.7424v48.7424h-48.7424z"
                              fill="#012777" p-id="47533"></path>
                          <path d="M780.1856 536.3712h48.7424v48.7936h-48.7424z" fill="#DEE6FC" p-id="47534"></path>
                          <path
                              d="M780.1856 487.6288h48.7424v48.7424h-48.7424zM780.1856 438.8352h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47535"></path>
                          <path
                              d="M780.1856 390.0928h48.7424v48.7424h-48.7424zM780.1856 341.3504h48.7424v48.7424h-48.7424zM780.1856 292.5568h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47536"></path>
                          <path d="M780.1856 243.8144h48.7424v48.7424h-48.7424z" fill="#DD75A1" p-id="47537"></path>
                          <path
                              d="M780.1856 195.072h48.7424v48.7424h-48.7424zM780.1856 146.2784h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47538"></path>
                          <path d="M780.1856 97.536h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47539"></path>
                          <path
                              d="M780.1856 48.7424h48.7424v48.7936h-48.7424zM731.4432 828.928h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47540"></path>
                          <path d="M731.4432 780.1856h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47541"></path>
                          <path
                              d="M731.4432 731.4432h48.7424v48.7424h-48.7424zM731.4432 682.6496h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47542"></path>
                          <path
                              d="M731.4432 633.9072h48.7424v48.7424h-48.7424zM731.4432 585.1648h48.7424v48.7424h-48.7424z"
                              fill="#DEE6FC" p-id="47543"></path>
                          <path
                              d="M731.4432 536.3712h48.7424v48.7936h-48.7424zM731.4432 487.6288h48.7424v48.7424h-48.7424z"
                              fill="#DD75A1" p-id="47544"></path>
                          <path d="M731.4432 438.8352h48.7424v48.7936h-48.7424z" fill="#012777" p-id="47545"></path>
                          <path
                              d="M731.4432 390.0928h48.7424v48.7424h-48.7424zM731.4432 341.3504h48.7424v48.7424h-48.7424zM731.4432 292.5568h48.7424v48.7936h-48.7424z"
                              fill="#375AA4" p-id="47546"></path>
                          <path d="M731.4432 243.8144h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47547"></path>
                          <path
                              d="M731.4432 195.072h48.7424v48.7424h-48.7424zM731.4432 146.2784h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47548"></path>
                          <path d="M731.4432 97.536h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47549"></path>
                          <path
                              d="M731.4432 48.7424h48.7424v48.7936h-48.7424zM682.6496 877.7216h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47550"></path>
                          <path d="M682.6496 828.928h48.7936v48.7936h-48.7936z" fill="#DEE6FC" p-id="47551"></path>
                          <path d="M682.6496 780.1856h48.7936v48.7424h-48.7936z" fill="#DD75A1" p-id="47552"></path>
                          <path
                              d="M682.6496 731.4432h48.7936v48.7424h-48.7936zM682.6496 682.6496h48.7936v48.7936h-48.7936zM682.6496 633.9072h48.7936v48.7424h-48.7936zM682.6496 585.1648h48.7936v48.7424h-48.7936zM682.6496 536.3712h48.7936v48.7936h-48.7936zM682.6496 487.6288h48.7936v48.7424h-48.7936z"
                              fill="#DD75A1" p-id="47553"></path>
                          <path d="M682.6496 438.8352h48.7936v48.7936h-48.7936z" fill="#012777" p-id="47554"></path>
                          <path
                              d="M682.6496 390.0928h48.7936v48.7424h-48.7936zM682.6496 341.3504h48.7936v48.7424h-48.7936zM682.6496 292.5568h48.7936v48.7936h-48.7936z"
                              fill="#375AA4" p-id="47555"></path>
                          <path d="M682.6496 243.8144h48.7936v48.7424h-48.7936z" fill="#A2D8EA" p-id="47556"></path>
                          <path d="M682.6496 195.072h48.7936v48.7424h-48.7936z" fill="#012777" p-id="47557"></path>
                          <path d="M682.6496 146.2784h48.7936v48.7936h-48.7936z" fill="#DD75A1" p-id="47558"></path>
                          <path d="M682.6496 97.536h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47559"></path>
                          <path
                              d="M682.6496 48.7424h48.7936v48.7936h-48.7936zM633.9072 926.464h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47560"></path>
                          <path d="M633.9072 877.7216h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47561"></path>
                          <path
                              d="M633.9072 828.928h48.7424v48.7936h-48.7424zM633.9072 780.1856h48.7424v48.7424h-48.7424z"
                              fill="#DD75A1" p-id="47562"></path>
                          <path
                              d="M633.9072 731.4432h48.7424v48.7424h-48.7424zM633.9072 682.6496h48.7424v48.7936h-48.7424zM633.9072 633.9072h48.7424v48.7424h-48.7424zM633.9072 585.1648h48.7424v48.7424h-48.7424zM633.9072 536.3712h48.7424v48.7936h-48.7424zM633.9072 487.6288h48.7424v48.7424h-48.7424z"
                              fill="#DD75A1" p-id="47563"></path>
                          <path d="M633.9072 438.8352h48.7424v48.7936h-48.7424z" fill="#012777" p-id="47564"></path>
                          <path
                              d="M633.9072 390.0928h48.7424v48.7424h-48.7424zM633.9072 341.3504h48.7424v48.7424h-48.7424z"
                              fill="#375AA4" p-id="47565"></path>
                          <path
                              d="M633.9072 292.5568h48.7424v48.7936h-48.7424zM633.9072 243.8144h48.7424v48.7424h-48.7424z"
                              fill="#A2D8EA" p-id="47566"></path>
                          <path d="M633.9072 195.072h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47567"></path>
                          <path d="M633.9072 146.2784h48.7424v48.7936h-48.7424z" fill="#DD75A1" p-id="47568"></path>
                          <path d="M633.9072 97.536h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47569"></path>
                          <path
                              d="M633.9072 48.7424h48.7424v48.7936h-48.7424zM585.1648 926.464h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47570"></path>
                          <path
                              d="M585.1648 877.7216h48.7424v48.7424h-48.7424zM585.1648 828.928h48.7424v48.7936h-48.7424z"
                              fill="#DEE6FC" p-id="47571"></path>
                          <path d="M585.1648 780.1856h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47572"></path>
                          <path
                              d="M585.1648 731.4432h48.7424v48.7424h-48.7424zM585.1648 682.6496h48.7424v48.7936h-48.7424z"
                              fill="#DEE6FC" p-id="47573"></path>
                          <path
                              d="M585.1648 633.9072h48.7424v48.7424h-48.7424zM585.1648 585.1648h48.7424v48.7424h-48.7424zM585.1648 536.3712h48.7424v48.7936h-48.7424zM585.1648 487.6288h48.7424v48.7424h-48.7424zM585.1648 438.8352h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47574"></path>
                          <path d="M585.1648 390.0928h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47575"></path>
                          <path
                              d="M585.1648 341.3504h48.7424v48.7424h-48.7424zM585.1648 292.5568h48.7424v48.7936h-48.7424zM585.1648 243.8144h48.7424v48.7424h-48.7424z"
                              fill="#A2D8EA" p-id="47576"></path>
                          <path d="M585.1648 195.072h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47577"></path>
                          <path d="M585.1648 146.2784h48.7424v48.7936h-48.7424z" fill="#DD75A1" p-id="47578"></path>
                          <path d="M585.1648 97.536h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47579"></path>
                          <path
                              d="M585.1648 48.7424h48.7424v48.7936h-48.7424zM536.3712 877.7216h48.7936v48.7424h-48.7936zM536.3712 828.928h48.7936v48.7936h-48.7936zM536.3712 731.4432h48.7936v48.7424h-48.7936zM536.3712 682.6496h48.7936v48.7936h-48.7936z"
                              fill="#012777" p-id="47580"></path>
                          <path d="M536.3712 633.9072h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47581"></path>
                          <path
                              d="M536.3712 585.1648h48.7936v48.7424h-48.7936zM536.3712 536.3712h48.7936v48.7936h-48.7936zM536.3712 487.6288h48.7936v48.7424h-48.7936zM536.3712 438.8352h48.7936v48.7936h-48.7936zM536.3712 390.0928h48.7936v48.7424h-48.7936z"
                              fill="#DD75A1" p-id="47582"></path>
                          <path
                              d="M536.3712 341.3504h48.7936v48.7424h-48.7936zM536.3712 292.5568h48.7936v48.7936h-48.7936zM536.3712 243.8144h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47583"></path>
                          <path d="M536.3712 195.072h48.7936v48.7424h-48.7936z" fill="#DD75A1" p-id="47584"></path>
                          <path d="M536.3712 146.2784h48.7936v48.7936h-48.7936z" fill="#DEE6FC" p-id="47585"></path>
                          <path
                              d="M536.3712 97.536h48.7936v48.7424h-48.7936zM487.6288 731.4432h48.7424v48.7424h-48.7424z"
                              fill="#012777" p-id="47586"></path>
                          <path d="M487.6288 682.6496h48.7424v48.7936h-48.7424z" fill="#A2D8EA" p-id="47587"></path>
                          <path d="M487.6288 633.9072h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47588"></path>
                          <path d="M487.6288 585.1648h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47589"></path>
                          <path
                              d="M487.6288 536.3712h48.7424v48.7936h-48.7424zM487.6288 487.6288h48.7424v48.7424h-48.7424zM487.6288 438.8352h48.7424v48.7936h-48.7424zM487.6288 390.0928h48.7424v48.7424h-48.7424zM487.6288 341.3504h48.7424v48.7424h-48.7424zM487.6288 292.5568h48.7424v48.7936h-48.7424zM487.6288 243.8144h48.7424v48.7424h-48.7424z"
                              fill="#DD75A1" p-id="47590"></path>
                          <path d="M487.6288 195.072h48.7424v48.7424h-48.7424z" fill="#DD75A1" p-id="47591"></path>
                          <path d="M487.6288 146.2784h48.7424v48.7936h-48.7424z" fill="#DEE6FC" p-id="47592"></path>
                          <path
                              d="M487.6288 97.536h48.7424v48.7424h-48.7424zM438.8352 731.4432h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47593"></path>
                          <path d="M438.8352 682.6496h48.7936v48.7936h-48.7936z" fill="#375AA4" p-id="47594"></path>
                          <path d="M438.8352 633.9072h48.7936v48.7424h-48.7936z" fill="#A2D8EA" p-id="47595"></path>
                          <path d="M438.8352 585.1648h48.7936v48.7424h-48.7936z" fill="#012777" p-id="47596"></path>
                          <path d="M438.8352 536.3712h48.7936v48.7936h-48.7936z" fill="#DEE6FC" p-id="47597"></path>
                          <path
                              d="M438.8352 487.6288h48.7936v48.7424h-48.7936zM438.8352 438.8352h48.7936v48.7936h-48.7936zM438.8352 390.0928h48.7936v48.7424h-48.7936zM438.8352 341.3504h48.7936v48.7424h-48.7936zM438.8352 292.5568h48.7936v48.7936h-48.7936zM438.8352 243.8144h48.7936v48.7424h-48.7936z"
                              fill="#DD75A1" p-id="47598"></path>
                          <path d="M438.8352 195.072h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47599"></path>
                          <path
                              d="M438.8352 146.2784h48.7936v48.7936h-48.7936zM390.0928 828.928h48.7424v48.7936h-48.7424zM390.0928 682.6496h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47600"></path>
                          <path d="M390.0928 633.9072h48.7424v48.7424h-48.7424z" fill="#375AA4" p-id="47601"></path>
                          <path d="M390.0928 585.1648h48.7424v48.7424h-48.7424z" fill="#A2D8EA" p-id="47602"></path>
                          <path d="M390.0928 536.3712h48.7424v48.7936h-48.7424z" fill="#012777" p-id="47603"></path>
                          <path d="M390.0928 487.6288h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47604"></path>
                          <path
                              d="M390.0928 438.8352h48.7424v48.7936h-48.7424zM390.0928 390.0928h48.7424v48.7424h-48.7424zM390.0928 341.3504h48.7424v48.7424h-48.7424zM390.0928 292.5568h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47605"></path>
                          <path d="M390.0928 243.8144h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47606"></path>
                          <path
                              d="M390.0928 195.072h48.7424v48.7424h-48.7424zM341.3504 877.7216h48.7424v48.7424h-48.7424zM341.3504 633.9072h48.7424v48.7424h-48.7424z"
                              fill="#012777" p-id="47607"></path>
                          <path d="M341.3504 585.1648h48.7424v48.7424h-48.7424z" fill="#375AA4" p-id="47608"></path>
                          <path d="M341.3504 536.3712h48.7424v48.7936h-48.7424z" fill="#A2D8EA" p-id="47609"></path>
                          <path d="M341.3504 487.6288h48.7424v48.7424h-48.7424z" fill="#012777" p-id="47610"></path>
                          <path d="M341.3504 438.8352h48.7424v48.7936h-48.7424z" fill="#DEE6FC" p-id="47611"></path>
                          <path
                              d="M341.3504 390.0928h48.7424v48.7424h-48.7424zM341.3504 341.3504h48.7424v48.7424h-48.7424zM341.3504 292.5568h48.7424v48.7936h-48.7424z"
                              fill="#DD75A1" p-id="47612"></path>
                          <path d="M341.3504 243.8144h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47613"></path>
                          <path
                              d="M341.3504 195.072h48.7424v48.7424h-48.7424zM292.5568 926.464h48.7936v48.7936h-48.7936zM292.5568 780.1856h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47614"></path>
                          <path
                              d="M292.5568 731.4432h48.7936v48.7424h-48.7936zM292.5568 585.1648h48.7936v48.7424h-48.7936z"
                              fill="#012777" p-id="47615"></path>
                          <path d="M292.5568 536.3712h48.7936v48.7936h-48.7936z" fill="#375AA4" p-id="47616"></path>
                          <path d="M292.5568 487.6288h48.7936v48.7424h-48.7936z" fill="#A2D8EA" p-id="47617"></path>
                          <path d="M292.5568 438.8352h48.7936v48.7936h-48.7936z" fill="#012777" p-id="47618"></path>
                          <path d="M292.5568 390.0928h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47619"></path>
                          <path
                              d="M292.5568 341.3504h48.7936v48.7424h-48.7936zM292.5568 292.5568h48.7936v48.7936h-48.7936zM292.5568 243.8144h48.7936v48.7424h-48.7936z"
                              fill="#DD75A1" p-id="47620"></path>
                          <path d="M292.5568 195.072h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47621"></path>
                          <path
                              d="M292.5568 146.2784h48.7936v48.7936h-48.7936zM243.8144 828.928h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47622"></path>
                          <path d="M243.8144 780.1856h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47623"></path>
                          <path d="M243.8144 731.4432h48.7424v48.7424h-48.7424z" fill="#DD75A1" p-id="47624"></path>
                          <path
                              d="M243.8144 682.6496h48.7424v48.7936h-48.7424zM243.8144 536.3712h48.7424v48.7936h-48.7424zM243.8144 487.6288h48.7424v48.7424h-48.7424zM243.8144 438.8352h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47625"></path>
                          <path d="M243.8144 390.0928h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47626"></path>
                          <path
                              d="M243.8144 341.3504h48.7424v48.7424h-48.7424zM243.8144 292.5568h48.7424v48.7936h-48.7424zM243.8144 243.8144h48.7424v48.7424h-48.7424z"
                              fill="#DD75A1" p-id="47627"></path>
                          <path d="M243.8144 195.072h48.7424v48.7424h-48.7424z" fill="#DEE6FC" p-id="47628"></path>
                          <path
                              d="M243.8144 146.2784h48.7424v48.7936h-48.7424zM195.072 877.7216h48.7424v48.7424H195.072z"
                              fill="#012777" p-id="47629"></path>
                          <path d="M195.072 828.928h48.7424v48.7936H195.072z" fill="#DEE6FC" p-id="47630"></path>
                          <path d="M195.072 780.1856h48.7424v48.7424H195.072z" fill="#DD75A1" p-id="47631"></path>
                          <path d="M195.072 731.4432h48.7424v48.7424H195.072z" fill="#DEE6FC" p-id="47632"></path>
                          <path d="M195.072 682.6496h48.7424v48.7936H195.072zM195.072 390.0928h48.7424v48.7424H195.072z"
                                fill="#012777" p-id="47633"></path>
                          <path d="M195.072 341.3504h48.7424v48.7424H195.072zM195.072 292.5568h48.7424v48.7936H195.072z"
                                fill="#DD75A1" p-id="47634"></path>
                          <path d="M195.072 243.8144h48.7424v48.7424H195.072z" fill="#DEE6FC" p-id="47635"></path>
                          <path d="M195.072 195.072h48.7424v48.7424H195.072zM146.2784 926.464h48.7936v48.7936h-48.7936z"
                                fill="#012777" p-id="47636"></path>
                          <path d="M146.2784 877.7216h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47637"></path>
                          <path d="M146.2784 828.928h48.7936v48.7936h-48.7936z" fill="#DD75A1" p-id="47638"></path>
                          <path d="M146.2784 780.1856h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47639"></path>
                          <path
                              d="M146.2784 731.4432h48.7936v48.7424h-48.7936zM146.2784 585.1648h48.7936v48.7424h-48.7936zM146.2784 438.8352h48.7936v48.7936h-48.7936z"
                              fill="#012777" p-id="47640"></path>
                          <path d="M146.2784 390.0928h48.7936v48.7424h-48.7936z" fill="#DEE6FC" p-id="47641"></path>
                          <path d="M146.2784 341.3504h48.7936v48.7424h-48.7936z" fill="#DD75A1" p-id="47642"></path>
                          <path d="M146.2784 292.5568h48.7936v48.7936h-48.7936z" fill="#DEE6FC" p-id="47643"></path>
                          <path
                              d="M146.2784 243.8144h48.7936v48.7424h-48.7936zM97.536 926.464h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47644"></path>
                          <path d="M97.536 877.7216h48.7424v48.7424h-48.7424z" fill="#DD75A1" p-id="47645"></path>
                          <path d="M97.536 828.928h48.7424v48.7936h-48.7424z" fill="#DEE6FC" p-id="47646"></path>
                          <path
                              d="M97.536 780.1856h48.7424v48.7424h-48.7424zM97.536 633.9072h48.7424v48.7424h-48.7424zM97.536 438.8352h48.7424v48.7936h-48.7424z"
                              fill="#012777" p-id="47647"></path>
                          <path d="M97.536 390.0928h48.7424v48.7424h-48.7424zM97.536 341.3504h48.7424v48.7424h-48.7424z"
                                fill="#DEE6FC" p-id="47648"></path>
                          <path
                              d="M97.536 292.5568h48.7424v48.7936h-48.7424zM48.7424 877.7216h48.7936v48.7424H48.7424zM48.7424 828.928h48.7936v48.7936H48.7424zM48.7424 682.6496h48.7936v48.7936H48.7424zM48.7424 390.0928h48.7936v48.7424H48.7424zM48.7424 341.3504h48.7936v48.7424H48.7424z"
                              fill="#012777" p-id="47649"></path>
                        </svg>
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item @click="handleCopyTaskFilePath(item)">复制文件链接</el-dropdown-item>
                            <el-dropdown-item @click="handleCancelTaskFile(item)">取消关联</el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div style="width: 30%; margin-left: 15px; max-height: 680px; background-color: #fff; overflow: hidden;">
          <!-- 右侧内容 -->
          <div style="height: calc(100% - 60px); display: flex; flex-direction: column;">
            <!-- 下拉菜单 -->
            <div style="padding: 10px;">
              <el-dropdown trigger="click" type="success">
                  <span class="el-dropdown-link">
                    所有动态
                    <el-icon class="el-icon--right">
                      <arrow-down/>
                    </el-icon>
                  </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="getTaskByTaskId">所有动态</el-dropdown-item>
                    <el-dropdown-item @click="handleGetTaskComment">仅评论</el-dropdown-item>
                    <el-dropdown-item @click="handleGetTaskLog">仅日志</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <!-- 消息列表 -->
            <div style=" overflow-y: auto;" @scroll="closeMenu">
              <!-- 消息列表 -->
              <div class="message-list" style="flex-grow: 1; overflow-y: auto;">
                <!-- 评论 -->
                <div v-for="item in currentTask.logAndCommentList">
                  <div v-if="item.isLog === 0" class="message-item p-4"
                       @contextmenu.prevent.native="handleShowContextMenu($event,item)">
                    <div class="flex items-center space-x-4 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-850">
                    <span
                        class="relative flex-shrink-0 overflow-hidden rounded-full w-10 h-10 bg-muted flex items-center justify-center">
                      <img :src="getFilePath(item.avatar)"/>
                    </span>
                      <div class="flex-1 grid gap-2.5">
                        <div class="font-semibold">{{ item.createBy }}</div>
                        <div class="text-base text-black">{{ item.content }}</div>
                      </div>
                      <div class="text-sm text-black ">{{ computedDateDiff(item.createTime) }}</div>
                    </div>
                  </div>
                  <!-- 日志 -->
                  <div v-else class="w-full grid max-w-sm rounded-lg dark:divide-gray-800">
                    <div class="flex items-center p-4 space-x-4">
                      <div class="flex items-center flex-1">
                        <div class="  w-8 h-8 dark:bg-gray-800 flex items-center justify-center flex-shrink-0">
                          <!-- 任务状态 -->
                          <svg v-if="item.type === taskType.STATUS" t="1708494309753" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="27291" width="16" height="16">
                            <path
                                d="M933.034667 77.653333h-89.6v115.541334c0 58.368-45.568 105.642667-101.546667 105.642666-56.149333 0-101.546667-47.274667-101.546667-105.642666V77.653333H381.098667v115.541334c0 58.368-45.568 105.642667-101.546667 105.642666-56.149333 0-101.546667-47.274667-101.546667-105.642666V77.653333h-87.04C40.789333 77.653333 0 118.954667 0 169.984v761.685333C0 982.698667 40.789333 1024 90.965333 1024h841.898667c50.346667 0 90.965333-41.301333 90.965333-92.330667V169.984c0.170667-51.029333-40.618667-92.330667-90.794666-92.330667zM742.058667 460.8L490.666667 731.306667c-8.192 8.874667-19.456 13.824-31.232 13.824-11.434667 0-22.528-4.778667-30.549334-13.312l-149.162666-158.208c-12.970667-11.264-18.432-29.354667-13.994667-46.421334 4.437333-16.896 17.92-29.696 34.474667-32.597333 16.554667-2.901333 33.450667 4.437333 43.008 18.773333l115.541333 125.098667 222.037333-240.469333c17.066667-13.653333 41.301333-12.117333 56.661334 3.584 15.530667 16.042667 17.408 41.130667 4.608 59.221333z"
                                fill="#2B3F64" p-id="27292"></path>
                            <path
                                d="M741.717333 0c25.258667 0 45.909333 21.162667 45.909334 47.445333v142.848c0 26.282667-20.48 47.445333-45.909334 47.445334s-45.909333-21.162667-45.909333-47.445334V47.445333c0-26.282667 20.48-47.445333 45.909333-47.445333zM279.552 0c25.429333 0 45.909333 21.162667 45.909333 47.445333v142.848c0 26.282667-20.48 47.445333-45.909333 47.445334-25.258667 0-45.909333-21.162667-45.909333-47.445334V47.445333c0-26.282667 20.48-47.445333 45.909333-47.445333z"
                                fill="#2B3F64" p-id="27293"></path>
                          </svg>
                          <!-- 紧急程度 -->
                          <svg v-else-if="item.type === taskType.URGENCY" t="1708494135801" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="24693" width="16" height="16">
                            <path d="M0 0h1024v1024H0z" fill="#FFFFFF" p-id="24694"></path>
                            <path
                                d="M707.413333 279.125333a277.333333 277.333333 0 0 0-421.12 237.226667v268.8H785.066667v-315.733333c0-73.813333-29.866667-141.226667-77.653334-190.293334z m-232.106666 403.626667l24.32-149.333333-72.96-21.333334 97.706666-170.666666v128l72.96 21.333333-122.026666 192z"
                                fill="#CCE1FE" p-id="24695"></path>
                            <path
                                d="M475.306667 682.666667l24.32-149.333334L426.666667 512l97.706666-170.666667v128l72.96 21.333334-122.026666 192z"
                                fill="#3974C7" p-id="24696"></path>
                            <path
                                d="M905.386667 787.2H853.333333V469.333333c0-176.64-134.826667-322.133333-306.773333-339.626666V110.933333h59.306667c18.773333 0 34.133333-15.36 34.133333-34.133333 0-18.773333-15.36-34.133333-34.133333-34.133333H418.133333c-18.773333 0-34.133333 15.36-34.133333 34.133333 0 18.773333 15.36 34.133333 34.133333 34.133333h60.16v18.773334C305.92 146.773333 170.666667 292.266667 170.666667 469.333333v317.866667H118.613333c-18.346667 0-33.28 14.506667-33.28 32.853333s14.933333 33.28 33.28 33.28h786.773334c18.346667 0 33.28-14.933333 33.28-33.28 0-18.346667-14.933333-32.853333-33.28-32.853333zM238.933333 469.333333c0-150.613333 122.453333-273.066667 273.066667-273.066666 76.373333 0 145.92 31.573333 195.413333 82.773333A272.682667 272.682667 0 0 1 785.066667 469.333333v315.733334H238.933333V469.333333zM512 981.333333c46.933333 0 85.333333-38.4 85.333333-85.333333h-170.666666c0 46.933333 38.4 85.333333 85.333333 85.333333z"
                                fill="#303133" p-id="24697"></path>
                          </svg>
                          <!-- 标签 -->
                          <svg v-else-if="item.type === taskType.TAG" t="1708521861918" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="9716" width="16" height="16">
                            <path
                                d="M771.8912 108.1344H255.1296c-47.2576 0-85.5552 38.2976-85.5552 85.5552v156.4672h687.872V193.6896c0-47.2576-38.2976-85.5552-85.5552-85.5552z"
                                fill="#ffa115" p-id="9717"></path>
                            <path
                                d="M233.0624 970.1888c-15.8208 0-31.6416-4.096-46.1824-12.2368-30.0544-16.896-48.0256-47.5648-48.0256-82.0736V197.4272c0-73.4208 59.6992-133.12 133.12-133.12h485.4272c73.4208 0 133.12 59.6992 133.12 133.12v672.768c0 34.7648-19.0464 66.6112-49.7152 82.9952a93.96736 93.96736 0 0 1-96.6656-4.6592l-197.1712-131.4816a32.75264 32.75264 0 0 0-35.1232-0.7168l-229.888 139.9296c-15.2064 9.2672-32.0512 13.9264-48.896 13.9264z m38.912-844.4416c-39.5264 0-71.68 32.1536-71.68 71.68v678.4c0 17.2544 11.6736 25.7024 16.6912 28.5184 5.0176 2.816 18.2784 8.3968 33.024-0.5632l229.888-139.9296c31.1296-18.944 70.8608-18.1248 101.1712 2.0992L778.24 897.4336c10.1888 6.8096 22.784 7.424 33.5872 1.6384 10.8032-5.7856 17.2544-16.5888 17.2544-28.8256V197.4272c0-39.5264-32.1536-71.68-71.68-71.68H271.9744z"
                                fill="#474A54" p-id="9718"></path>
                            <path
                                d="M678.5024 287.0272H350.9248c-16.9472 0-30.72-13.7728-30.72-30.72s13.7728-30.72 30.72-30.72h327.6288c16.9472 0 30.72 13.7728 30.72 30.72s-13.7728 30.72-30.7712 30.72z"
                                fill="#474A54" p-id="9719"></path>
                          </svg>
                          <!-- 排序 -->
                          <svg v-else-if="item.type === taskType.ORDER" t="1708494388439" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="29898" width="16" height="16">
                            <path
                                d="M358.265 273.813l-111.561 94.165c-19.508 16.466-48.67 14-65.136-5.508-16.466-19.507-14-48.67 5.508-65.135l187.597-158.346c30.052-25.365 76.036-4.003 76.036 35.322v682.667c0 25.528-20.694 46.222-46.222 46.222-25.528 0-46.222-20.694-46.222-46.222V273.813z m410.837 389.409c19.507-16.466 48.67-14 65.135 5.508 16.466 19.507 14 48.67-5.507 65.135L641.132 892.211c-30.05 25.365-76.036 4.003-76.036-35.322V174.222c0-25.528 20.695-46.222 46.223-46.222 25.527 0 46.222 20.694 46.222 46.222v583.165l111.56-94.165z"
                                fill="#1296db" p-id="29899"></path>
                          </svg>
                          <!-- 描述 -->
                          <svg v-else-if="item.type === taskType.DESCRIPTION" t="1708494612416" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="41652" width="16" height="16">
                            <path
                                d="M887.466667 120.285867c0-24.9856-23.552-18.7392-30.890667-11.3664-13.994667 15.36-51.063467 46.557867-130.218667 63.010133a159.709867 159.709867 0 0 1-36.488533 3.959467c-38.161067 0-70.178133-12.458667-106.632533-27.2384-42.120533-16.452267-89.838933-35.157333-154.965334-35.157334-5.597867 0-11.195733 0-16.827733 0.546134-70.724267 3.413333-131.925333 34.6112-176.264533 57.890133-13.0048 6.587733-26.112 13.0048-39.253334 19.285333l-3.959466-9.6256-0.546134-0.580266a28.535467 28.535467 0 0 0-37.614933-15.325867 27.989333 27.989333 0 0 0-15.1552 15.906133 30.139733 30.139733 0 0 0 0 22.1184l292.4544 700.3136a28.194133 28.194133 0 0 0 26.385067 17.578667 24.917333 24.917333 0 0 0 11.229866-2.286933 29.0816 29.0816 0 0 0 15.1552-37.9904l-99.9424-239.5136a161.553067 161.553067 0 0 1 96.5632-73.181867 214.801067 214.801067 0 0 1 55.022934-7.406933c37.614933 0 74.069333 8.533333 112.810666 17.6128 35.362133 7.953067 71.304533 16.452267 106.666667 16.452266 53.316267 0 94.3104-18.7392 129.672533-58.4704a28.7744 28.7744 0 0 0 2.798934-9.079466V120.285867z"
                                fill="#85C2FF" p-id="41653"></path>
                          </svg>
                          <!-- 指派人 -->
                          <svg v-else-if="item.type === taskType.ASSIGN" t="1708494528945" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="39776" width="16" height="16">
                            <path
                                d="M1005.049995 672.258716a66.824088 66.824088 0 0 0-62.432952-38.000218h-154.42163a50.216585 50.216585 0 0 0 5.291883-34.284642 67.837427 67.837427 0 0 0-40.702457-51.398814 73.185606 73.185606 0 0 0-77.182666 12.216366l-12.66674 10.921545c-23.98236 20.66086-46.669898 40.195787-70.201885 61.419612H541.953996a26.065335 26.065335 0 0 0-25.277182 26.740895v248.887358a28.14831 28.14831 0 0 0 8.388196 18.352698 29.668319 29.668319 0 0 0 18.296402 7.037078h53.988458a104.9369 104.9369 0 0 1 11.540808 6.248925 96.436111 96.436111 0 0 0 15.706757 8.050417l10.865247 6.755594a67.555944 67.555944 0 0 0 17.789732 4.053357h145.977137l-0.731856-0.394077a31.751294 31.751294 0 0 0 13.792672-5.235585 57.535146 57.535146 0 0 0 48.696576-63.952961 9.288942 9.288942 0 0 0-1.125932-3.377797 23.475691 23.475691 0 0 1-1.351119-3.490391 52.63734 52.63734 0 0 0 27.585344-32.257963 63.164808 63.164808 0 0 0-2.308161-46.613602 67.555944 67.555944 0 0 0 14.243044-40.871346h41.772093a52.524747 52.524747 0 0 0 21.730495-2.702238 62.601842 62.601842 0 0 0 43.460991-36.592803 58.041816 58.041816 0 0 0 0.056297-51.511408z m-40.871347 28.936463a27.022378 27.022378 0 0 1-16.15713 11.259324h-0.562966a14.186748 14.186748 0 0 1-9.626722 2.364458h-82.981218l-12.891926 22.912725 6.924484 11.259324a19.81641 19.81641 0 0 1 1.238526 2.026678 20.379377 20.379377 0 0 1-8.83857 27.472751l-8.444493 7.037077-2.871127 22.518649 7.205967 7.205967a17.395656 17.395656 0 0 1 3.65928 19.253444 26.065335 26.065335 0 0 1-17.620842 13.680079l-11.259324 4.278543-7.149671 22.856428 7.374858 9.288942a13.286002 13.286002 0 0 1 3.49039 6.755595 17.226766 17.226766 0 0 1-12.891926 18.071215 20.773453 20.773453 0 0 0-3.490391 1.688898h-0.394076v0.394077c-0.844449 1.688899-4.16595 1.688899-6.417815 1.688898h-136.237821L643.287913 906.56525c-5.629662-2.420755-11.82229-5.629662-17.733436-9.120052-3.546687-1.970382-6.980781-3.884467-10.245985-5.629662a94.409432 94.409432 0 0 0-19.084554-4.053357H561.094847v-211.675292h33.777972a31.638701 31.638701 0 0 0 23.757174-8.3319c12.441553-10.808951 24.601623-21.561606 36.761693-32.31426 15.98824-14.130452 32.539447-28.767573 49.484729-43.404695a21.054936 21.054936 0 0 1 26.797191-6.192628c8.275603 5.629662 13.792672 10.245985 13.792672 15.931944a18.859368 18.859368 0 0 1-7.431153 16.888986l-2.364458 2.420755c-18.071215 11.259324-24.545326 23.98236-19.140851 38.000218a30.737955 30.737955 0 0 0 26.515708 15.368978h197.488544c12.891926 0 19.929004 3.99706 23.588284 13.680078a12.32896 12.32896 0 0 1 0.056296 13.060816z"
                                fill="#8BAEF7" p-id="39777"></path>
                            <path
                                d="M448.951979 956.444056H103.403324a21.167529 21.167529 0 0 1-15.594164-7.712637 32.14537 32.14537 0 0 1-7.205967-25.558666c29.893505-219.556819 198.952256-386.363705 396.384503-394.076342 4.785213 0 9.626722 0.394076 14.524528 0.394076 145.92084 0 264.594115-133.704473 264.594116-264.594115a264.594115 264.594115 0 1 0-409.895693 220.851641 472.103458 472.103458 0 0 0-172.492844 106.682096C87.47138 675.749107 30.668091 789.749763 13.722808 914.052701a99.645018 99.645018 0 0 0 23.250504 78.815268 88.554584 88.554584 0 0 0 66.486309 30.850548h345.492358a33.777972 33.777972 0 0 0 0-67.555944zM295.994062 264.783779A195.574459 195.574459 0 1 1 491.287038 460.358238 195.799645 195.799645 0 0 1 295.994062 264.783779z"
                                fill="#467CFD" p-id="39778"></path>
                          </svg>
                          <!-- 时间 -->
                          <svg v-else-if="item.type === taskType.TIME" t="1708494693058" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="50650" width="16" height="16">
                            <path
                                d="M912.82 342.68A436.48 436.48 0 1 0 947 512a433.41 433.41 0 0 0-34.18-169.32zM752.54 745.77l-3.66 3.89a27.2 27.2 0 0 1-38.28 1.16L560.14 609.21a108.49 108.49 0 1 1-77.89-201.56V253.08A27.11 27.11 0 0 1 509.33 226h5.34a27.11 27.11 0 0 1 27.08 27.08v154.57A108.46 108.46 0 0 1 604.26 569l147.12 138.5a27.11 27.11 0 0 1 1.16 38.27z"
                                fill="#F9DB91" p-id="50651"></path>
                            <path
                                d="M541.75 407.65V253.08A27.11 27.11 0 0 0 514.67 226h-5.34a27.11 27.11 0 0 0-27.08 27.08v154.57a109 109 0 0 1 59.5 0zM751.38 707.5L604.26 569a109.27 109.27 0 0 1-44.12 40.18L710.6 750.82a27.2 27.2 0 0 0 38.28-1.16l3.66-3.89a27.11 27.11 0 0 0-1.16-38.27z"
                                fill="#EF6A6A" p-id="50652"></path>
                            <path d="M604.26 569a108.48 108.48 0 1 0-44.12 40.18A109.27 109.27 0 0 0 604.26 569z"
                                  fill="#AEF0FF"
                                  p-id="50653"></path>
                            <path
                                d="M512 64C264.58 64 64 264.58 64 512s200.58 448 448 448 448-200.58 448-448S759.42 64 512 64z m298.4 746.4A420.48 420.48 0 0 1 525 933.8V896a13 13 0 0 0-26 0v37.8A421.57 421.57 0 0 1 90.2 525H128a13 13 0 0 0 0-26H90.2A421.57 421.57 0 0 1 499 90.2V128a13 13 0 0 0 26 0V90.2A421.57 421.57 0 0 1 933.8 499H896a13 13 0 0 0 0 26h37.8a420.48 420.48 0 0 1-123.4 285.4z"
                                fill="#512C56" p-id="50654"></path>
                            <path
                                d="M760.29 698L620.58 566.54a121.57 121.57 0 0 0-65.83-168.29V253.08A40.2 40.2 0 0 0 514.67 213h-5.34a40.2 40.2 0 0 0-40.08 40.08v145.17a121.52 121.52 0 1 0 88.33 226.4l144.11 135.64a40.2 40.2 0 0 0 56.65-1.72l3.66-3.89a40.19 40.19 0 0 0-1.71-56.68z m-265-444.95A14.27 14.27 0 0 1 509.33 239h5.34a14.27 14.27 0 0 1 14.08 14.08v138.58a122.56 122.56 0 0 0-33.5 0zM416.51 512A95.49 95.49 0 1 1 512 607.49 95.59 95.59 0 0 1 416.51 512z m326.56 224.86l-3.66 3.89a14.21 14.21 0 0 1-19.9 0.61L581.62 611.57a122.35 122.35 0 0 0 24.59-22.85L742.47 717a14.27 14.27 0 0 1 0.6 19.86z"
                                fill="#512C56" p-id="50655"></path>
                          </svg>
                          <!-- 子任务 -->
                          <svg v-else-if="item.type === taskType.CHILD_TASK" t="1708525997990" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="23851" width="16" height="16">
                            <path
                                d="M624 652.8h280c24 0 43.2-19.2 43.2-41.6V420.8c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6V486.4h-291.2V297.6H400c24 0 43.2-19.2 43.2-41.6V65.6c0-22.4-19.2-41.6-43.2-41.6H120c-24 0-43.2 19.2-43.2 41.6V256c0 22.4 19.2 41.6 43.2 41.6H230.4v595.2h348.8v65.6c0 22.4 19.2 41.6 43.2 41.6H902.4c24 0 43.2-19.2 43.2-41.6V768c0-22.4-19.2-41.6-43.2-41.6H624c-24 0-43.2 19.2-43.2 41.6v65.6h-291.2v-288h291.2V611.2c0 22.4 19.2 41.6 43.2 41.6z m12.8-222.4h252.8V601.6H636.8V430.4zM132.8 246.4v-172.8h252.8v171.2h-252.8v1.6z m504 531.2h252.8v171.2H636.8V777.6z"
                                fill="#346FF1" p-id="23852"></path>
                          </svg>
                          <!-- 任务名称 -->
                          <svg v-else-if="item.type === taskType.NAME" t="1708494215342" class="icon"
                               viewBox="0 0 1024 1024" version="1.1"
                               xmlns="http://www.w3.org/2000/svg" p-id="25761" width="16" height="16">
                            <path
                                d="M193.378462 759.729231c-48.600615-79.084308-30.326154-226.067692-13.154462-279.315693l48.128-11.382153A198.301538 198.301538 0 0 1 271.36 325.316923c66.599385-84.676923 256.078769-253.715692 620.189538-273.723077-132.529231 148.676923-227.682462 401.959385-251.31323 438.941539a815.104 815.104 0 0 1-97.988923 56.477538l44.032 42.968615s-244.224 210.313846-392.900923 169.747693"
                                fill="#49D2FF" p-id="25762"></path>
                            <path
                                d="M206.217846 742.518154c33.043692 6.695385 73.964308 0.787692 120.950154-16.344616 40.448-14.769231 83.810462-37.257846 128.433231-65.378461a1128.684308 1128.684308 0 0 0 101.179077-72.073846l-28.317539-27.608616a19.692308 19.692308 0 0 1 5.12-31.783384c31.507692-15.36 62.030769-32.846769 91.254154-52.263385 10.870154-23.748923 89.009231-190.582154 133.080615-267.618461 29.144615-50.963692 58.683077-96.098462 88.851693-134.774154-267.579077 24.654769-457.373538 132.411077-560.128 263.010461a178.609231 178.609231 0 0 0-38.675693 129.417846 19.692308 19.692308 0 0 1-15.084307 21.110154l-37.021539 8.743385c-20.283077 72.940308-20.873846 186.052923 10.358154 245.563077z m369.427692-190.424616l24.379077 23.788308a19.692308 19.692308 0 0 1-0.905846 29.026462c-1.732923 1.457231-4.883692 4.135385-9.412923 7.837538a1167.596308 1167.596308 0 0 1-113.073231 81.368616c-46.828308 29.499077-92.553846 53.208615-135.955692 69.04123-57.698462 21.070769-109.095385 27.411692-152.536615 15.517539a19.692308 19.692308 0 0 1-11.579077-8.664616c-44.110769-71.798154-42.417231-210.865231-15.044923-295.620923a19.692308 19.692308 0 0 1 14.178461-13.115077l32.059077-7.60123a217.796923 217.796923 0 0 1 48.088616-140.524308C371.042462 166.715077 587.067077 48.600615 890.486154 31.901538a19.692308 19.692308 0 0 1 15.753846 32.807385c-38.675692 43.401846-76.603077 98.697846-114.097231 164.233846-46.395077 81.132308-134.616615 271.005538-135.325538 272.147693l-5.592616 5.710769c-24.379077 16.423385-49.624615 31.507692-75.539692 45.292307z"
                                fill="#505050" p-id="25763"></path>
                            <path
                                d="M596.795077 246.153846l23.630769 31.547077A1376.689231 1376.689231 0 0 0 147.219692 921.757538l-37.139692-13.075692A1416.073846 1416.073846 0 0 1 596.795077 246.153846zM193.142154 953.344l-21.110154-33.240615c25.009231-15.911385 73.649231-38.872615 108.307692-48.167385 26.742154-7.168 47.497846-7.798154 62.660923 2.048 20.716308 13.390769 21.819077 38.360615 8.073847 70.498462-5.198769 12.209231-7.325538 8.192-2.363077 8.152615 14.060308-0.078769 39.896615-8.073846 73.412923-22.646154 50.609231-21.976615 115.672615-57.501538 158.404923-83.574154a688.324923 688.324923 0 0 1 144.856615-66.402461c57.186462-17.880615 118.941538-16.699077 161.437539 4.52923 51.081846 25.560615 68.923077 76.130462 43.598769 138.909539l-36.548923-14.729846c17.841231-44.110769 7.719385-72.743385-24.654769-88.930462-32.531692-16.265846-83.928615-17.250462-131.859693-2.284307a648.467692 648.467692 0 0 0-136.310154 62.542769c-44.150154 26.939077-110.670769 63.251692-163.24923 86.094769-38.203077 16.580923-67.859692 25.757538-88.851693 25.875692-35.643077 0.196923-49.703385-26.584615-34.067692-63.054769 7.089231-16.620308 6.892308-21.819077 6.695385-21.937231-2.875077-1.890462-14.375385-1.536-31.035077 2.953846-30.404923 8.152615-75.500308 29.459692-97.398154 43.323077z"
                                fill="#505050" p-id="25764"></path>
                          </svg>
                          <svg v-else-if="item.type === taskType.FILE" t="1708653936999" class="icon"
                               viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                               p-id="2327" width="16" height="16">
                            <path
                                d="M731.648 626.176l67.584-67.584c28.672-31.232 67.584-46.08 107.008-46.08V327.68c0-22.528-9.728-44.544-26.112-59.904L643.072 47.104c-22.528-20.992-52.224-32.768-83.456-32.768H147.456c-45.056 0-81.92 36.864-81.92 81.92v831.488c0 45.056 36.864 81.92 81.92 81.92h346.112c-30.72-58.368-20.48-133.632 30.208-184.832l67.584-67.584"
                                fill="#0067B1" p-id="2328"></path>
                            <path
                                d="M530.432 147.968v192c0 19.456 12.8 32.256 32.256 32.256h204.8c0-0.512-237.056-224.256-237.056-224.256zM186.368 377.344h151.04c11.264 0 20.48 9.216 20.48 20.48v55.296c0 11.264-9.216 20.48-20.48 20.48H186.368c-11.264 0-20.48-9.216-20.48-20.48V397.824c0-11.264 9.216-20.48 20.48-20.48zM186.368 574.976h323.584c11.264 0 20.48 9.216 20.48 20.48v55.296c0 11.264-9.216 20.48-20.48 20.48H186.368c-11.264 0-20.48-9.216-20.48-20.48v-55.296c0-11.264 9.216-20.48 20.48-20.48z"
                                fill="#FFFFFF" p-id="2329"></path>
                            <path
                                d="M965.632 617.984c-48.128-45.056-124.928-48.128-169.472 0l-51.2 51.2c-12.8 12.8-12.8 38.4 0 51.2s35.328 12.8 51.2 0l51.2-51.2c19.456-19.456 48.128-19.456 67.072 0s19.456 48.128 0 67.072l-51.2 51.2c-12.8 12.8-12.8 38.4 0 51.2s38.4 12.8 51.2 0l51.2-51.2c47.616-48.128 47.616-124.928 0-169.472z"
                                fill="#FF7F00" p-id="2330"></path>
                            <path
                                d="M780.8 764.928l-38.4 38.4c-9.728 9.728-9.728 28.672 0 38.4s28.672 9.728 38.4 0l35.328-35.328c9.728-9.728 9.728-28.672 0-38.4-9.728-12.8-26.112-12.8-35.328-3.072z"
                                fill="#FF7F00" p-id="2331"></path>
                            <path
                                d="M757.248 886.784l-51.2 51.2c-19.456 19.456-48.128 19.456-67.072 0-18.944-19.456-19.456-48.128 0-67.072l51.2-51.2c12.8-12.8 12.8-38.4 0-51.2s-38.4-12.8-51.2 0l-51.2 51.2c-48.128 48.128-48.128 124.928 0 169.472s124.928 48.128 169.472 0l51.2-51.2c12.8-12.8 12.8-38.4 0-51.2s-34.816-12.8-51.2 0z"
                                fill="#FF7F00" p-id="2332"></path>
                          </svg>
                          <!-- 错误 -->
                          <svg v-else t="1708615346285" class="icon" viewBox="0 0 1024 1024" version="1.1"
                               xmlns="http://www.w3.org/2000/svg" p-id="4026" width="16" height="16">
                            <path
                                d="M512 0a512 512 0 0 0-512 512 512 512 0 0 0 512 512 512 512 0 0 0 512-512 512 512 0 0 0-512-512z"
                                fill="#FD6B6D" p-id="4027"></path>
                            <path
                                d="M513.755429 565.540571L359.277714 720.018286a39.058286 39.058286 0 0 1-55.296-0.073143 39.277714 39.277714 0 0 1 0.073143-55.442286l154.331429-154.331428-155.062857-155.136a36.571429 36.571429 0 0 1 51.712-51.785143l365.714285 365.714285a36.571429 36.571429 0 1 1-51.785143 51.785143L513.755429 565.540571z m157.549714-262.582857a35.254857 35.254857 0 1 1 49.737143 49.737143l-106.057143 108.982857a35.254857 35.254857 0 1 1-49.883429-49.810285l106.203429-108.982858z"
                                fill="#FFFFFF" p-id="4028"></path>
                          </svg>
                        </div>

                        <div class="ml-4">
                          <p class="text-sm font-medium dark:text-gray-400" style="word-wrap: break-word;">
                            {{ item.content }}</p>
                        </div>
                      </div>
                      <div>
                        <time class="text-sm text-gray-500 dark:text-gray-400" style="white-space: nowrap;">
                          {{ computedDateDiff(item.createTime) }}
                        </time>
                      </div>
                    </div>
                    <hr class="border-gray-200 dark:border-gray-600">
                  </div>
                </div>

              </div>

            </div>
          </div>

          <!-- 右侧底部输入框和按钮 -->
          <div class="flex items-center p-4 border-t border-gray-200">
            <input v-model="taskCommentValue" type="text" class="flex-grow border border-gray-300 p-2 mr-2"
                   placeholder="输入内容">
            <button @click="handleAddTaskComment" :disabled="disabledTaskComment"
                    class="bg-blue-500 text-white px-4 py-2 rounded">发送
            </button>
          </div>
        </div>
      </div>
      <div v-show="showContextMenu"
           class="absolute z-10 origin-top-right mt-1 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
           :style="{ top: dropdownTop + 'px', left: dropdownLeft + 'px' }">
        <div class="py-1" role="menu" aria-orientation="vertical" aria-labelledby="options-menu">
          <!-- 菜单项 -->
          <div class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900 cursor-pointer"
               @click="handleRemoveTaskComment" role="menuitem">删除该评论
          </div>
          <!-- 其他菜单项 -->
        </div>
      </div>
    </el-dialog>
    <!--    删除任务-->
    <el-dialog v-model="showRemoveTask" :close-on-click-modal="false" :close-on-press-escape="false" title="删除任务"
               width="450">
      <span>确定删除该任务吗？</span>
      <template #footer>
        <el-button type="primary" @click="confirmRemoveTask">确 定</el-button>
        <el-button @click="cancelRemoveTask">取 消</el-button>
      </template>
    </el-dialog>
    <!--    回收站-->
    <el-drawer
        v-model="showRecycleBin"
        title="🪣回收站"
        direction="rtl"
        size="40%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-tabs v-model="recycleBinActiveName" class="demo-tabs">
        <el-tab-pane label="任务" name="task">
          <el-table :data="recycleTaskList">
            <el-table-column label="任务名称" align="center" prop="name"></el-table-column>
            <el-table-column label="任务描述" align="center" prop="description">
              <template #default="scope">
                <span v-html="scope.row.description"></span>
              </template>
            </el-table-column>
            <el-table-column label="删除时间" align="center" prop="deleteTime">
              <template #default="scope">
                {{ parseTime(scope.row.deleteTime, '{yyyy}-{mm}-{dd} {h}:{i}:{s}') }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button link @click="handleRecoverTask(scope.row)" type="primary">
                  <template #icon>
                    <svg t="1708765993270" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="4335" width="16" height="16">
                      <path
                          d="M664.8 738.7c-50.9 37.7-114 55.1-176.9 48.7-2.9-0.3-5.8-0.8-8.8-1.2-5.6-0.8-11.3-1.6-16.8-2.7-3.4-0.7-6.7-1.5-10.1-2.4-5.4-1.2-10.7-2.6-16-4.3-2.5-0.8-5-1.7-7.5-2.6-14.6-5-28.7-11.3-42.1-18.9l-0.9-0.5c-29.6-17.1-55.8-39.6-77-66.4-1-1.3-2.1-2.8-3.1-4.2-37.8-49.6-58.3-110.3-58.1-172.6H309c1.7 0 3.2-0.9 4-2.4 0.8-1.5 0.7-3.3-0.2-4.7l-103.3-162c-0.8-1.3-2.2-2.1-3.7-2.1s-2.9 0.8-3.7 2.1l-103.3 162c-0.9 1.4-1 3.2-0.3 4.7 0.8 1.6 2.3 2.4 4 2.4h61.4c0 76.8 22.9 147.9 61.8 206.6 0.5 0.9 0.8 1.7 1.3 2.5 4.1 6 8.5 11.6 12.9 17.3 1.6 2.1 3.1 4.3 4.8 6.5 6.4 8.1 13.1 15.6 20.1 23.1l1.9 2c30.2 31.8 66.2 57.4 106.1 75.7l6.4 3c7.3 3.2 15 6 22.5 8.6 3.6 1.3 7.1 2.6 10.8 3.7 6.7 2.1 13.5 3.7 20.4 5.5 4.6 1.1 9.1 2.3 13.8 3.2 1.9 0.5 3.6 1 5.6 1.4 6.5 1.2 12.9 1.9 19.5 2.7l7 1c83.3 8.6 166.9-14.2 234.2-64.2 19-14.4 23.6-41.1 10.3-60.9-6.2-9.4-16-15.9-27.1-18-11-1.8-22.4 0.8-31.4 7.4z m196.9-226.9c0.1-73.2-21.2-144.9-61.4-206.1-0.6-1-1-2-1.6-2.9-4.9-7-9.9-13.9-15.3-20.6l-1.8-2.4c-34.5-44.1-79.2-79.2-130.2-102.3-1.4-0.6-2.8-1.3-4.2-2-8.1-3.4-16.2-6.5-24.5-9.4-3.1-1-6.1-2.2-9.2-3.1-7.2-2.2-14.5-4.2-21.9-5.9-4.1-0.9-8.2-2-12.3-2.9-2-0.4-3.9-1-5.9-1.5-5.5-1-11.1-1.4-16.6-2.1-3.7-0.5-7.6-1.1-11.4-1.6-9.2-0.9-18.4-1.3-27.6-1.5-1.7 0-3.4-0.3-5.1-0.3-72 0-142.1 23.1-200 65.9-19.1 14.3-23.6 41.1-10.4 60.9 6.2 9.4 16 15.9 27.1 18 11 2 22.4-0.6 31.3-7.3 51-37.7 114.2-55.1 177.3-48.7l7 1c6.4 0.7 12.6 1.7 18.7 3 2.7 0.5 5.4 1.2 8.1 1.9 6 1.4 12.1 3 18 4.8l5.5 2c6.8 2.3 13.3 4.7 19.9 7.6l2 1c39.1 17.7 73.4 44.6 99.9 78.3l0.5 0.7c39.2 50.1 60.4 111.9 60.2 175.5h-61.4c-1.7 0-3.2 0.9-4 2.4-0.8 1.5-0.7 3.3 0.3 4.7l103.4 162c0.8 1.3 2.2 2.1 3.7 2.1s2.9-0.8 3.7-2.1L926.8 519c0.1-0.1 0.2-0.3 0.3-0.4 2.2-2.8 0.1-6.9-3.5-6.8h-61.9z"
                          p-id="4336"></path>
                    </svg>
                  </template>
                  恢复
                </el-button>
                <el-button link @click="handleRealDeleteTask(scope.row)" type="danger">
                  <template #icon>
                    <svg t="1708766022884" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="6178" width="16" height="16">
                      <path
                          d="M792.96 933.12v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72h-47.36a46.72 46.72 0 0 1-46.72-46.72v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72h-46.72a46.72 46.72 0 0 1-46.72-46.72v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72H90.88a46.72 46.72 0 0 1-46.72-46.72V605.44a46.72 46.72 0 0 1 46.72-46.72h842.24a46.72 46.72 0 0 1 46.72 46.72v327.68a46.72 46.72 0 0 1-46.72 46.72h-93.44a46.72 46.72 0 0 1-46.72-46.72zM418.56 277.76V90.88a46.72 46.72 0 0 1 46.72-46.72h93.44a46.72 46.72 0 0 1 46.72 46.72v186.88a46.72 46.72 0 0 0 46.72 46.72h280.96a46.72 46.72 0 0 1 46.72 46.72v46.72a46.72 46.72 0 0 1-46.72 46.72H90.88a46.72 46.72 0 0 1-46.72-46.72v-46.08a46.72 46.72 0 0 1 46.72-46.72h280.96a46.72 46.72 0 0 0 46.72-47.36z"
                          fill="#5697FF" p-id="6179"></path>
                    </svg>
                  </template>
                  彻底删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="文件" name="file">
          <el-table :data="recycleFileList">
            <el-table-column label="文件名" prop="fileName" align="center"></el-table-column>
            <el-table-column label="文件大小" prop="fileSize" align="center"></el-table-column>
            <el-table-column label="删除时间" prop="deleteTime" align="center">
              <template #default="scope">
                {{ parseTime(scope.row.deleteTime, '{yyyy}-{mm}-{dd} {h}:{i}:{s}') }}
              </template>
            </el-table-column>
            <el-table-column label="关联任务" prop="taskName" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button link @click="handleRecoverFile(scope.row)" type="primary">
                  <template #icon>
                    <svg t="1708765993270" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="4335" width="16" height="16">
                      <path
                          d="M664.8 738.7c-50.9 37.7-114 55.1-176.9 48.7-2.9-0.3-5.8-0.8-8.8-1.2-5.6-0.8-11.3-1.6-16.8-2.7-3.4-0.7-6.7-1.5-10.1-2.4-5.4-1.2-10.7-2.6-16-4.3-2.5-0.8-5-1.7-7.5-2.6-14.6-5-28.7-11.3-42.1-18.9l-0.9-0.5c-29.6-17.1-55.8-39.6-77-66.4-1-1.3-2.1-2.8-3.1-4.2-37.8-49.6-58.3-110.3-58.1-172.6H309c1.7 0 3.2-0.9 4-2.4 0.8-1.5 0.7-3.3-0.2-4.7l-103.3-162c-0.8-1.3-2.2-2.1-3.7-2.1s-2.9 0.8-3.7 2.1l-103.3 162c-0.9 1.4-1 3.2-0.3 4.7 0.8 1.6 2.3 2.4 4 2.4h61.4c0 76.8 22.9 147.9 61.8 206.6 0.5 0.9 0.8 1.7 1.3 2.5 4.1 6 8.5 11.6 12.9 17.3 1.6 2.1 3.1 4.3 4.8 6.5 6.4 8.1 13.1 15.6 20.1 23.1l1.9 2c30.2 31.8 66.2 57.4 106.1 75.7l6.4 3c7.3 3.2 15 6 22.5 8.6 3.6 1.3 7.1 2.6 10.8 3.7 6.7 2.1 13.5 3.7 20.4 5.5 4.6 1.1 9.1 2.3 13.8 3.2 1.9 0.5 3.6 1 5.6 1.4 6.5 1.2 12.9 1.9 19.5 2.7l7 1c83.3 8.6 166.9-14.2 234.2-64.2 19-14.4 23.6-41.1 10.3-60.9-6.2-9.4-16-15.9-27.1-18-11-1.8-22.4 0.8-31.4 7.4z m196.9-226.9c0.1-73.2-21.2-144.9-61.4-206.1-0.6-1-1-2-1.6-2.9-4.9-7-9.9-13.9-15.3-20.6l-1.8-2.4c-34.5-44.1-79.2-79.2-130.2-102.3-1.4-0.6-2.8-1.3-4.2-2-8.1-3.4-16.2-6.5-24.5-9.4-3.1-1-6.1-2.2-9.2-3.1-7.2-2.2-14.5-4.2-21.9-5.9-4.1-0.9-8.2-2-12.3-2.9-2-0.4-3.9-1-5.9-1.5-5.5-1-11.1-1.4-16.6-2.1-3.7-0.5-7.6-1.1-11.4-1.6-9.2-0.9-18.4-1.3-27.6-1.5-1.7 0-3.4-0.3-5.1-0.3-72 0-142.1 23.1-200 65.9-19.1 14.3-23.6 41.1-10.4 60.9 6.2 9.4 16 15.9 27.1 18 11 2 22.4-0.6 31.3-7.3 51-37.7 114.2-55.1 177.3-48.7l7 1c6.4 0.7 12.6 1.7 18.7 3 2.7 0.5 5.4 1.2 8.1 1.9 6 1.4 12.1 3 18 4.8l5.5 2c6.8 2.3 13.3 4.7 19.9 7.6l2 1c39.1 17.7 73.4 44.6 99.9 78.3l0.5 0.7c39.2 50.1 60.4 111.9 60.2 175.5h-61.4c-1.7 0-3.2 0.9-4 2.4-0.8 1.5-0.7 3.3 0.3 4.7l103.4 162c0.8 1.3 2.2 2.1 3.7 2.1s2.9-0.8 3.7-2.1L926.8 519c0.1-0.1 0.2-0.3 0.3-0.4 2.2-2.8 0.1-6.9-3.5-6.8h-61.9z"
                          p-id="4336"></path>
                    </svg>
                  </template>
                  恢复
                </el-button>
                <el-button link @click="handleRealDeleteFile(scope.row)" type="danger">
                  <template #icon>
                    <svg t="1708766022884" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="6178" width="16" height="16">
                      <path
                          d="M792.96 933.12v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72h-47.36a46.72 46.72 0 0 1-46.72-46.72v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72h-46.72a46.72 46.72 0 0 1-46.72-46.72v-140.16a46.72 46.72 0 0 0-46.72-46.72 46.72 46.72 0 0 0-46.72 46.72v140.16a46.72 46.72 0 0 1-46.72 46.72H90.88a46.72 46.72 0 0 1-46.72-46.72V605.44a46.72 46.72 0 0 1 46.72-46.72h842.24a46.72 46.72 0 0 1 46.72 46.72v327.68a46.72 46.72 0 0 1-46.72 46.72h-93.44a46.72 46.72 0 0 1-46.72-46.72zM418.56 277.76V90.88a46.72 46.72 0 0 1 46.72-46.72h93.44a46.72 46.72 0 0 1 46.72 46.72v186.88a46.72 46.72 0 0 0 46.72 46.72h280.96a46.72 46.72 0 0 1 46.72 46.72v46.72a46.72 46.72 0 0 1-46.72 46.72H90.88a46.72 46.72 0 0 1-46.72-46.72v-46.08a46.72 46.72 0 0 1 46.72-46.72h280.96a46.72 46.72 0 0 0 46.72-47.36z"
                          fill="#5697FF" p-id="6179"></path>
                    </svg>
                  </template>
                  彻底删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>
    <!--  是否彻底删除任务  -->
    <el-dialog
        v-model="showRealDeleteTask"
        title="Tips"
        width="500"
    >
      <span>确认删除该任务及其子任务？
        <span style="color: red">注：该任务下的评论、文件等都会被一并删除</span>
      </span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="confirmRealDeleteTask" type="danger">
            确认
          </el-button>
          <el-button @click="cancelRealDeleteTask">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--  是否彻底删除文件  -->
    <el-dialog
        v-model="showRealDeleteFile"
        title="Tips"
        width="500"
    >
      <span>确认彻底删除该关联文件？
      </span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="confirmRealDeleteFile" type="danger">
            确认
          </el-button>
          <el-button @click="cancelRealDeleteFile">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--  文件关联列表  -->
    <el-drawer v-model="showFileDrawer" size="40%" :close-on-click-modal="false" :close-on-press-escape="false"
               title="文件列表">

      <div class="bg-white p-8">
        <div class="mb-6 flex items-center justify-between">
          <h1 class="text-xl font-semibold">文件列表</h1>
        </div>
        <div class="space-y-4">
          <div v-for="item in fileList" class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  class="text-gray-400"
              >
                <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" x2="8" y1="13" y2="13"></line>
                <line x1="16" x2="8" y1="17" y2="17"></line>
                <line x1="10" x2="8" y1="9" y2="9"></line>
              </svg>
              <div>
                <div class="text-sm font-medium">{{ item.fileName }}</div>
                <div class="text-xs text-gray-500">{{ item.fileSizeStr }}</div>
              </div>
            </div>
            <div class="flex items-center space-x-4">
              <div class="text-xs text-gray-500">
                <el-tooltip
                    effect="dark"
                    content="上传时间"
                    placement="top">
                  {{ computedDateDiff(item.createTime) }}
                </el-tooltip>
              </div>
              <div
                  class="inline-flex items-center rounded-full whitespace-nowrap border px-2.5 py-0.5 w-fit text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 border-transparent bg-secondary text-secondary-foreground hover:bg-secondary/80">
                <el-tooltip
                    effect="dark"
                    content="创建人"
                    placement="top">
                  {{ item.createBy }}
                </el-tooltip>
              </div>
              <el-tooltip
                  effect="dark"
                  content="下载"
                  placement="top">
                <svg
                    @click="downloadFile(item)"
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="text-gray-400"
                    style="cursor: pointer"
                >
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                  <polyline points="7 10 12 15 17 10"></polyline>
                  <line x1="12" x2="12" y1="15" y2="3"></line>
                </svg>
              </el-tooltip>
              <el-dropdown trigger="click">
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="text-gray-400"
                >
                  <circle cx="12" cy="12" r="1"></circle>
                  <circle cx="12" cy="5" r="1"></circle>
                  <circle cx="12" cy="19" r="1"></circle>
                </svg>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleCopyTaskFilePath(item)">📋 复制文件链接</el-dropdown-item>
                    <el-dropdown-item @click="handleRenameFile(item)">✏️ 修改文件名</el-dropdown-item>
                    <el-dropdown-item @click="handleDeleteFile(item)">🗑️ 移入回收站</el-dropdown-item>
                  </el-dropdown-menu>

                </template>
              </el-dropdown>

            </div>
          </div>
        </div>
      </div>
    </el-drawer>
    <!--   修改文件名 -->
    <el-dialog
        v-model="showRenameFileDiaLog"
        title="修改文件名"
        width="500"
    >
      <el-input v-model="renameFileForm.fileName" placeholder="请输入文件名"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmRenameFile">
            确认
          </el-button>
          <el-button @click="cancelRenameFile">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--   删除文件 -->
    <el-dialog
        v-model="showDeleteFileDiaLog"
        title="移入回收站"
        width="500"
    >
      <span>确认将该文件移入回收站</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="confirmDeleteFile">
            确认
          </el-button>
          <el-button @click="cancelDeleteFile">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <!--  日志 -->
    <el-drawer title="项目员工日志" v-model="showReport" size="40%" :close-on-click-modal="false"
               :close-on-press-escape="false">
      <div class=" ml-6 mr-6 mb-6">
        <div class="text-gray-500 mb-3">注：默认显示从现在到昨天0点的日志。</div>
        <div class="mb-3">
          <span>成员：</span>
          <el-select v-model="reportQueryForm.userId" clearable @change="handleGetReportInTask">
            <el-option
                v-for="item in pjUserOptions"
                :key="item.userId"
                :label="item.nickName"
                :value="item.userId"
            />
          </el-select>
        </div>
        <div >
          <span>日期范围：</span>
          <el-date-picker
              v-model="timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="shortcuts"
              @change="handleTimeRange"
          ></el-date-picker>
        </div>
      </div>
      <!-- component -->
      <div
          v-for="item in reportList"
          class="relative block p-8 overflow-hidden border bg-gray-100 border-slate-100 rounded-lg ml-6 mr-6 "
      >
          <span class="absolute inset-x-0 bottom-0 h-2 bg-gradient-to-r from-green-300 via-blue-500 to-purple-600">

          </span>

        <div class="justify-between sm:flex">
          <div>
            <h5 class="text-xl font-bold text-slate-900">
              {{ item.name }}
            </h5>
            <p class="mt-1 text-xs font-medium text-slate-600">创建人： {{ item.sysUser.nickName }}</p>
          </div>

          <div class="flex-shrink-0 hidden ml-3 sm:block">
            <image-preview :src="item.sysUser.avatar" style="width: 60px;height: 60px"/>
          </div>
        </div>

        <div class="mt-4 sm:pr-8">
          <p class="text-sm text-slate-500" v-html="item.description">
          </p>
        </div>

        <dl class="flex mt-6">
          <div class="flex flex-col-reverse">
            <dt class="text-sm font-medium text-slate-600">发布时间</dt>
            <dd class="text-xs text-slate-500">
                <span v-if="item.updateTime !== null">
                  {{ parseTime(item.updateTime) }}
                </span>
              <span v-else>
                  {{ parseTime(item.createTime) }}
                  </span>
            </dd>
          </div>

          <!--            <div class="flex flex-col-reverse ml-3 sm:ml-6">-->
          <!--              <dt class="text-sm font-medium text-slate-600">Reading time</dt>-->
          <!--              <dd class="text-xs text-slate-500">5 minutes</dd>-->
          <!--            </div>-->
        </dl>
      </div>

    </el-drawer>
  </div>
</template>
<style scoped>

* {
  font-family: "Poppins", sans-serif;
  box-sizing: border-box;
}

.my-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.buttons-container {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.demo-tabs:deep( .el-tabs__item) {
  width: 360px;
}

.task-right {

}

/* 样式优化 */
.message-list {
  flex-grow: 1;
  overflow-y: auto;
}

.message-item {
  padding: 1rem;
  border-bottom: 1px solid #E5E7EB; /* 使用十六进制颜色来定义边框颜色 */
}

.message-item:hover {
  background-color: #F3F4F6; /* 使用十六进制颜色来定义鼠标悬停时的背景色 */
}

.message-item .flex-shrink-0 {
  width: 2.5rem; /* 调整头像的大小 */
  height: 2.5rem;
  border-radius: 9999px; /* 使用一个足够大的值来实现圆形 */
  background-color: #D1D5DB; /* 使用十六进制颜色来定义头像的背景色 */
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold; /* 加粗字体以使头像中的文字更清晰 */
}

.message-item .font-semibold {
  font-weight: bold; /* 加粗字体以突出显示发送者的名称 */
}

.message-item .text-xs {
  font-size: 0.75rem; /* 调整时间戳的字体大小 */
  color: #6B7280; /* 使用十六进制颜色来定义时间戳的颜色 */
}

.cell-item {
  display: flex;
  align-items: center;
}

.app-task {
  background-color: #ebf0f7;
  min-width: 100%;
  height: 100vh;
  display: flex; /* 使用 flexbox 布局 */
}

.task-item {
  display: flex;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 20px;
}

.left-box {
  margin-right: 10px;
}

.task-name-not-finish {
  margin-bottom: 2px;
  flex: 1; /* 中间部分填充剩余空间 */
}

.task-name-finish {
  margin-bottom: 2px;
  flex: 1; /* 中间部分填充剩余空间 */
  text-decoration: line-through;
  color: gray; /* 设置字体颜色为灰色 */
}

.right-arrow {
  margin-left: 10px;
}

h1 {
  font-size: 30px;
  color: #2e2e2f;
}


.project {
  padding-left: 20px;
  width: 80%;
  display: inline-block;
}

.project-info {
  padding: 1.5rem 0;
  display: flex;
  width: 100%;
  justify-content: space-between;
  align-items: center;
}

.project-participants {
  display: flex;
  align-items: center;
}

.project-participants_item {
  width: 30px;
  height: 30px;
  display: inline-flex; /* 修改为 inline-flex */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  background: transparent;
  border-radius: 100rem;
  margin: 0 0.2rem;
  border: 1px dashed #969696;
  cursor: pointer;
  position: relative;
  content: "+";
  font-size: 15px;
  color: #969696;
}

.project-participants span,
.project-participants__add {
  width: 30px;
  height: 30px;
  display: inline-flex; /* 修改为 inline-flex */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  background: #7784ee;
  border-radius: 100rem;
  margin: 0 0.2rem;
}

.project-participants__add {
  background: transparent;
  border: 1px dashed #969696;
  font-size: 0;
  cursor: pointer;
  position: relative;
}

.project-participants__add:after {
  content: "+";
  font-size: 15px;
  color: #969696;
}

.project-tasks {
  display: flex;
  overflow-x: auto;
  width: calc(100% + 1.5rem); /* 加上水平滚动条的宽度 */
  margin-bottom: -1.5rem; /* 补偿滚动条高度 */
}


/* 鼠标悬停在滑块上时 */
.project-tasks::-webkit-scrollbar-thumb:hover {
  background-color: #555; /* 设置悬停时滑块颜色 */
}

.project-column {
  flex-shrink: 0; /* 防止项目列缩小 */
  //min-width: 300px; /* 最小宽度 */
  overflow-y: hidden;
  width: 20%;
  margin-right: 1.5rem; /* 添加列之 间的间距 */
  height: 785px;
  padding-right: 15px; /* 设置左边内边距为 20px */
  padding-bottom: 30px; /* 底部内边距增加 */
}

.project-column .task-container::-webkit-scrollbar-thumb {
  background-color: #7784ee; /* 设置滑块颜色 */
}

.project-column .task-container {
  overflow-y: auto;
  height: 100%;
  padding-right: 10px;
}

.project-column-heading {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.project-column-heading__title {
  font-size: 20px;

}

.project-column-heading__options {
  background: transparent;
  color: #c4cad3;
  font-size: 18px;
  border: 0;
  cursor: pointer;
}

.task {
  cursor: auto;
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  width: 100%;
  box-shadow: rgba(99, 99, 99, 0.1) 0px 2px 8px 0px;
  margin-bottom: 1rem;
  border: 3px dashed transparent;
  //pointer-events: none; /* 取消拖动 */
}

.task:hover {
  box-shadow: rgba(99, 99, 99, 0.3) 0px 2px 8px 0px;
  border-color: rgba(162, 179, 207, 0.2) !important;
}

.task p {
  font-size: 15px;
  margin: 1.2rem 0;
}

.task__tag {
  border-radius: 100px;
  padding: 2px 13px;
  font-size: 12px;
}

.task__tag--copyright {
  color: #a734ba;
  background-color: #f2dcf5;
}

.task__tag--design {
  color: #2d86ba;
  background-color: #ceecfd;
}

.task__tag--illustration {
  color: #13854e;
  background-color: #d6ede2;
}

.task__tags {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.task__options {
  background: transparent;
  border: 0;
  color: #c4cad3;
  font-size: 17px;
}

.task__stats {
  position: relative;
  width: 100%;
  color: #c4cad3;
  font-size: 12px;
}

.task__stats span:not(:last-of-type) {
  margin-right: 1rem;
}

.task__stats svg {
  margin-right: 5px;
}

.task__owner {
  width: 25px;
  height: 25px;
  border-radius: 100rem;
  background: #7784ee;
  position: absolute;
  display: inline-block;
  right: 0;
  bottom: 0;
}

.task-hover {
  border: 3px dashed #c4cad3 !important;
}

.task-details {
  width: 20%;
  border-left: 1px solid #d9e0e9;
  display: inline-block;
  height: 100%;
  vertical-align: top;
  padding: 2.5rem 2rem;
}

.task-progress {
  position: relative;
  height: 250px; /* 设置一个固定的高度，使得内容区域可以滚动 */
}

.task-progress .task-progress-tag::-webkit-scrollbar-thumb {
  background-color: #7784ee; /* 设置滑块颜色 */
}

.task-progress .task-progress-tag {
  overflow-y: auto;
  height: 100%;
}

.task-progress .tag-progress {
  //overflow-y: hidden; /* 允许内容区域垂直滚动 */
  margin-right: 10px;
}

.task-progress h2 {
  font-size: 18px;
  font-weight: bold;
}

.tag-progress p {
  display: flex;
  width: 100%;
  justify-content: space-between;
}

.tag-progress p span {
  color: #b4b4b4;
}

.tag-progress .progress {
  width: 100%;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  border: none;
  border-radius: 10px;
  height: 10px;
}

.tag-progress .progress::-webkit-progress-bar, .tag-progress .progress::-webkit-progress-value {
  border-radius: 10px;
}

.tag-progress .progress--copyright::-webkit-progress-bar {
  background-color: #ecd8e6;
}

.tag-progress .progress--copyright::-webkit-progress-value {
  background: #d459e8;
}

.tag-progress .progress--illustration::-webkit-progress-bar {
  background-color: #dee7e3;
}

.tag-progress .progress--illustration::-webkit-progress-value {
  background-color: #46bd84;
}

.tag-progress .progress--design::-webkit-progress-bar {
  background-color: #d8e7f4;
}

.tag-progress .progress--design::-webkit-progress-value {
  background-color: #08a0f7;
}

.task-activity {
  position: relative;
  margin-top: 30px;
  height: 550px; /* 设置一个固定的高度，使得内容区域可以滚动 */
}

.task-activity .task-activity-li::-webkit-scrollbar-thumb {
  background-color: #7784ee; /* 设置滑块颜色 */
}

.task-activity .task-activity-li {
  overflow-y: auto;
  height: 100%;
}

.task-activity h2 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 1rem;
}

.task-activity ul {
  margin: 0; /* 去除默认的外边距 */
  padding: 0; /* 去除默认的内边距 */
}

.task-activity li {
  list-style: none;
  margin: 1rem 0;
  padding: 0rem 1rem 1rem 3rem;
  position: relative;
}

.task-activity time {
  display: block;
  color: #c4cad3;
}

.task-icon {
  width: 30px;
  height: 30px;
  border-radius: 100rem;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 5px;
}

.task-icon svg {
  /* 增大 SVG 图标的大小 */
  width: 20px; /* 调整宽度 */
  height: 20px; /* 调整高度 */
  color: #ffffff;
}

.task-icon--attachment {
  background-color: #fba63c;
}

.task-icon--comment {
  background-color: #5dc983;
}

.task-icon--edit {
  background-color: #7784ee;
}

@media only screen and (max-width: 1300px) {
  .project {
    max-width: 100%;
  }

  .task-details {
    width: 100%;
    display: flex;
  }

  .tag-progress,
  .task-activity {
    flex-basis: 50%;
    background: #ffffff;
    padding: 1rem;
    border-radius: 8px;
    margin: 1rem;
  }
}

@media only screen and (max-width: 1000px) {
  .project-column:nth-child(2),
  .project-column:nth-child(3) {
    display: none;
  }

  .project-tasks {
    grid-template-columns: 1fr 1fr;
  }
}

@media only screen and (max-width: 600px) {
  .project-column:nth-child(4) {
    display: none;
  }

  .project-tasks {
    grid-template-columns: 1fr;
  }

  .task-details {
    flex-wrap: wrap;
    padding: 3rem 1rem;
  }

  .tag-progress,
  .task-activity {
    flex-basis: 100%;
  }

  h1 {
    font-size: 25px;
  }
}

</style>