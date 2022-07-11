package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.Article;
import ink.wyy.dao.ArticleDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

public class ArticleServiceImpl implements ArticleService{

    private ArticleDao articleDao;

    public ArticleServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    public Article add(Article article) {
        if (article.getTitle() == null || article.getTitle().equals("")) {
            article.setErrorMsg("title不能为空");
            return article;
        }
        article = articleDao.add(article);
        return article;
    }

    @Override
    public String update(HttpServletRequest req, Integer userId) {
        String id = req.getParameter("id");
        if (id == null) {
            return "id不能为空";
        }
        Article article = articleDao.findById(Integer.valueOf(id));
        if (article == null) {
            return "文章不存在";
        }
        if (!Objects.equals(article.getUserId(), userId)) {
            return "无操作权限";
        }
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String category = req.getParameter("category");
        if (title != null && !title.equals("")) {
            article.setTitle(title);
        }
        if (content != null && !content.equals("")) {
            article.setContent(content);
        }
        if (category != null && !category.equals("")) {
            article.setCategory(title);
        }
        return articleDao.update(article);
    }

    @Override
    public String delete(Integer id, Integer userId) {
        Article article = articleDao.findById(id);
        if (article == null) {
            return "文章不存在";
        }
        if (!Objects.equals(article.getUserId(), userId)) {
            return "无操作权限";
        }
        Boolean ok = articleDao.delete(id);
        if (!ok) {
            return "删除失败";
        } else {
            return null;
        }
    }

    @Override
    public Article get(Integer id) {
        Article article = articleDao.findById(id);
        if (article == null) {
            article = new Article();
            article.setErrorMsg("文章不存在");
        }
        return article;
    }

    @Override
    public String getList(int page, int pageSize, String order, String category, Integer userId) {
        HashMap<String, Object> map = articleDao.getList(page, pageSize, order, category, userId);
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}
