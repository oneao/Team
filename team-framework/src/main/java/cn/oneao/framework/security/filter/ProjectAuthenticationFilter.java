package cn.oneao.framework.security.filter;

import cn.oneao.common.utils.ProjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class ProjectAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/project/task/")) {
            String header = request.getHeader("Project-Id");
            // 将当前请求的项目ID存入线程变量
            if (!header.equals("undefined")) {
                ProjectUtils.setProjectId(Long.valueOf(header));
            }
        }
        chain.doFilter(request, response);
    }
}
