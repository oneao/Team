package cn.oneao.common.exception.project;

import cn.oneao.common.exception.base.BaseException;

public class ProjectException extends BaseException {
    private static final long serialVersionUID = 1L;
    public ProjectException(String defaultMessage) {
        super("project", null, null, defaultMessage);
    }
}
