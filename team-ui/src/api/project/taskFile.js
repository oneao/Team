import request from '@/utils/request'
export function removeTaskFile(fileId){
    return request({
        url: '/project/taskFile/' + fileId,
        method: 'delete'
    })
}
export function getTaskFileList(projectId){
    return request({
        url: '/project/taskFile/' + projectId,
        method: 'get',
    })
}
export function renameFile(data){
    return request({
        url: '/project/taskFile/renameFile',
        method: 'put',
        data:data
    })
}
export function deleteFile(data){
    return request({
        url: '/project/taskFile/deleteFile',
        method: 'put',
        data:data
    })
}
export function deleteFileByIds(ids){
    return request({
        url: '/project/taskFile/deleteFile/' + ids,
        method: 'delete',
    })
}
export function getRecycleFile(params){
    return request({
        url: '/project/taskFile/getRecycleFile',
        method: 'get',
        params:params
    })
}
export function getFileList(params){
    return request({
        url: '/project/taskFile/getFileList',
        method: 'get',
        params:params
    })
}