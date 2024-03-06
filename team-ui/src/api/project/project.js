import request from '@/utils/request'

// 查询项目管理列表
export function listProject(query) {
    return request({
        url: '/project/project/list',
        method: 'get',
        params: query
    })
}
export function listProjectAll() {
    return request({
        url: '/project/project/listAll',
        method: 'get'
    })
}

// 查询项目管理详细
export function getProject(id) {
    return request({
        url: '/project/project/' + id,
        method: 'get'
    })
}

// 新增项目管理
export function addProject(data) {
    return request({
        url: '/project/project',
        method: 'post',
        data: data
    })
}

// 修改项目管理
export function updateProject(data) {
    return request({
        url: '/project/project',
        method: 'put',
        data: data
    })
}

// 删除项目管理
export function delProject(ids) {
    return request({
        url: '/project/project/' + ids,
        method: 'delete'
    })
}

// 获取回收站项目管理列表
export function listRecycleProject(query) {
    return request({
        url: '/project/project/getRecycle',
        method: 'get',
        params: query
    })
}
export function recoveryProject(ids) {
    return request({
        url: '/project/project/recovery/' + ids,
        method: 'put'
    })
}
export function removeProject(ids) {
    return request({
        url: '/project/project/realDelete/' + ids,
        method: 'delete'
    })
}
export function updateCollection(ids) {
    return request({
        url: '/project/project/updateCollection/' + ids,
        method: 'put',
    })
}
export function getCollectList(query) {
    return request({
        url: '/project/project/collection',
        method: 'get',
        params: query
    })
}
export function updateArchive(ids) {
    return request({
        url: '/project/project/updateArchive/' + ids,
        method: 'put',
    })
}
export function getArchiveList(query) {
    return request({
        url: '/project/project/archive',
        method: 'get',
        params: query
    })
}