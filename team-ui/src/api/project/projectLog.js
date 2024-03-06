import request from '@/utils/request'
export function getProjectLogListByProjectId(params){
    return request({
        url: '/project/projectLog/listByProjectId/',
        method: 'get',
        params:params
    })
}