import request from '@/utils/request'
// 获取任务列表
export function listTask(params) {
    return request({
        url: '/project/task/list/',
        method: 'get',
        params: params
    })
}
export function listByProjectId(projectId) {
    return request({
        url: '/project/task/listByProjectId/' + projectId,
        method: 'get'
    })
}
export function addTask(data) {
    return request({
        url: '/project/task',
        method: 'post',
        data: data
    })
}
export function getTask(taskId) {
    return request({
        url: '/project/task/' + taskId,
        method: 'get'
    })
}
export function updateTask(data) {
    return request({
        url: '/project/task',
        method: 'put',
        data: data
    })
}
export function addChildTask(data){
    return request({
        url: '/project/task/addChildTask',
        method: 'post',
        data: data
    })
}
export function getTaskLog(taskId){
    return request({
        url: '/project/task/getTaskLog',
        method: 'get',
        params:taskId
    })
}
export function getTaskComment(taskId){
    return request({
        url: '/project/task/getTaskComment',
        method: 'get',
        params:taskId
    })
}
export function removeTask(taskId){
    return request({
        url: '/project/task/'+taskId,
        method: 'delete'
    })
}
export function updateTaskLike(taskId){
    return request({
        url: '/project/task/updateTaskLike/'+taskId,
        method: 'put'
    })
}
export function getRecycleTask(projectId){
    return request({
        url: '/project/task/getRecycleTask/'+projectId,
        method: 'get'
    })
}
export function recoverTask(taskId){
    return request({
        url: '/project/task/recoverTask/'+taskId,
        method: 'put'
    })
}
export function removeRecycleTask(taskId){
    return request({
        url: '/project/task/realDeleteTask/'+taskId,
        method: 'delete'
    })
}
export function getRecycleFile(projectId){
    return request({
        url: '/project/task/getRecycleFile/'+projectId,
        method: 'get'
    })
}
export function recoverFile(fileId){
    return request({
        url: '/project/task/recoverFile/'+fileId,
        method: 'put'
    })
}
export function removeRecycleFile(fileId){
    return request({
        url: '/project/task/realDeleteFile/'+fileId,
        method: 'delete'
    })
}
export function getRecycleTaskAll(params){
    return request({
        url: '/project/task/getRecycleTaskAll',
        method: 'get',
        params:params
    })
}
export function getTaskList(params){
    return request({
        url: '/project/task/getTaskList',
        method: 'get',
        params:params
    }
    )
}
export function removeTaskList(ids){
    return request({
        url: '/project/task/removeTaskList/' + ids,
        method: 'delete',
    })
}
export function getTaskStageByTaskId(taskId){
    return request({
        url: '/project/task/getTaskStageByTaskId/'+taskId,
        method: 'get'
    })
}