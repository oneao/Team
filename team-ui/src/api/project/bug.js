import request from "@/utils/request.js";
export function getOption(){
    return request({
        url: "/project/bug/getOption",
        method: "get"
    });
}
export function getAllOption(){
    return request({
        url: "/project/bug/getAllOption",
        method: "get"
    });
}
export function addBug(data){
    return request({
        url: "/project/bug",
        method: "post",
        data
    });
}
export function getBugList(params){
    return request({
        url: "/project/bug",
        method: "get",
        params
    });
}
export function getBugDetail(id){
    return request({
        url: `/project/bug/${id}`,
        method: "get"
    });
}
export function updateBug(data){
    return request({
        url: "/project/bug",
        method: "put",
        data
    });
}
export function removeBug(ids){
    return request({
        url: "/project/bug/" + ids,
        method: "delete",
    });
}
export function getBugDeclare(params){
    return request({
        url: "/project/bug/declare",
        method: "get",
        params
    });
}
export function getBugDistribute(params){
    return request({
        url: "/project/bug/distribute",
        method: "get",
        params
    });
}