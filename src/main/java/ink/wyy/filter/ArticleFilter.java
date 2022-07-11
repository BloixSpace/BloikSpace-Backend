package ink.wyy.filter;

import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebFilter(
        filterName = "filter3_articleFilter",
        urlPatterns = "/article/*"
)
public class ArticleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        HttpSession session = req.getSession();

        String uri = req.getRequestURI();
        switch (uri) {
            case "/article/add":
            case "/article/update":
            case "/article/delete":
                if (session.getAttribute("user") == null) {
                    HashMap<String, Object> res = new HashMap<>();
                    Gson gson = new Gson();
                    res.put("status", 0);
                    res.put("errMsg", "未登录");
                    resp.getWriter().write(gson.toJson(res));
                } else {
                    filterChain.doFilter(req, resp);
                }
                break;
            case "/article/get":
            case "/article/list":
                filterChain.doFilter(req, resp);
                break;
        }
    }

    @Override
    public void destroy() {

    }
}
