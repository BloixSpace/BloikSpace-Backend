package ink.wyy.filter;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class FollowFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        String uri = req.getRequestURI();
        switch (uri) {
            case "/follow/add":
            case "/follow/delete":
                if (session.getAttribute("user") != null) {
                    chain.doFilter(req, resp);
                } else {
                    resp.getWriter().write("{\"status\":0,\"errMsg\":\"未登录\"}");
                    return;
                }
                break;
            default:
                chain.doFilter(req, resp);
        }
    }
}
