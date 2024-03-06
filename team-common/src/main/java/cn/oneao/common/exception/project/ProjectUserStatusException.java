package cn.oneao.common.exception.project;

public class ProjectUserStatusException extends ProjectException{
    private static final long serialVersionUID = 1L;

    public ProjectUserStatusException() {
        super("您在项目中的状态已被禁用，无法操作当前项目，请联系管理员。");
    }
}
