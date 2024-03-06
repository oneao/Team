<script setup>
import ImagePreview from "@/components/ImagePreview/index.vue";
const loading = ref(true)
import router from "@/router/index.js";

// 一言===========================================================
import {getYiYan} from "@/api/common.js";

const yiYan = ref({})

function handleGetYiYan() {
  getYiYan().then(res => {
    yiYan.value = JSON.parse(res.msg)
    console.log(yiYan.value)
    loading.value = false
  })
}

// 获取提示信息
import {getIndexData} from "@/api/index.js";
import {parseTime} from "@/utils/ruoyi.js";

const data = ref({})

function handleGetIndexData() {
  getIndexData().then(res => {
    data.value = res.data
    data.value.projectList.forEach(item => {
      item.cover = import.meta.env.VITE_APP_BASE_API + item.cover
    })
  })
}

function handleGetTipMessage(name) {
  const date = new Date();
  const hour = date.getHours();

  let tipMessage = '';

  if (hour < 12) {
    tipMessage = '早上好！' + name + '，欢迎开始美好的一天吧！';
  } else if (hour < 18) {
    tipMessage = '下午好！' + name + '，下午也要努力工作哦！';
  } else {
    tipMessage = '晚上好！' + name + '，记得早点休息哦！';
  }

  return tipMessage;
}

function toTask(item){
  router.push({
    path: '/project/task',
    query: {
      projectId: item.id,
    }
  })
}
function init() {
  // 获取一言
  handleGetYiYan()
  handleGetIndexData()

}
init()
</script>

