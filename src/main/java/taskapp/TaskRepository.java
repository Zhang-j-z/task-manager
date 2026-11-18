package taskapp;

import taskapp.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final Connection connection;

    public TaskRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Task> findAll() throws SQLException {
        String sql = "SELECT id, title, completed FROM tasks ORDER BY id";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tasks.add(readTask(resultSet));
                }
            }
        return tasks;
    }


    public Task findTaskById(int taskId) throws SQLException {
        String sql = "SELECT id, title, completed FROM tasks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readTask(resultSet);
                }
            }
        }
        return null;
    }

    private Task readTask(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        boolean completed = resultSet.getBoolean("completed");

        Task task = new Task(id, title);

        if (completed) {
            task.complete();
        }
        return task;
    }

    public boolean renameTask(int taskId, String newTitle)
            throws SQLException {
        if (newTitle == null || newTitle.isBlank()) return false;

        String sql = "UPDATE tasks SET title = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newTitle);
            statement.setInt(2, taskId);

            int rows = statement.executeUpdate();
            if (rows == 0 ) return false;
        }
        return true;
    }

    public boolean insertTask(Task task)
            throws SQLException {
        if (task == null) return false;

        String sql = "INSERT INTO tasks (id, title, completed) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, task.getId());
            statement.setString(2, task.getTitle());
            statement.setBoolean(3, task.isCompleted());

            int rows = statement.executeUpdate();
            if (rows == 0) return false;
        }
        return true;
    }

    public boolean deleteTask(int taskId)
            throws SQLException {
        // 由你实现
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);

            int rows = statement.executeUpdate();
            if (rows == 0) return false;
        }
        return true;
    }

    public boolean updateCompleted(int taskId, boolean completed)
            throws SQLException {
        // 由你实现
        String sql = "UPDATE tasks SET completed = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, completed);
            statement.setInt(2, taskId);

            int rows = statement.executeUpdate();
            if (rows == 0 ) return false;
        }
        return true;
    }

    public List<Task> findByCompleted(boolean completed)
            throws SQLException {
        // 由你实现
        String sql = "SELECT id, title, completed FROM tasks WHERE completed = ? ORDER BY id";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, completed);
            try (ResultSet resultSet = statement.executeQuery()) {
                // 遍历结果
                while (resultSet.next()) {
                    tasks.add(readTask(resultSet));
                }
            }
        }
        return tasks;
    }

    public List<Task> findPage(Boolean completed, int limit, int offset) throws SQLException{
        String sql = "SELECT id, title, completed FROM tasks ORDER BY id LIMIT ? OFFSET ?";
        String sql_screen = "SELECT id, title, completed FROM tasks WHERE completed = ? ORDER BY id LIMIT ? OFFSET ?";
        List<Task> tasks = new ArrayList<>();

        if (completed == null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, limit);
                statement.setInt(2, offset);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        tasks.add(readTask(resultSet));
                    }
                }
            }
        }
        else {
            try (PreparedStatement statement = connection.prepareStatement(sql_screen)) {
                statement.setBoolean(1, completed);
                statement.setInt(2, limit);
                statement.setInt(3, offset);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        tasks.add(readTask(resultSet));
                    }
                }
            }
        }
        return tasks;
    }
}