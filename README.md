# Java 任务管理器

基于 Java 17、Spring Boot、JDBC 和 MySQL 的任务管理项目。HTTP 接口通过 Controller、Service 和 Repository 访问数据库。项目同时保留了使用文本文件保存任务的命令行版本。

## 运行 HTTP 服务

1. 安装 JDK 17 或更高版本、Maven 和 MySQL 8.0。在 MySQL 中执行以下 SQL 初始化 Web 接口需要的数据库和表。

```sql
CREATE DATABASE IF NOT EXISTS task_learning DEFAULT CHARACTER SET utf8mb4;
USE task_learning;

CREATE TABLE IF NOT EXISTS tasks (
    id INT PRIMARY KEY CHECK (id > 0),
    title VARCHAR(100) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE
);
```

2. 在 IntelliJ IDEA 中打开 Maven 项目。编辑 `taskapp.TaskManagerApplication` 的运行配置，将本地 MySQL 密码放在环境变量 `MYSQL_PASSWORD` 中。默认连接为 `127.0.0.1:3306/task_learning`，用户名为 `root`。使用其他地址或账户时，可设置 `SPRING_DATASOURCE_URL` 和 `SPRING_DATASOURCE_USERNAME`。
3. 运行 `taskapp.TaskManagerApplication`，访问 `http://localhost:8080/tasks`。刚初始化的数据库会返回空数组。

## 代码结构

| 包 | 职责 |
|---|---|
| `taskapp` | Spring Boot 启动入口 `TaskManagerApplication` |
| `taskapp.web` | HTTP 路由和响应 |
| `taskapp.web.request` | 请求 JSON 对应的数据类 |
| `taskapp.web.error` | 参数异常和错误响应 |
| `taskapp.service` | 任务操作、数据库连接和事务范围 |
| `taskapp.repository` | JDBC、SQL 和结果映射 |
| `taskapp.model` | 任务对象 |
| `taskapp.cli` | 命令行入口、任务管理、打印和文本文件存储 |
| `taskapp.practice` | 文件、集合和 JDBC 练习 |

`TaskService.completeBatch` 在一条连接上批量标记任务完成，全部成功时提交，找不到任务或更新出错时回滚。目前通过 Service 测试调用。

## HTTP 接口

| 方法 | 路径 | 用途 | 请求 JSON 示例 |
|---|---|---|---|
| GET | `/tasks` | 按编号升序列出任务，支持状态筛选和分页 | — |
| GET | `/tasks/{id}` | 查询指定任务 | — |
| POST | `/tasks` | 创建未完成任务，返回 `201` 和 `Location` | `{"id":200,"title":"学习 Spring Boot"}` |
| PATCH | `/tasks/{id}` | 修改标题，成功返回 `204` | `{"title":"复习 Spring Boot"}` |
| PATCH | `/tasks/{id}/completed` | 标记完成或恢复未完成，成功返回 `204` | `{"completed":true}` |
| DELETE | `/tasks/{id}` | 删除任务，成功返回 `204` | — |

提交 JSON 时使用 `Content-Type: application/json`。编号已存在时，创建接口返回 `409`。按编号查询、修改或删除不存在的任务时返回 `404`。

```bash
curl "http://localhost:8080/tasks?completed=false&limit=10&offset=0"
```

这条请求先筛选未完成任务，再按编号升序取最多 10 条。省略 `completed` 时查询两种状态。`limit` 的范围是 1 到 100，传入 `limit` 后可以用 `offset` 跳过指定数量的记录，默认从 0 开始。省略两个分页参数时返回全部匹配结果。结果为空时返回 `200` 和 `[]`。

例如 `GET /tasks?limit=0` 返回 `400`，正文如下。

```json
{
  "code": "INVALID_PARAMETER",
  "message": "limit 必须在 1 到 100 之间"
}
```

## 命令行版本与测试

在 IDEA 运行 `taskapp.cli.Main` 可使用命令行版本。它在启动时读取 `tasks.txt`，退出时保存。运行 `mvn test` 或使用 IDEA 的 Maven 面板可执行 JUnit 测试，覆盖任务校验、命令行版业务逻辑、文件持久化和批量更新事务。事务测试使用独立的 H2 内存数据库。
