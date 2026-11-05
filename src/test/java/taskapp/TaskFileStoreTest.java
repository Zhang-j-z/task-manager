package taskapp;

import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import taskapp.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class TaskFileStoreTest {
    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadShouldPreserveTaskData() throws IOException {//保存后加载
        Path path = tempDir.resolve("task.txt");

        TaskManager manager = new TaskManager();

        Task task1 = new Task(1, "学习测试");
        Task task2 = new Task(2, "学习 SQL");
        task1.complete();

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> tasks = manager.getTasks();
        TaskFileStore.saveTasks(path, tasks);
        List<Task> loaded = TaskFileStore.loadTasks(path);

        assertEquals(2, loaded.size());
        assertEquals(tasks.get(0).getId(), loaded.get(0).getId());
        assertEquals(tasks.get(1).getId(), loaded.get(1).getId());
        assertEquals(tasks.get(0).getTitle(), loaded.get(0).getTitle());
        assertEquals(tasks.get(1).getTitle(), loaded.get(1).getTitle());
        assertEquals(tasks.get(0).isCompleted(), loaded.get(0).isCompleted());
        assertEquals(tasks.get(1).isCompleted(), loaded.get(1).isCompleted());

    }

    @Test
    void saveAndLoadShouldHandleEmptyTaskList() throws IOException {//保存空列表后加载
        Path path = tempDir.resolve("task.txt");

        TaskManager manager = new TaskManager();

        List<Task> tasks = manager.getTasks();
        TaskFileStore.saveTasks(path, tasks);
        List<Task> loaded = TaskFileStore.loadTasks(path);

        assertEquals(0, loaded.size());
    }

    @Test
    void loadTasksShouldThrowWhenFileIsMissing() throws IOException {//加载不存在的文件
        Path path = tempDir.resolve("missing.txt");

        assertThrows(NoSuchFileException.class,
                () -> TaskFileStore.loadTasks(path));
    }

    @Test
    void loadTasksShouldReportLineNumberAndReasonForInvalidStatus() throws IOException{//第二行完成状态非法
        Path path = tempDir.resolve("task.txt");

        Files.writeString(path, "1\t学习测试\ttrue\n2\t学习 SQL\tyes");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> TaskFileStore.loadTasks(path));

        String message = error.getMessage();
        assertTrue(message.contains("第2行"));
        assertTrue(message.contains("完成状态不合法"));
    }
}
