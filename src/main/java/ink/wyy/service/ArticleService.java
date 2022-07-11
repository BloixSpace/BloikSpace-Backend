package ink.wyy.service;

import ink.wyy.bean.Article;

import javax.servlet.http.HttpServletRequest;

public interface ArticleService {
    Article add(Article article);

    String update(HttpServletRequest req, Integer userId);

    String delete(Integer id, Integer userId);

    Article get(Integer id);

    String getList(int page, int pageSize, String order, String category, Integer userId);
}
