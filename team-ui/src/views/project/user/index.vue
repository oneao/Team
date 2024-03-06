<script setup>
import {ref, reactive, computed, watch} from 'vue';
import {
  queryRoleSe,
  queryRoleSeAll,
  addProjectRoleSe,
  updateProjectRoleSe,
  queryUserList,
  addProjectUser,
  updateProjectUser,
  removeProjectUser,
  list
} from "@/api/project/user.js";
import {getPostOptionSelect} from "@/api/system/post.js";
import router from "@/router/index.js";

const {proxy} = getCurrentInstance();

import {listProjectAll} from "@/api/project/project";
import {Avatar, CoffeeCup, Iphone, Message, School, Sugar, User, UserFilled} from "@element-plus/icons-vue";

const data = reactive({
  chooseProjectForm: {
    currentProjectId: null,
    currentProjectName: ''
  },
  chooseProjectRules: {
    currentProjectId: [
      {required: true, message: '请选择项目', trigger: 'change'}
    ]
  },
  projectRoleSeForm: {
    userId: null
  },
  projectRoleSeRules: {
    userId: [
      {required: true, message: '请选择项目经理', trigger: 'change'}
    ]
  },
  queryUserParams: {
    pNum: 1,
    pSize: 10,
    nickName: '',
    projectId: 16,
    deptId: null,
    postId: null,
    inProject: null
  }
})

const {chooseProjectForm, chooseProjectRules, projectRoleSeForm, projectRoleSeRules, queryUserParams} = toRefs(data)
//初始化选择项目
const projectList = ref([]);// 项目列表
const showChooseProject = ref(false)// 是否显示选择项目弹窗
const showNotHasProject = ref(false);// 是否显示没有项目的提示
const showStep = ref(false);// 是否显示步骤条
// 步骤条
const currentStep = ref(1);

// 获取项目列表
function getProjectList() {
  listProjectAll().then(res => {
    if (res.data.length > 0) {
      showChooseProject.value = true
      projectList.value = res.data
      chooseProjectForm.value.currentProjectId = projectList.value[0].id
    } else {
      showNotHasProject.value = true
    }
  })
}

// 确定选择项目
function confirmChooseProject() {
  proxy.$refs["chooseProjectRef"].validate((valid) => {
    if (valid) {
      chooseProjectForm.value.currentProjectName = projectList.value.find(item => item.id === chooseProjectForm.value.currentProjectId).name
      showChooseProject.value = false
      showProjectRoleSe.value = false
      showStep.value = true
      currentStep.value = 1
      getProjectRoleSe() // 获取该项目下项目经理的信息
    } else {
      return false;
    }
  });
}

// =============================================项目经理===========================================================
const disabledUpdateProjectRoleSe = ref(true)
let oldProjectRoleSe = {
  id: null,
  projectStatus: null
}
const projectRoleSe = ref({})
const projectRoleSeList = ref([])// 项目经理列表
const showProjectRoleSe = ref(false)

// 获取岗位为“项目经理”的信息
function getProjectRoleSe() {
  queryRoleSe(chooseProjectForm.value.currentProjectId).then(res => {
    projectRoleSe.value = res.data
    if (projectRoleSe.value.id === null) {
      // showProjectRoleSe.value = true
      queryRoleSeAll().then(res => {
        projectRoleSeList.value = res.data
      })
    } else {
      showProjectRoleSe.value = true
      projectRoleSe.value = res.data

      queryRoleSeAll().then(res => {
        projectRoleSeList.value = res.data
        projectRoleSeList.value = projectRoleSeList.value.filter(item => item.id !== projectRoleSe.value.id)
      })
      oldProjectRoleSe.id = projectRoleSe.value.id
      oldProjectRoleSe.projectStatus = projectRoleSe.value.projectStatus
    }
  })
}

// 保存信息
function submitUpdateProjectRole() {
  const params = {
    projectId: chooseProjectForm.value.currentProjectId,
    userId: projectRoleSeForm.value.userId,
    projectStatus: projectRoleSe.value.projectStatus
  }
  updateProjectRoleSe(params).then(res => {
    disabledUpdateProjectRoleSe.value = true
    proxy.$message.success('更新项目经理成功')
  })
}

