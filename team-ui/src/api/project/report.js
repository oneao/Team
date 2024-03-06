import request from "@/utils/request.js";
export function listReport(params){
    return request({
        url: "/project/report",
        method: "get",
        params: params
    });
}
export function listReportMy(params){
    return request({
        url: "/project/report/my",
        method: "get",
        params: params
    });
}
export function addReport(data){
    return request({
        url: "/project/report",
        method: "post",
        data: data
    });
}
export function getReport(id){
    return request({
        url: "/project/report/" + id,
        method: "get"
    });
}
export function updateReport(data){
    return request({
        url: "/project/report",
        method: "put",
        data: data
    });
}
export function deleteReport(ids){
    return request({
        url: "/project/report/" + ids,
        method: "delete"
    });
}
export function getReportByProjectId(params){
    return request({
        url: "/project/report/getListInTask",
        method: "get",
        params: params
    });
}