<template>
  <div>
    <div v-if="!loading" class="bg-white" style="height: 100%">
      <div class="flex justify-between p-6 border rounded-md bg-amber-100" style="width: 98%;margin: 14px auto">
        <div class="flex space-x-4">
        <span class="relative flex h-12 w-12 shrink-0 overflow-hidden rounded-full">
          <span class=" inset-0 bg-muted flex items-center justify-center rounded-full">
            <el-avatar :src="data.sysUser.avatar"></el-avatar>
          </span>
        </span>
          <div>
            <div class="text-lg font-semibold">{{ handleGetTipMessage(data.sysUser.nickName) }}</div>
            <div class="text-sm text-gray-500">{{ yiYan.data.vhan }} <span
                style="margin-left: 18px">节选自《{{ yiYan.data.source }}》</span></div>
          </div>
        </div>
        <div class="flex space-x-6">
          <div class="text-center">
            <div class="text-2xl font-semibold">{{ data.projectList.length }}</div>
            <div class="text-sm text-gray-500">项目数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-semibold">{{ data.toDoCount }}/{{ data.allCount }}</div>
            <div class="text-sm text-gray-500">待办</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-semibold">35</div>
            <div class="text-sm text-gray-500">消息</div>
          </div>
        </div>
      </div>
      <div class="text-lg font-semibold" style="margin-left: 24px">
        我的项目
      </div>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 p-6">
        <div v-if="data.projectList.length > 0" v-for="item in data.projectList"
             class="w-full max-w-sm overflow-hidden bg-white rounded-lg shadow-lg dark:bg-gray-800 "
             style="cursor: pointer"
             :class="{ 'pointer-events-none': item.status === 0 }"
             @click="toTask(item)">
          <img class="object-cover w-full max-h-44 "
               :src="item.cover"
               alt="avatar">
          <div v-if="item.status === 0" class="flex items-center px-6 py-3 bg-gray-500">
            <svg t="1708933961633" class="w-6 h-6 text-white fill-current" viewBox="0 0 1024 1024" version="1.1"
                 xmlns="http://www.w3.org/2000/svg" p-id="11963" width="16" height="16">
              <path
                  d="M511.999985 0c282.799992 0 511.999985 229.199993 511.999985 511.999985s-229.199993 511.999985-511.999985 511.999985c-282.799992 0.1-511.999985-229.199993-511.999985-511.999985S229.199993 0 511.999985 0z m69.499998 284.599992c1.7-32.099999-22.599999-57.099998-54.199998-57.099999H500.999985c-31.399999 0-56.099998 25.599999-54.599998 57.099999l13.1 284.199991c1.4 29.999999 24.699999 52.799998 53.199998 55.799999-1.9 0.1-3.8 1.1-5.8 1.1h11.8c-2.1 0-3.9-1-6-1.1 28.299999-3.1 52.099998-26.399999 53.799998-55.799999l15-284.199991zM568.999983 739.699978c0-31.399999-25.399999-56.999998-56.999998-56.999998-31.399999 0-56.999998 25.399999-56.999998 56.999998 0 31.499999 25.399999 56.999998 56.999998 56.999999 31.499999-0.2 56.999998-25.599999 56.999998-56.999999z"
                  fill="#E1E1E0" p-id="11964"></path>
              <path
                  d="M527.299985 227.499993c31.699999 0 55.999998 24.999999 54.199998 57.099999l-15.1 284.199991c-1.5 29.399999-25.399999 52.799998-53.799998 55.799999-28.499999-3.1-51.799998-25.999999-53.199998-55.799999l-13.1-284.199991c-1.4-31.499999 23.199999-57.099998 54.599998-57.099999h26.4zM511.999985 682.69998c31.399999 0 56.999998 25.399999 56.999998 56.999998 0 31.499999-25.399999 56.999998-56.999998 56.999999-31.399999 0-56.999998-25.399999-56.999998-56.999999s25.599999-56.999998 56.999998-56.999998z"
                  fill="#FFFFFF" p-id="11965"></path>
            </svg>
            <h1 class="mx-3 text-lg font-semibold text-white">未开始</h1>
          </div>
          <div v-else-if="item.status === 1" class="flex items-center px-6 py-3 bg-blue-400">
            <svg t="1708934116527" class="w-6 h-6 text-white fill-current" viewBox="0 0 1024 1024" version="1.1"
                 xmlns="http://www.w3.org/2000/svg" p-id="14818" width="16" height="16">
              <path
                  d="M292.949333 513.536a43.52 43.52 0 0 0-43.52-43.52H43.52a43.52 43.52 0 1 0 0 87.04h205.994667a43.52 43.52 0 0 0 43.52-43.52z m688.384-43.52H775.338667a43.52 43.52 0 1 0 0 87.04H981.333333a43.52 43.52 0 1 0 0-87.04z m-468.48 275.626667a43.52 43.52 0 0 0-43.434666 43.434666v191.402667a43.52 43.52 0 1 0 87.04 0V789.162667a43.52 43.52 0 0 0-43.52-43.52zM512.853333 0a43.52 43.52 0 0 0-43.434666 43.52v205.994667a43.52 43.52 0 1 0 87.04 0V43.52A43.52 43.52 0 0 0 512.853333 0zM294.826667 666.453333L149.162667 812.202667a43.52 43.52 0 0 0 61.525333 61.44L356.352 727.893333a43.52 43.52 0 1 0-61.610667-61.44z m401.92-296.874666c11.178667 0 22.186667-4.181333 30.72-12.714667l145.749333-145.664a43.52 43.52 0 0 0-61.44-61.525333L665.856 295.253333a43.52 43.52 0 0 0 30.72 74.325334z m42.154666 307.029333a43.52 43.52 0 0 0-61.44 61.44l135.338667 135.594667a43.178667 43.178667 0 0 0 61.44 0 43.52 43.52 0 0 0 0-61.525334L738.901333 676.608zM210.773333 150.869333a43.52 43.52 0 0 0-61.44 61.44l145.664 145.749334a43.178667 43.178667 0 0 0 61.44 0 43.52 43.52 0 0 0 0-61.44L210.773333 150.784z"
                  fill="#4C7BFF" p-id="14819"></path>
            </svg>
            <h1 class="mx-3 text-lg font-semibold text-white">正在进行</h1>
          </div>
          <div v-else class="flex items-center px-6 py-3 bg-red-500">
            <svg t="1708934232674" class="w-6 h-6 text-white fill-current" viewBox="0 0 1024 1024" version="1.1"
                 xmlns="http://www.w3.org/2000/svg" p-id="18497" width="16" height="16">
              <path
                  d="M758.016 846.72a405.674667 405.674667 0 0 1-240.682667 79.146667c-225.237333 0-408.533333-183.296-408.533333-408.533334 0-225.28 183.296-408.533333 408.533333-408.533333S925.866667 292.053333 925.866667 517.333333c0 35.114667-4.906667 68.992-13.312 101.546667-21.930667 54.442667 42.069333 81.109333 62.634666 30.122667 12.074667-41.856 18.901333-85.973333 18.901334-131.669334C994.090667 254.421333 780.202667 40.533333 517.290667 40.533333S40.533333 254.421333 40.533333 517.333333 254.421333 994.133333 517.333333 994.133333c105.941333 0 203.562667-35.157333 282.837334-93.824 34.474667-34.986667-9.514667-78.976-42.154667-53.589333z"
                  fill="#231815" p-id="18498"></path>
              <path
                  d="M756.821333 369.365333a40.106667 40.106667 0 0 0-56.576 0l-243.626666 243.584-125.098667-125.056c-15.829333-15.872-40.448-16.896-55.04-2.261333-14.634667 14.592-13.610667 39.210667 2.261333 55.04l143.104 143.104c6.272 6.314667 14.08 9.642667 22.016 11.178667a39.509333 39.509333 0 0 0 49.024-4.992l264.021334-264.021334a40.106667 40.106667 0 0 0-0.085334-56.576z"
                  fill="#231815" p-id="18499"></path>
            </svg>
            <h1 class="mx-3 text-lg font-semibold text-white">已结束</h1>
          </div>
          <div class="px-6 py-4">

            <h1 class="text-xl font-semibold text-gray-800 dark:text-white">{{ item.name }}</h1>
            <div class="flex items-center mt-4 text-gray-700 dark:text-gray-200">
              <svg t="1708936586747" class="w-6 h-6 fill-current"  viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="20360" width="16" height="16"><path d="M211.541333 541.397333l86.144-0.725333 0.213334 29.653333-86.144 0.682667-0.213334-29.610667z m310.229334-29.482666a51.626667 51.626667 0 0 1 22.698666 5.034666 519.466667 519.466667 0 0 1 89.088-86.741333l24.704 23.168a495.786667 495.786667 0 0 1-85.290666 99.669333 51.882667 51.882667 0 0 1-50.389334 62.677334 51.925333 51.925333 0 0 1-43.349333-81.152 887.338667 887.338667 0 0 1-99.242667-166.442667l25.941334-14.506667a1039.616 1039.616 0 0 1 100.821333 160.64c4.693333-1.493333 9.770667-2.304 15.018667-2.346666z m-76.544-282.410667l0.554666 88.704 243.712-142.677333L444.117333 35.754667l0.469334 90.965333a432.512 432.512 0 0 0-241.408 124.117333 432.298667 432.298667 0 0 0-124.544 307.413334c0.938667 115.370667 47.232 224.554667 129.408 305.450666s192.128 125.397333 307.413333 124.501334a431.957333 431.957333 0 0 0 305.450667-129.322667 434.005333 434.005333 0 0 0 104.789333-174.250667l4.864-15.658666-97.322667-27.989334-4.736 14.762667a331.946667 331.946667 0 0 1-79.786666 132.096 331.306667 331.306667 0 0 1-234.026667 99.157333 331.52 331.52 0 0 1-235.562667-95.402666 331.349333 331.349333 0 0 1-99.157333-234.069334 330.837333 330.837333 0 0 1 95.445333-235.52 330.666667 330.666667 0 0 1 169.813334-92.501333z m394.709333 272.981333a321.92 321.92 0 0 0-16.213333-62.250666l-5.461334-14.848 94.677334-36.181334 5.632 15.104c10.794667 29.226667 18.389333 59.52 22.656 90.325334l2.474666 17.408-101.461333 4.522666-2.304-14.08z m-0.938667 110.549334c3.541333-20.096 5.248-40.405333 5.034667-60.8l-0.085333-6.016-0.341334-15.701334 101.12-4.437333 0.512 16.256c0.085333 3.029333 0.085333 6.101333 0.128 9.130667 0.213333 29.098667-2.474667 58.197333-8.021333 86.784l-3.328 17.152-97.578667-28.16 2.56-14.208z m-33.152-212.992a331.733333 331.733333 0 0 0-31.488-48.810667l-9.770666-12.501333 79.232-63.061334 9.941333 12.714667c17.578667 22.442667 32.853333 46.592 45.653333 72.064l7.936 15.829333-94.933333 36.352-6.570667-12.586666zM755.2 328.32l-10.282667-10.112a331.818667 331.818667 0 0 0-48.512-39.552l-14.378666-9.6 62.762666-80.128 12.373334 8.533333c20.992 14.421333 40.704 30.677333 58.88 48.512 2.090667 2.133333 4.224 4.224 6.272 6.4l12.416 12.672L755.2 328.32z m-240.64 521.216l-0.725333-86.101333 29.653333-0.256 0.682667 86.101333-29.610667 0.256z m216.832-87.68l-61.354667-60.416 20.778667-21.162667 61.354667 60.501334-20.778667 21.077333z m-432.170667-3.626667l60.458667-61.397333 21.162667 20.821333-60.416 61.354667-21.205334-20.778667z m3.669334-432.170666l61.354666 60.458666-20.736 21.12-61.397333-60.416 20.778667-21.162666z m432.170666 3.712l-21.162666-20.821334-60.416 61.354667 21.12 20.864 60.458666-61.397333z m87.637334 216.832l-86.144 0.682666-0.298667-29.653333 86.229333-0.682667 0.213334 29.653334z" fill="#040000" p-id="20361"></path></svg>
              <h1 v-if="item.status === 0" class="px-2 text-sm">尚未开始</h1>
              <h1 v-if="item.status === 1" class="px-2 text-sm">{{ item.beginTime }} 到 {{item.endTime}}</h1>
              <h1 v-else class="px-2 text-sm">{{ item.realEndTime }}</h1>
            </div>
          </div>
        </div>
        <div v-else style="margin: 0 auto">
          <el-result
              icon="warning"
              title="Warning Tip"
              sub-title="暂无项目"
          >
          </el-result>
        </div>
      </div>
      <div class="flex items-center justify-center">
      </div>
      <div class="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-4">
          <div class="text-lg font-semibold">动态</div>
          <div dir="ltr" class="relative overflow-hidden h-72 w-full rounded-md border"
               style="position: relative; --radix-scroll-area-corner-width: 0px; --radix-scroll-area-corner-height: 0px;">
            <div data-radix-scroll-area-viewport="" class="h-full w-full rounded-[inherit]"
                 style="overflow: hidden scroll;">
              <div style="min-width: 100%; display: table;">
                <div class="space-y-4 p-4">
                  <div v-for="item in data.logList">
                    <div class="flex items-center space-x-2">
                      <div class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
                        <image-preview :src="item.avatar"/>
                      </div>
                      <div>
                        <span class="font-semibold">{{item.createBy}}</span>
                        <span v-if="item.isLog === 0" class="text-xs text-gray-500" style="margin-left: 12px">项目：{{ item.name }}</span>
                        <span v-else class="text-xs text-gray-500" style="margin-left: 12px">任务：{{ item.name }}</span>
                      </div>
                    </div>
                    <div>
                      <div class="text-sm">{{ item.content }}</div>
                      <div class="text-xs text-gray-500">{{parseTime(item.createTime)}}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="space-y-4">
          <div class="text-lg font-semibold">快速操作</div>
          <div class="grid grid-cols-2 gap-4">
            <button
                class="inline-flex bg-fuchsia-300 items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-20 px-4 py-2">
              创建
            </button>
          </div>
          <div class="flex justify-center"></div>
        </div>
      </div>
    </div>
    <div v-else class='flex items-center  justify-center' style="align-items: center;height: 880px">
      <button type="button"
              class="bg-indigo-400 h-max w-max rounded-lg text-white font-bold hover:bg-indigo-300 hover:cursor-not-allowed duration-[500ms,800ms]"
              disabled>
        <div class="flex items-center justify-center m-[10px]">
          <div class="h-5 w-5 border-t-transparent border-solid animate-spin rounded-full border-white border-4"></div>
          <div class="ml-2"> 正在加载，请稍等...</div>
        </div>
      </button>
    </div>
  </div>
</template>

