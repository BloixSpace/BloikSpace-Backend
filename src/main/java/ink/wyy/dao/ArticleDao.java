package ink.wyy.dao;

import ink.wyy.bean.Article;

import java.util.HashMap;

public interface ArticleDao {
    Article add(Article article);
    String update(Article article);
    Boolean delete(Integer id);
    Article findById(Integer id);

    HashMap<String, Object> getList(int page, int pageSize, String order, String category, Integer userId);
}
