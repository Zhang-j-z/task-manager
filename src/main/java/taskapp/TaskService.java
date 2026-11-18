package taskapp;

import org.springframework.stereotype.Service;
import taskapp.model.Task;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class TaskService {
    public Task demo() {
        return new Task(1, "学习 HTTP 接口");
    }
    public Task findTaskById(int taskId) throws SQLException{
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.findTaskById(taskId);
        }
    }

    private final DataSource dataSource;

    public TaskService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public List<Task> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.findAll();
            // 调用 repository 已有的 findAll()，并返回结果
        }
    }

    public boolean renameTask(int taskId, String newTitle)
            throws SQLException {
        // 由你实现
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.renameTask(taskId, newTitle);
        }
    }

    public Task createTask(int taskId, String title) throws SQLException {
        Task task = new Task(taskId, title);
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            boolean created = repository.insertTask(task);
            if (!created) throw new SQLException("新增任务未写入记录");
        }
        return task;
    }

    public boolean deleteTask(int taskId) throws SQLException {
        // 获取连接 → 创建 Repository
        // 调用 deleteTask(taskId)，返回结果
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.deleteTask(taskId);
        }
    }

    public boolean updateCompleted(int taskId, boolean completed) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.updateCompleted(taskId, completed);
        }
    }

    public List<Task> findByCompleted(boolean completed)
            throws SQLException {
        // 获取连接 → 创建 Repository
        // 调用 repository.findByCompleted(completed)，返回列表
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.findByCompleted(completed);
        }
    }

    public List<Task> findPage(Boolean completed, int limit, int offset) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            TaskRepository repository = new TaskRepository(connection);
            return repository.findPage(completed, limit, offset);
        }
    }

}