<script setup>
import {getArchiveList, updateArchive, delProject} from '@/api/project/project.js'
import {deleteFileByIds} from "@/api/project/taskFile.js";
import ImagePreview from "@/components/ImagePreview/index.vue";

const {proxy} = getCurrentInstance()
const tableData = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: '',
  archiveTime: null
})

function handleQuery() {
  getArchiveList(queryParams.value).then(res => {
    tableData.value = res.data.rows
    total.value = res.data.total
  })
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: '',
    archiveTime: null
  }
  handleQuery()
}

const ids = ref([]);
const single = ref(true);
const multiple = ref(true);

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleUpdateArchive(row) {
  const _id = row.id || ids.value
  proxy.$confirm('确定取消归档选中的项目么？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateArchive(_id).then(res => {
      if (res.code === 200) {
        proxy.$message.success('删除成功')
        handleQuery()
      }
    })
  }).catch(() => {
  });
}

function handleDelProject(row) {
  const _id = row.id || ids.value
  proxy.$confirm('确定删除选中的项目么？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    delProject(_id).then(res => {
      if (res.code === 200) {
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
      <el-form-item label="项目">
        <el-input v-model="queryParams.name" placeholder="请输入项目名称"></el-input>
      </el-form-item>
      <el-form-item label="归档时间">
        <el-date-picker
            v-model="queryParams.archiveTime"
            type="date"
            clearable
            value-format="YYYY-MM-DD"
            placeholder="选择日期">
        </el-date-picker>
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
            @click="handleUpdateArchive"
        >
          <template #icon>
            <svg t="1709277722292" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="4603" width="16" height="16">
              <path
                  d="M687.544889 627.2L768 707.669333l80.455111-80.440889a42.666667 42.666667 0 1 1 60.330667 60.330667L828.344889 768l80.440889 80.455111a42.666667 42.666667 0 1 1-60.330667 60.330667L768 828.344889l-80.455111 80.440889a42.666667 42.666667 0 1 1-60.330667-60.330667L707.655111 768l-80.440889-80.455111a42.666667 42.666667 0 1 1 60.330667-60.330667zM760.234667 113.777778a113.777778 113.777778 0 0 1 111.445333 90.851555l0.682667 3.612445 50.432 292.892444a113.777778 113.777778 0 0 1 1.550222 14.464l0.099555 4.849778v44.913778A254.890667 254.890667 0 0 0 768 512c-60.444444 0-115.982222 20.935111-159.772444 55.964444 1.820444-3.370667 2.958222-7.153778 3.256888-11.178666L611.555556 554.666667v-42.666667a42.666667 42.666667 0 0 1 40.163555-42.595556L654.222222 469.333333h176.497778l-42.453333-246.613333a28.444444 28.444444 0 0 0-25.742223-23.523556L760.234667 199.111111H263.765333a28.444444 28.444444 0 0 0-27.548444 21.376l-0.483556 2.247111L193.265778 469.333333H369.777778a42.666667 42.666667 0 0 1 42.595555 40.163556L412.444444 512v42.666667a28.444444 28.444444 0 0 0 26.311112 28.373333L440.888889 583.111111h142.222222c3.370667 0 6.613333-0.583111 9.614222-1.664A256.042667 256.042667 0 0 0 532.081778 668.444444H440.888889a113.777778 113.777778 0 0 1-113.720889-110.222222L327.111111 554.666667H184.888889v241.777777a28.444444 28.444444 0 0 0 26.311111 28.373334L213.333333 824.888889h305.009778a254.634667 254.634667 0 0 0 36.778667 85.347555L213.333333 910.222222a113.777778 113.777778 0 0 1-113.720889-110.222222L99.555556 796.444444V520.448a113.777778 113.777778 0 0 1 0.924444-14.506667l0.725333-4.807111 50.432-292.892444a113.777778 113.777778 0 0 1 108.444445-94.407111L263.765333 113.777778h496.469334z"
                  fill="#1A1A1A" fill-opacity=".56" p-id="4604"></path>
            </svg>
          </template>
          取消归档
        </el-button>
        <el-button type="danger" plain @click="handleDelProject" icon="Delete">
          删除项目
        </el-button>
      </el-col>
    </el-row>
    <el-table
        :data="tableData"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="id" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="模板封面" align="center" prop="cover" >
        <template #default="scope">
          <image-preview :src="scope.row.cover" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="项目名称" prop="name"  align="center"></el-table-column>
      <el-table-column label="项目描述" prop="description" align="center"></el-table-column>
      <el-table-column label="归档人" prop="updateBy"  align="center"></el-table-column>
      <el-table-column label="归档时间" prop="archiveTime" align="center"></el-table-column>
      <el-table-column label="操作"  align="center">
        <template #default="scope">
          <el-button
              type="primary"
              plain
              @click="handleUpdateArchive(scope.row)"
          ><template #icon>
            <svg t="1709277722292" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="4603" width="16" height="16">
              <path
                  d="M687.544889 627.2L768 707.669333l80.455111-80.440889a42.666667 42.666667 0 1 1 60.330667 60.330667L828.344889 768l80.440889 80.455111a42.666667 42.666667 0 1 1-60.330667 60.330667L768 828.344889l-80.455111 80.440889a42.666667 42.666667 0 1 1-60.330667-60.330667L707.655111 768l-80.440889-80.455111a42.666667 42.666667 0 1 1 60.330667-60.330667zM760.234667 113.777778a113.777778 113.777778 0 0 1 111.445333 90.851555l0.682667 3.612445 50.432 292.892444a113.777778 113.777778 0 0 1 1.550222 14.464l0.099555 4.849778v44.913778A254.890667 254.890667 0 0 0 768 512c-60.444444 0-115.982222 20.935111-159.772444 55.964444 1.820444-3.370667 2.958222-7.153778 3.256888-11.178666L611.555556 554.666667v-42.666667a42.666667 42.666667 0 0 1 40.163555-42.595556L654.222222 469.333333h176.497778l-42.453333-246.613333a28.444444 28.444444 0 0 0-25.742223-23.523556L760.234667 199.111111H263.765333a28.444444 28.444444 0 0 0-27.548444 21.376l-0.483556 2.247111L193.265778 469.333333H369.777778a42.666667 42.666667 0 0 1 42.595555 40.163556L412.444444 512v42.666667a28.444444 28.444444 0 0 0 26.311112 28.373333L440.888889 583.111111h142.222222c3.370667 0 6.613333-0.583111 9.614222-1.664A256.042667 256.042667 0 0 0 532.081778 668.444444H440.888889a113.777778 113.777778 0 0 1-113.720889-110.222222L327.111111 554.666667H184.888889v241.777777a28.444444 28.444444 0 0 0 26.311111 28.373334L213.333333 824.888889h305.009778a254.634667 254.634667 0 0 0 36.778667 85.347555L213.333333 910.222222a113.777778 113.777778 0 0 1-113.720889-110.222222L99.555556 796.444444V520.448a113.777778 113.777778 0 0 1 0.924444-14.506667l0.725333-4.807111 50.432-292.892444a113.777778 113.777778 0 0 1 108.444445-94.407111L263.765333 113.777778h496.469334z"
                  fill="#1A1A1A" fill-opacity=".56" p-id="4604"></path>
            </svg>
          </template>
            取消归档
          </el-button>
          <el-button
              type="danger"
              plain
              icon="Delete"
              @click="handleDelProject(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
</style>
