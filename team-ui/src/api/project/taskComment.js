import request from '@/utils/request'
export function addTaskComment(data){
    return request({
        url: '/project/taskComment',
        method: 'post',
        data: data
    })
}
export function removeTaskComment(commentId){
    return request({
        url: '/project/taskComment/' + commentId,
        method: 'delete'
    })
}