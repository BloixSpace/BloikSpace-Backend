package ink.wyy.filter;

import ink.wyy.bean.User;
import ink.wyy.dao.UserDao;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class BaseFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();
        if (uri.contains("/img/") || uri.contains("/files/")) {
            filterChain.doFilter(req, resp);
            return;
        }

        if (req.getRequestURI().contains("html")) {
            resp.setContentType("text/html;charset=UTF-8");
        } else {
            resp.setContentType("application/json;charset=UTF-8");
        }

        // 检测登录的用户是否存在
        ServletContext context = req.getServletContext();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        UserDao userDao = (UserDao) context.getAttribute("userDao");
        if (user != null && !user.equals(userDao.findById(user.getId()))) {
            session.removeAttribute("user");
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"未登录\"}");
            return;
        }

        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
