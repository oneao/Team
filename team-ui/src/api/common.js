import request from '@/utils/request'

export function getYiYan(){
    return request({
        url: '/common/yiYan',
        method: 'get'
    })
}