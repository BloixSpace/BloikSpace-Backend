package ink.wyy.dao;

import ink.wyy.bean.Notice;

import java.util.HashMap;

public interface NoticeDao {

    String add(Notice notice);

    String read(Integer id);

    String delete(Integer id);

    Notice findById(Integer id);

    HashMap<String, Object> getList(Integer page, Integer pageSize, String order, Integer unread, Boolean desc, Integer userId);
}
