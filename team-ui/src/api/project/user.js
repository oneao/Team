import request from '@/utils/request'
export function list(params) {
    return request({
        url: '/project/user/list/',
        method: 'get',
        params: params
    })
}
export function queryNowUser(projectId) {
    return request({
        url: '/project/user/' + projectId,
        method: 'get'
    })
}
// 查询当前项目下的项目经理
export function queryRoleSe(projectId){
    return request({
        url: '/project/user/queryRoleSe/' + projectId,
        method: 'get'
    })
}
// 获取所有岗位为项目经理的用户
export function queryRoleSeAll(){
    return request({
        url: '/project/user/queryRoleSeAll',
        method: 'get'
    })
}
// 添加项目经理
export function addProjectRoleSe(data) {
    return request({
        url: '/project/user/addProjectRoleSe',
        method: 'post',
        data: data
    })
}
export function updateProjectRoleSe(data) {
    return request({
        url: '/project/user/updateProjectRoleSe',
        method: 'put',
        data: data
    })
}
export function queryUserList(params) {
    return request({
        url: '/project/user/queryUserList',
        method: 'get',
        params: params
    })
}
export function addProjectUser(data) {
    return request({
        url: '/project/user/addProjectUser',
        method: 'post',
        data: data
    })
}
export function updateProjectUser(data) {
    return request({
        url: '/project/user/updateProjectUser',
        method: 'put',
        data: data
    })
}
export function removeProjectUser(data) {
    return request({
        url: '/project/user/removeProjectUser' ,
        method: 'delete',
        data: data
    })
}
