package taskapp.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import taskapp.TaskManagerApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskApiIntegrationTest {
    private static ServletWebServerApplicationContext application;
    private static JdbcTemplate database;
    private static URI baseUri;
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @BeforeAll
    static void startApplication() {
        String databaseUrl = "jdbc:h2:mem:task_api_" + UUID.randomUUID();
        application = (ServletWebServerApplicationContext) new SpringApplicationBuilder(TaskManagerApplication.class)
                .run("--server.address=127.0.0.1", "--server.port=0",
                        "--spring.datasource.url=" + databaseUrl,
                        "--spring.datasource.driver-class-name=org.h2.Driver",
                        "--spring.datasource.username=sa", "--spring.datasource.password=");
        database = new JdbcTemplate(application.getBean(DataSource.class));
        baseUri = URI.create("http://127.0.0.1:" + application.getWebServer().getPort());
    }

    @AfterAll
    static void stopApplication() {
        if (application != null) {
            application.close();
        }
    }

    @BeforeEach
    void resetTasks() {
        database.execute("DROP TABLE IF EXISTS tasks");
        database.execute("""
                CREATE TABLE tasks (
                    id INT PRIMARY KEY,
                    title VARCHAR(100) NOT NULL,
                    completed BOOLEAN NOT NULL
                )
                """);
        database.update("INSERT INTO tasks VALUES (1, 'First task', FALSE), (2, 'Second task', FALSE)");
    }

    @Test
    void completesAllRequestedTasks() throws Exception {
        HttpResponse<String> response = completeBatch("[1,2]");

        assertEquals(204, response.statusCode());
        assertEquals("", response.body());
        assertEquals(Boolean.TRUE, isCompleted(1));
        assertEquals(Boolean.TRUE, isCompleted(2));
    }

    @Test
    void returnsNotFoundAndRollsBackWhenLaterTaskIsMissing() throws Exception {
        HttpResponse<String> response = completeBatch("[1,999]");

        assertEquals(404, response.statusCode());
        assertEquals(Boolean.FALSE, isCompleted(1));
    }

    @Test
    void rejectsEmptyAndInvalidIdLists() throws Exception {
        for (String request : new String[]{"[]", "[1,0]", "[1,null]"}) {
            assertEquals(400, completeBatch(request).statusCode(), request);
            assertEquals(Boolean.FALSE, isCompleted(1), request);
        }
    }

    @Test
    void returnsServerErrorAndRollsBackWhenDatabaseRejectsLaterUpdate() throws Exception {
        database.execute("ALTER TABLE tasks ADD CHECK (id <> 2 OR completed = FALSE)");

        HttpResponse<String> response = completeBatch("[1,2]");

        assertEquals(500, response.statusCode());
        assertEquals(Boolean.FALSE, isCompleted(1));
        assertEquals(Boolean.FALSE, isCompleted(2));
    }

    @Test
    void countsTasksWithAndWithoutCompletedFilter() throws Exception {
        assertCount("?completed=true", 0);
        database.update("UPDATE tasks SET completed = TRUE WHERE id = 1");
        assertCount("", 2);
        assertCount("?completed=true", 1);
        assertCount("?completed=false", 1);
    }

    private HttpResponse<String> completeBatch(String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("/tasks/batch/completed"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Boolean isCompleted(int id) {
        return database.queryForObject("SELECT completed FROM tasks WHERE id = ?", Boolean.class, id);
    }

    private void assertCount(String query, int count) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("/tasks/count" + query))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), query);
        assertEquals("{\"count\":" + count + "}", response.body(), query);
    }
}