// 保存“项目经理”的信息。
function submitAddProjectRoleSe() {
  proxy.$refs["projectRoleSeRef"].validate((valid) => {
    if (valid) {
      const params = {
        projectId: chooseProjectForm.value.currentProjectId,
        userId: projectRoleSeForm.value.userId
      };
      addProjectRoleSe(params).then(res => {
        projectRoleSe.value = projectRoleSeList.value.find(item => item.id === projectRoleSeForm.value.userId)
        showProjectRoleSe.value = true
        proxy.$message.success('保存成功')
      })
    } else {
      return false;
    }
  });
}

// 监听项目经理的选择
watch(() => projectRoleSeForm.value.userId, (newValue, oldValue) => {
  if (newValue === null) {
    return;
  }
  if (projectRoleSe.value.id === newValue) {
    return
  }
  projectRoleSe.value = projectRoleSeList.value.find(item => item.id === newValue)
  if (projectRoleSe.value.projectStatus === null) {
    projectRoleSe.value.projectStatus = 0
  }
  disabledUpdateProjectRoleSe.value = false
});
// 监听项目经理的状态
watch(() => projectRoleSe.value.projectStatus, (newValue, oldValue) => {
  if (oldValue === undefined) {
    return;
  }
  if (oldProjectRoleSe.id === projectRoleSe.value.id && oldProjectRoleSe.projectStatus === newValue) {
    disabledUpdateProjectRoleSe.value = true
    return;
  }
  disabledUpdateProjectRoleSe.value = false
});
//================================================项目成员相关================================================
import {deptTreeSelect} from "@/api/system/user";
import ImagePreview from "@/components/ImagePreview/index.vue";

const userFormLoading = ref(false)
const deptName = ref(""); // 部门名称
const deptOptions = ref(undefined);
const userList = ref([]) // 用户列表
const userListTotal = ref(0) // 用户列表总数
const postOptions = ref([]) // 岗位列表

const userIds = ref([]) // 选中的用户id
const showRemoveProjectUser = ref(false)
const isInProjectOptions = ref([
  {label: '全部', value: -1},
  {label: '项目成员', value: 1},
  {label: '非项目成员', value: 2}
])
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};
/** 根据名称筛选部门树 */
watch(deptName, val => {
  proxy.$refs["deptTreeRef"].filter(val);
});

/** 查询部门下拉树结构 */
function getDeptTree() {
  deptTreeSelect().then(response => {
    deptOptions.value = response.data;
  });
}

function handleNodeClick(data) {
  queryUserParams.value.deptId = data.id;
  handleQueryUser();
}

function handleQueryUser() {
  queryUserParams.value.pageNum = 1;
  getUserList();
}

function getUserList() {
  userFormLoading.value = true
  queryUserParams.value.projectId = chooseProjectForm.value.currentProjectId
  queryUserList(queryUserParams.value).then(response => {
    userList.value = response.data.rows
    userListTotal.value = response.data.total
    userFormLoading.value = false
  })
}

function resetUserList() {
  queryUserParams.value.nickName = '';
  queryUserParams.value.isInProject = null;
  proxy.resetForm('queryUserRef')
  proxy.$refs.deptTreeRef.setCurrentKey(null)
  getUserList();
}

// 添加项目用户
function handleAddProjectUser(row) {
  const obj = {
    projectId: chooseProjectForm.value.currentProjectId,
    userId: row.userId
  }
  addProjectUser(obj).then(res => {
    proxy.$message.success('添加成功')
    getUserList()
  })
}

// 更新项目用户状态
function handleUpdateProjectUser(row) {
  const obj = {
    projectId: chooseProjectForm.value.currentProjectId,
    userId: row.userId,
    projectStatus: row.projectStatus === 0 ? 1 : 0
  }
  updateProjectUser(obj).then(res => {
    proxy.$message.success('更新成功')
    getUserList()
  })
}

