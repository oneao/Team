package cn.oneao.common.exception.project;

import cn.oneao.common.constant.HttpStatus;

public class ProjectUserNotInException extends ProjectException{
    private static final long serialVersionUID = 1L;

    public ProjectUserNotInException() {
        super("当前您不在项目中，无法操作当前项目，请联系管理员添加您到项目中。");
    }

}
