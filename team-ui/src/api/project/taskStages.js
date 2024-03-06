import request from '@/utils/request'
// 新增
export function addTaskStage(data) {
    return request({
        url: '/project/taskStages',
        method: 'post',
        data: data
    })
}
// 更新
export function updateTaskStage(data) {
    return request({
        url: '/project/taskStages',
        method: 'put',
        data: data
    })
}
// 删除
export function removeTaskStage(taskStageId){
    return request({
        url: '/project/taskStages/' + taskStageId,
        method: 'delete'
    })
}