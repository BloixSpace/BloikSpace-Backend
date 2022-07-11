# noixForum——noix论坛

## 基本功能

- 登录、注册（密码使用hash加密，数据库保存hash串）
- 论坛多类别或者多板块（学术版、问答版、灌水区、讨论版）
- 反馈、申请、举报等
- 发文，修改、删除文章（记录创建、修改时间）
- 文章回复，插入图片，表情，链接等
- 管理员面板，对所有文章进行任何操作，管理用户，评论等

## 接口

### 注册

- /register
  - post
  - 请求字段：
    - username: 用户名
    - password: 密码（使用md5加密）
    - 测: safeQuestion: true/false是否进行安全问题设置
    - 测: question: 问题
    - 测: answer: 答案，一个字符串
  - 响应字段：
    - status: 状态返回0失败或者1成功
    - id: 用户唯一id
    - errMsg: 如果失败返回失败信息

### 登录

- /login
  - post
  - 请求字段：
      - username: 用户名
      - password: 密码（使用md5加密）
  - 响应字段：
      - status: 状态返回0失败或者1成功
      - id: 用户唯一id
      - errMsg: 如果失败返回失败信息

- /getImageUrl
  - get
  - 无请求字段
  - 响应字段
    - status: 0,1
    - url: 链接
    - errMsg: 失败信息

### 文章相关

- /article/add
  - post
  - 请求字段：
    - title: 标题
    - text: 正文，html字符串
    - category: "学术"，"题目"。。。
    - time: 发布时间
  - 响应字段
    - status: 0不成功1成功
    - id: 文章唯一id，仅当成功
    - errMsg: 错误信息，仅当失败

- /article/getlist?page=x&category=学术。。。
  - get
  - 响应字段：
    - count: 文章数量 
    - pageNum: 页数
    - 数组
      - id: 文章id
      - count: 查看次数
      - title: 文章题目
      - authorId: 作者id
      - authorName: 作者
      - time: 最后修改时间

- /article/get?id=x
  - 每次访问，查看次数加一
  - get
  - 响应字段
    - status: 0,1
    - id: 文章id
    - text: 文章html代码
    - count: 查看次数
    - title: 文章题目
    - authorName: 作者
    - authorId: 作者用户id
    - time: 发布时间
    - modifyTime: 最后修改时间

- /article/get_self_all?page=x
  - get
  - 响应字段:
    - count: 数量
    - pageNum: 页数
    - 数组:
      - id: 文章id
      - title: 文章id
      - time: 发布时间
      - modifyTime: 最后修改时间

- /article/set
  - post
  - 请求字段:
    - id: 文章id
    - title: 文章题目
    - text: 文章html
    - modifyTime: 修改时间
  - 响应字段:
    - status: 0,1
    - errMsg: 错误信息

- /article/delete?id=x
  - get
  - 无请求字段
  - 响应字段:
    - status: 0,1
    - errMsg: 错误信息

### 回复相关

- /reply/add
  - 请求字段:
    - id: 文章id
    - text: 回复html
    - time: 回复时间
  - 响应字段:
    - status: 0,1
    - id: 评论唯一id
    - errMsg: 错误信息

- /reply/get_all?article_id=x&page=y
  - get
  - 响应字段
    - count: 评论数量
    - pageNum: 页数
    - 数组:
      - id: 回复id
      - text: 回复的html代码
      - time: 回复时间

- /reply/delete?id=x
  - get
  - 响应字段
    - status: 0，1
    - errMsg: 错误信息

