import request from '@/utils/request'
export function addTaskTag(data) {
    return request({
        url: '/project/taskTag',
        method: 'post',
        data:data
    })
}
export function deleteTaskTag(id) {
    return request({
        url: '/project/taskTag/' + id,
        method: 'delete'
    })
}