function handleSelectionUserListChange(selection) {
  userIds.value = selection.map(item => item.userId);
}

// 删除项目用户
function handleRemoveProjectUserOne(row) {
  showRemoveProjectUser.value = true
  userIds.value = [row.userId]
}

function handleRemoveProjectUserList() {
  let executeShowRemove = true; // 声明一个变量表示是否执行 showRemoveProjectUser.value = true

  userList.value.forEach(item => {
    if (userIds.value.includes(item.userId) && item.isInProject === 0 && executeShowRemove) {
      proxy.$message.error('未加入项目的用户不能删除');
      executeShowRemove = false; // 如果条件成立，将变量设置为false，表示不执行 showRemoveProjectUser.value = true
    }
  });

  if (executeShowRemove) {
    showRemoveProjectUser.value = true; // 只有当 executeShowRemove 为 true 时才执行该行
  }
}

function confirmRemoveProjectUserList() {
  const obj = {
    projectId: chooseProjectForm.value.currentProjectId,
    ids: userIds.value
  }
  removeProjectUser(obj).then(res => {
    proxy.$message.success('删除成功')
    showRemoveProjectUser.value = false
    getUserList()
  })
}

function cancelRemoveProjectUserList() {
  showRemoveProjectUser.value = false
}

//================================================项目所有成员相关================================================
const userAllList = ref([]) // 项目所有成员列表
const userAllListTotal = ref(0)

// 获取项目所有成员
function getProjectUserAll() {
  list({projectId: chooseProjectForm.value.currentProjectId}).then(res => {
    userAllList.value = res.data
    userAllListTotal.value = userAllList.value.length
  })
}

//================================================公共方法================================================

// 监听步骤条

watch(currentStep, (val) => {
  // 项目经理
  if (val === 1) {
    getProjectRoleSe()
  } else if (val === 2) {
    postOptions.value = []
    getPostOptionSelect().then(res => {
      res.data.forEach(item => {
        postOptions.value.push({label: item.postName, value: item.postId})
      })
    })
    getDeptTree()
    getUserList()
  } else if (val === 3) {
    getProjectUserAll()
  }
})

// 取消
function cancel() {
  if (currentStep.value === 1 && !showProjectRoleSe.value) {
    // 保存项目经理的信息
    showProjectRoleSe.value = false
  } else if (currentStep.value === 1 && showProjectRoleSe.value) {
    // 更新项目经理的信息
    projectRoleSeForm.value.userId = null
    getProjectRoleSe()
    disabledUpdateProjectRoleSe.value = true
  } else if (currentStep.value === 2) {
    // 重置用户列表
    resetUserList()
  }
}

const currentProjectId = ref(null)

// 初始化方法
function init() {
  currentProjectId.value = router.currentRoute.value.query.projectId
  if (currentProjectId.value !== undefined) {
    listProjectAll().then(res => {
      projectList.value = res.data
    })
    setTimeout(() => {
      chooseProjectForm.value.currentProjectId = Number(currentProjectId.value)
      chooseProjectForm.value.currentProjectName = router.currentRoute.value.query.projectName
      showStep.value = true
      currentStep.value = 1
      getProjectRoleSe()
    },1000)
  } else {
    getProjectList()
  }
}

function handleToAddProject() {
  router.push({path: '/project/project'})
}

init();
</script>

