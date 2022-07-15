package ink.wyy.filter;

import ink.wyy.bean.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        User user = (User) req.getSession().getAttribute("user");

        if (user == null) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"未登录\"}");
            return;
        }

        String uri = req.getRequestURI();
        switch (uri) {
            case "/order/add":
            case "/order/queryList":
                if (user.getLevel() == 1) {
                    chain.doFilter(req, resp);
                } else {
                    resp.getWriter().write("{\"status\":0,\"errMsg\":\"无操作权限\"}");
                }
                return;
            default:
                chain.doFilter(req, resp);
        }


    }

    @Override
    public void destroy() {
    }
}
