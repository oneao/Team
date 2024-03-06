package cn.oneao.common.utils;

public class ProjectUtils {
    private static final ThreadLocal<Long> projectIdThreadLocal = new ThreadLocal<>();

    public static void setProjectId(Long projectId) {
        projectIdThreadLocal.set(projectId);
    }

    public static Long getProjectId() {
        return projectIdThreadLocal.get();
    }

    public static void removeProjectId() {
        projectIdThreadLocal.remove();
    }
}
