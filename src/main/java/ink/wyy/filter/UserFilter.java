package ink.wyy.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName = "filter1_userFilter",
        urlPatterns = "/user/*"
)
public class UserFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String uri = req.getRequestURI();
        switch (uri) {
            case "/user/login":
            case "/user/register":
            case "/user/getUserList":
                chain.doFilter(req, resp);
                break;
            default:
                if (req.getSession().getAttribute("user") != null) {
                    chain.doFilter(req, resp);
                } else {
                    resp.getWriter().write("{\"status\":0,\"errMsg\":\"未登录\"}");
                }
        }
    }

    @Override
    public void destroy() {
    }
}
