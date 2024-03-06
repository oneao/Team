import request from '@/utils/request'

// 查询项目模版列表
export function listTemplate(query) {
    return request({
        url: '/project/template/list',
        method: 'get',
        params: query
    })
}

export function listTemplateAll() {
    return request({
        url: '/project/template/listAll',
        method: 'get'
    })
}

// 查询项目模版详细
export function getTemplate(id) {
    return request({
        url: '/project/template/' + id,
        method: 'get'
    })
}

// 新增项目模版
export function addTemplate(data) {
    return request({
        url: '/project/template',
        method: 'post',
        data: data
    })
}

// 修改项目模版
export function updateTemplate(data) {
    return request({
        url: '/project/template',
        method: 'put',
        data: data
    })
}

// 删除项目模版
export function delTemplate(id) {
    return request({
        url: '/project/template/' + id,
        method: 'delete'
    })
}

// 查询任务列表模版列表
export function listTaskStagesTemplate(id) {
    return request({
        url: '/project/template/taskStagesList/' + id,
        method: 'get',
    })
}
// 查询任务列表模版详细
export function getTaskStagesTemplate(id) {
    return request({
        url: '/project/template/taskStages/' + id,
        method: 'get'
    })
}
// 新增任务列表模版
export function addTaskStagesTemplate(data) {
    return request({
        url: '/project/template/taskStages',
        method: 'post',
        data: data
    })
}
// 修改任务列表模版列表
export function updateTaskStagesTemplate(data) {
    return request({
        url: '/project/template/taskStages',
        method: 'put',
        data: data
    })
}
// 删除任务列表模版
export function delTaskStagesTemplate(id) {
    return request({
        url: '/project/template/taskStages/' + id,
        method: 'delete'
    })
}