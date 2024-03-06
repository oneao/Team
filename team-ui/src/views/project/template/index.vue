
<script setup name="Template">
import {listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate} from "@/api/project/template";
import ImagePreview from "@/components/ImagePreview/index.vue";
import ImageUpload from "@/components/ImageUpload/index.vue";
import router  from "@/router/index.js";

const {proxy} = getCurrentInstance();

const templateList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const isSystemSelect = [
  {label: "是", value: 1},
  {label: "否", value: 0},
  {label: "全部", value: -1}
];


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    id: null,
    isSystem: null,
  },
  rules: {
    name: [
      {required: true, message: "项目模版名称不能为空", trigger: "blur"}
    ],
    description: [
      {required: true, message: "项目模版描述不能为空", trigger: "blur"}
    ],
    cover: [
      {required: true, message: "项目模版封面不能为空", trigger: "blur"}
    ]
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询项目模版列表 */
function getList() {
  loading.value = true;
  listTemplate(queryParams.value).then(response => {
    templateList.value = response.data.rows;
    total.value = response.data.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    name: null,
    description: null,
    cover: null,
    isSystem: null,
  };
  proxy.resetForm("templateRef");
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
  reset();
  open.value = true;
  title.value = "添加项目模版";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getTemplate(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改项目模版";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["templateRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null){
        console.log(form.value)
        updateTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addTemplate(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除项目模版编号为"' + _ids + '"的数据项？').then(function () {
    return delTemplate(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('project/template/export', {
    ...queryParams.value
  }, `template_${new Date().getTime()}.xlsx`)
}

function handleTaskStagesList(row) {
  console.log(row)
  router.push({
    path: '/project/templateTaskStages',
    query: {
      id: row.id,
      name: row.name
    }
  })
}

getList();
</script>
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模版名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入项目模版名称"
            clearable
        />
      </el-form-item>
      <el-form-item label="是否默认">
        <el-select
            v-model="queryParams.isSystem"
            class="m-2"
            placeholder="全部"
            style="width: 240px"
        >
          <el-option
              v-for="item in isSystemSelect"
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
            v-hasPermi="['project:template:add']"
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
            v-hasPermi="['project:template:edit']"
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
            v-hasPermi="['project:template:remove']"
        >删除
        </el-button>
      </el-col>

      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="模版名称" width="130" align="center" prop="name"/>
      <el-table-column label="模版描述" align="center" prop="description"/>
      <el-table-column label="模板封面" align="center" prop="cover" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.cover" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="系统默认" align="center" prop="isSystem">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.isSystem === 1">默认</el-tag>
          <el-tag type="info" v-else>否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy"/>
      <el-table-column label="创建时间" align="center" prop="createTime"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="success" icon="SetUp"
                     v-hasPermi="['project:template:taskStagesList']" @click="handleTaskStagesList(scope.row)">阶段
          </el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['project:template:edit']">修改
          </el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['project:template:remove']">删除
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
        layout="total,prev, pager, next, jumper"
    />

    <!-- 添加或修改项目模版对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="templateRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模版名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入项目模版名称"/>
        </el-form-item>
        <el-form-item label="模版描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
        <el-form-item label="模版封面" prop="cover">
          <image-upload v-model:model-value="form.cover" :limit="1" ></image-upload>
        </el-form-item>
        <el-form-item label="系统默认" prop="isSystem">
          <el-switch v-model="form.isSystem" :active-value=1 :inactive-value=0 />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

