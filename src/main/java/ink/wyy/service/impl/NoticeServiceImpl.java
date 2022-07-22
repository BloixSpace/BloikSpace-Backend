package ink.wyy.service.impl;

import com.google.gson.Gson;
import ink.wyy.bean.Notice;
import ink.wyy.dao.NoticeDao;
import ink.wyy.service.NoticeService;

import java.util.HashMap;
import java.util.Objects;

public class NoticeServiceImpl implements NoticeService {

    private final NoticeDao noticeDao;
    private final Gson gson;

    public NoticeServiceImpl(NoticeDao noticeDao) {
        this.noticeDao = noticeDao;
        gson = new Gson();
    }

    @Override
    public String add(Notice notice) {
        HashMap<String, Object> res = new HashMap<>();
        String msg = noticeDao.add(notice);
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String delete(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Notice notice = noticeDao.findById(id);
        if (notice == null) {
            res.put("errMsg", "通知不存在");
            return gson.toJson(res);
        }
        if (userId != -1 && !Objects.equals(notice.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String msg = noticeDao.delete(id);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String read(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Notice notice = noticeDao.findById(id);
        if (notice == null) {
            res.put("errMsg", "通知不存在");
            return gson.toJson(res);
        }
        if (!Objects.equals(notice.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String msg = noticeDao.read(id);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String getList(Integer page, Integer pageSize, String order, String unread, Boolean desc, Integer userId) {
        Integer bunread = null;
        if (unread != null && !unread.equals("")) {
            if (unread.equals("true")) {
                bunread = 1;
            } else {
                bunread = 0;
            }
        }
        if (order == null || order.equals("")) {
            order = "time";
        }
        desc = !desc;
        HashMap<String, Object> res = noticeDao.getList(page, pageSize, order, bunread, desc, userId);
        if (res == null) {
            return "{\"status\":0,\"errMsg\":\"查询失败\"}";
        }
        return gson.toJson(res);
    }
}
