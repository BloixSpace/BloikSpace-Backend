package ink.wyy.filter;

import ink.wyy.bean.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LevelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String uri = req.getRequestURI();
        if (uri.contains("/admin/")) {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.getWriter().write("{\"status\":0,\"errMsg\":\"未登录\"}");
            } else if (user.getLevel() >= 3) {
                filterChain.doFilter(req, resp);
            } else {
                resp.getWriter().write("{\"status\":0,\"errMsg\":\"无操作权限\"}");
            }
        } else {
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
    }
}
