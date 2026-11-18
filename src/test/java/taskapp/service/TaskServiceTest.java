package taskapp.service;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {
    private Connection database;
    private TaskService service;

    @BeforeEach
    void createDatabase() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL");
        database = dataSource.getConnection();
        // Keep the in-memory database alive while the service opens its own connections.
        try (var statement = database.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE tasks (
                        id INT PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        completed BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """);
            statement.executeUpdate("INSERT INTO tasks (id, title) VALUES (1, 'First'), (2, 'Second')");
        }
        service = new TaskService(dataSource);
    }

    @AfterEach
    void closeDatabase() throws SQLException {
        if (database != null) {
            database.close();
        }
    }

    @Test
    void commitsWhenAllTasksExist() throws SQLException {
        assertTrue(service.completeBatch(List.of(1, 2)));
        assertTrue(service.findTaskById(1).isCompleted());
        assertTrue(service.findTaskById(2).isCompleted());
    }

    @Test
    void rollsBackEarlierUpdateWhenLaterTaskIsMissing() throws SQLException {
        assertFalse(service.completeBatch(List.of(1, 999)));
        assertFalse(service.findTaskById(1).isCompleted());
    }

    @Test
    void rollsBackEarlierUpdateWhenLaterUpdateThrows() throws SQLException {
        try (var statement = database.createStatement()) {
            statement.executeUpdate("ALTER TABLE tasks ADD CHECK (id <> 2 OR completed = FALSE)");
        }

        assertThrows(SQLException.class, () -> service.completeBatch(List.of(1, 2)));

        assertFalse(service.findTaskById(1).isCompleted());
        assertFalse(service.findTaskById(2).isCompleted());
    }
}