<template>
  <div class="app-container">
    <!--  是否有项目：无则跳转  -->
    <el-dialog
        v-model="showNotHasProject"
        title="消息提示"
        width="500"
        :show-close="false"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <span style="font-size: 16px">tips：当前暂无项目，请先添加项目。</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleToAddProject">
            跳转添加项目
          </el-button>
        </div>
      </template>
    </el-dialog>
    <!--  选择项目  -->
    <el-dialog
        v-model="showChooseProject"
        title="选择项目"
        width="500"
        :show-close="false"
        :close-on-click-modal="false"
        :close-on-press-escape="false">
      <el-form :model="chooseProjectForm" :rules="chooseProjectRules" ref="chooseProjectRef" label-width="80px">
        <el-form-item label="项目名称" prop="currentProjectId">
          <el-select v-model="chooseProjectForm.currentProjectId" value-key="id" placeholder="请选择项目">
            <el-option
                v-for="item in projectList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmChooseProject">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    <!--  切换项目  -->
    <div style="text-align: center;  padding: 10px;" v-if="showStep">
      <span style="display: inline-block;">
          当前项目为：{{ chooseProjectForm.currentProjectName }}
      </span>
      <el-button type="danger" @click="showChooseProject = true" style="margin-bottom: 3px;margin-left: 20px">
        切换项目
      </el-button>
    </div>
    <!--  步骤条  -->
    <el-steps v-if="showStep" :active="currentStep" align-center finish-status="success"
              style="margin-top: 20px;margin-bottom: 40px">
      <el-step title="项目经理" description="项目经理管理" :icon="User"></el-step>
      <el-step title="普通成员" description="普通成员管理" :icon="UserFilled"/>
      <el-step title="项目所有成员" description="项目成员展示" :icon="Avatar"/>
    </el-steps>
    <!--  步骤条1内容  -->
    <div v-if="showStep && currentStep === 1" style="margin: 20px" class="step-content">
      <div style="display: flex;justify-content: center;">
        <div v-if="showProjectRoleSe">
          <el-descriptions title="当前项目经理信息" border size="large" :column="2">
            <template #extra>
              <el-text type="danger">更改项目经理：</el-text>
              <el-select v-model="projectRoleSeForm.userId" value-key="id" placeholder="请选择项目经理">
                <el-option
                    v-for="item in projectRoleSeList"
                    :key="item.id"
                    :label="item.nickName"
                    :value="item.id">
                </el-option>
              </el-select>
            </template>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <Avatar/>
                </el-icon>
                头像
              </template>
              <el-avatar :src="projectRoleSe.avatar" shape="square" size="large"></el-avatar>
            </el-descriptions-item>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <User/>
                </el-icon>
                姓名
              </template>
              {{ projectRoleSe.nickName }}
            </el-descriptions-item>
            <el-descriptions-item align="center" min-width="250px">
              <template #label>
                <el-icon>
                  <School/>
                </el-icon>
                部门
              </template>
              {{ projectRoleSe.deptName }}
            </el-descriptions-item>
            <el-descriptions-item align="center" min-width="250px">
              <template #label>
                <el-icon>
                  <Iphone/>
                </el-icon>
                手机号
              </template>
              {{ projectRoleSe.phone }}
            </el-descriptions-item>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <Message/>
                </el-icon>
                邮箱
              </template>
              {{ projectRoleSe.email }}
            </el-descriptions-item>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <CoffeeCup/>
                </el-icon>
                性别
              </template>
              <el-tag v-if="projectRoleSe.sex === '1'">男</el-tag>
              <el-tag v-else>女</el-tag>
            </el-descriptions-item>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <Sugar/>
                </el-icon>
                账号状态
              </template>
              <el-tag v-if="projectRoleSe.accountStatus === 0" type="success">正常</el-tag>
              <el-tag v-else type="danger">禁用</el-tag>
            </el-descriptions-item>
            <el-descriptions-item align="center">
              <template #label>
                <el-icon>
                  <Sugar/>
                </el-icon>
                项目账号状态
              </template>
              <el-switch v-model="projectRoleSe.projectStatus" :active-value=0 :inactive-value=1 active-text="正常"
                         inactive-text="禁用">
              </el-switch>
              <br/>
              <el-text>更换项目经理时，默认为激活状态。</el-text>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        <div v-else>
          <el-result icon="warning" title="提醒" sub-title="当前项目尚未安排项目经理，请安排项目经理。">
            <template #extra>
              <el-form :model="projectRoleSeForm" :rules="projectRoleSeRules" ref="projectRoleSeRef" label-width="80px">
                <el-form-item label="项目经理" prop="userId">
                  <el-select v-model="projectRoleSeForm.userId" value-key="id" placeholder="请选择项目经理">
                    <el-option
                        v-for="item in projectRoleSeList"
                        :key="item.id"
                        :label="item.nickName"
                        :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </template>
          </el-result>
        </div>
      </div>
    </div>
    <!--  步骤条2内容  -->
    <div v-if="showStep && currentStep === 2" style="width: 90%;margin: auto" class="step-content">
      <el-row :gutter="20">
        <el-col :span="4" :xs="24">
          <div class="head-container">
            <el-input
                v-model="deptName"
                placeholder="请输入部门名称"
                clearable
                prefix-icon="Search"
                style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
                :data="deptOptions"
                :props="{ label: 'label', children: 'children' }"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="deptTreeRef"
                node-key="id"
                highlight-current
                default-expand-all
                @node-click="handleNodeClick"
            />
          </div>
        </el-col>
        <el-col :span="20" :xs="24">
          <el-form :model="queryUserParams" ref="queryUserRef" :inline="true" label-width="68px">
            <el-form-item label="用户名称" prop="nickName">
              <el-input
                  v-model="queryUserParams.nickName"
                  placeholder="请输入用户名称"
                  clearable
              />
            </el-form-item>
            <el-form-item label="是否在项目中" prop="isInProject" label-width="100px">
              <el-select v-model="queryUserParams.inProject" placeholder="请选择">
                <el-option
                    v-for="item in isInProjectOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="岗位" prop="postId">
              <el-select v-model="queryUserParams.postId" placeholder="请选择">
                <el-option
                    v-for="item in postOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="getUserList">搜索</el-button>
              <el-button icon="Refresh" @click="resetUserList">重置</el-button>
            </el-form-item>
          </el-form>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                  type="primary"
                  :disabled="userIds.length < 1"
                  plain
                  icon="Remove"
                  @click="handleRemoveProjectUserList"
                  v-hasPermi="['project:user:removeProjectUser']"
              >删除
              </el-button>
            </el-col>
          </el-row>
          <el-table v-loading="userFormLoading" :data="userList" stripe
                    @selection-change="handleSelectionUserListChange">
            <el-table-column type="selection" width="50" align="center"></el-table-column>
            <el-table-column label="用户名称" align="center" key="nickName" prop="nickName"
                             width="120px"></el-table-column>
            <el-table-column label="邮箱" align="center" key="email" prop="email" width="160px"></el-table-column>
            <el-table-column label="手机号" align="center" key="phoneNumber" prop="phoneNumber"
                             width="160px"></el-table-column>
            <el-table-column label="岗位" align="center" key="postName" prop="postName" width="200px">
              <template #default="scope">
                <el-tag v-for="item in scope.row.postName.split(',')" style="margin-top:5px;margin-left: 5px">{{
                    item
                  }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="性别" align="center" key="sex" prop="sex" width="50">
              <template #default="scope">
                <el-tag v-if="scope.row.sex === 0">女</el-tag>
                <el-tag v-else-if="scope.row.sex === 1">男</el-tag>
                <el-tag v-else>暂未设置</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="账号状态" align="center" key="accountStatus" prop="accountStatus" width="100px">
              <template #default="scope">
                <el-tag v-if="scope.row.accountStatus === 0">正常</el-tag>
                <el-tag v-else type="danger">禁用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="邀请人" align="center" key="createBy" prop="createBy" width="120px">
              <template #default="scope">
                <el-tag v-if="scope.row.createBy !== null">
                  {{ scope.row.createBy }}
                </el-tag>
                <el-tag v-else type="danger">
                  未加入项目
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="项目状态" align="center" key="projectStatus" prop="projectStatus" width="100px">
              <template #default="scope">
                <el-tag v-if="scope.row.projectStatus === 0">正常</el-tag>
                <el-tag v-else-if="scope.row.projectStatus === 1" type="danger">禁用</el-tag>
                <el-tag v-else type="danger">未加入</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <div v-if="scope.row.userId !== 1">
                  <el-button v-if="scope.row.isInProject === 0" @click="handleAddProjectUser(scope.row)"
                             v-hasPermi="['project:user:addProjectUser']" type="primary">添加
                  </el-button>
                  <el-button v-if="scope.row.isInProject === 1 && scope.row.projectStatus === 0"
                             @click="handleUpdateProjectUser(scope.row)" v-hasPermi="['project:user:updateProjectUser']"
                             type="warning">禁用
                  </el-button>
                  <el-button v-if="scope.row.isInProject === 1 && scope.row.projectStatus === 1"
                             @click="handleUpdateProjectUser(scope.row)" v-hasPermi="['project:user:updateProjectUser']"
                             type="success">恢复
                  </el-button>
                  <el-button v-if="scope.row.isInProject === 1" v-hasPermi="['project:user:removeProjectUser']"
                             @click="handleRemoveProjectUserOne(scope.row)"
                             type="danger">删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <pagination
              layout="total, prev, pager, next"
              v-if="userListTotal > 0"
              :total="userListTotal"
              v-model:page="queryUserParams.pNum"
              v-model:limit="queryUserParams.pSize"
              @pagination="getUserList"
          />
        </el-col>
      </el-row>
      <el-dialog title="删除提醒" v-model="showRemoveProjectUser" width="500px">
        是否从项目中删除选中的用户？
        <template #footer>
          <div class="dialog-footer">
            <el-button type="info" @click="cancelRemoveProjectUserList">取消删除</el-button>
            <el-button type="danger" @click="confirmRemoveProjectUserList">确认删除</el-button>
          </div>
        </template>
      </el-dialog>

    </div>
    <!--  步骤条3内容  -->
    <div v-if="showStep && currentStep === 3" style="width: 90%;margin: auto;text-align: center" class="step-content">
      <div>当前项目共：{{ userAllListTotal }} 人</div>
      <br/>
      <div v-if="userAllListTotal > 0">
        <el-space wrap size="large">
          <el-card v-for="item in userAllList">
            <template #header>
              <div v-if="item.role === 1">项目经理</div>
              <div v-else>普通成员</div>
            </template>
            <template #default>
              <image-preview :src="item.avatar" :width="50" :height="50"/>
              <div>姓名:{{ item.nickName }}</div>
              <div>部门名:{{ item.deptName }}</div>
              <div>项目状态:
                <el-tag v-if="item.projectStatus === 0" type="success">正常</el-tag>
                <el-tag v-else type="danger">禁用</el-tag>
              </div>
              <div>邀请人:{{ item.createBy }}</div>
            </template>
          </el-card>
        </el-space>
      </div>
      <div v-else>
        <el-result>
          <template #title>
            <div>当前项目尚未添加成员</div>
          </template>
          <template #extra>
            <el-button type="primary" @click="currentStep = 1">添加项目经理</el-button>
            <el-button type="primary" @click="currentStep = 2">添加普通成员</el-button>
          </template>
        </el-result>
      </div>
    </div>
    <div class="footer" v-if="showStep">
      <el-button type="primary" :disabled="currentStep === 1" @click="--currentStep">上</el-button>
      <el-button type="primary" :disabled="currentStep === 3" @click="++currentStep">下</el-button>
      <el-button v-if="currentStep === 1 && !showProjectRoleSe" type="success" :disabled="currentStep === 3 "
                 @click="submitAddProjectRoleSe"
                 v-hasPermi="['project:user:addProjectRoleSe']">保存
      </el-button>
      <el-button v-if="currentStep === 1 && showProjectRoleSe" type="success"
                 :disabled="disabledUpdateProjectRoleSe"
                 @click="submitUpdateProjectRole"
                 v-hasPermi="['project:user:updateProjectRoleSe']">更新
      </el-button>

      <el-button type="warning" :disabled="currentStep === 3" @click="cancel">取消</el-button>
    </div>
  </div>
</template>

<style scoped>

.footer {
  position: marker;
  margin-top: 20px;
  bottom: 20px;
  right: 20px;
  display: flex;
  justify-content: flex-end;
  margin-right: 150px;
}

.step-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
</style>