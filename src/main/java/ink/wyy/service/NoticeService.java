package ink.wyy.service;

import ink.wyy.bean.Notice;

public interface NoticeService {

    String add(Notice notice);

    String delete(Integer id, Integer userId);

    String read(Integer id, Integer userId);

    String getList(Integer page, Integer pageSize, String order, String unread, Boolean desc, Integer userId);
}
