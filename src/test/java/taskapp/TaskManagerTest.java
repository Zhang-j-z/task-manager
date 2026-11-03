package taskapp;

import org.junit.jupiter.api.Test;
import taskapp.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    @Test
    void addTaskShouldStoreNewTask() {
        // 1. 准备：创建管理器和任务
        TaskManager manager = new TaskManager();
        Task task = new Task(1, "学习测试");

        // 2. 执行：调用要测试的方法，记录结果
        boolean added = manager.addTask(task);
        Task stored = manager.findById(1);

        // 3. 检查：用断言检查返回值及管理器中的数据
        assertTrue(added);
        assertEquals(1, manager.getTasks().size());
        assertNotNull(stored);
        assertEquals("学习测试", stored.getTitle());

    }

    @Test
    void addTaskShouldRejectDuplicateId() {//重复编号
        TaskManager manager = new TaskManager();
        Task task = new Task(1, "学习 Java");
        Task task1 = new Task(1, "学习 SQL");

        boolean added = manager.addTask(task);
        boolean added1 = manager.addTask(task1);
        Task stored = manager.findById(1);

        assertTrue(added);
        assertFalse(added1);
        assertEquals(1, manager.getTasks().size());
        assertNotNull(stored);
        assertEquals("学习 Java", stored.getTitle());

    }

    @Test
    void addTaskShouldRejectNull() {//空任务
        TaskManager manager = new TaskManager();

        boolean added = manager.addTask(null);

        assertFalse(added);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void findByIdShouldReturnNullWhenIdIsMissing() {//查找不存在的任务
        TaskManager manager = new TaskManager();

        assertNull(manager.findById(99));

    }

    @Test
    void completeTaskShouldMarkExistingTaskAsCompleted() {//完成已有任务
        TaskManager manager = new TaskManager();

        Task task = new Task(1, "学习 Java");

        boolean added = manager.addTask(task);
        assertFalse(task.isCompleted());

        assertEquals(task, manager.findById(1));
        assertTrue(manager.completeTask(1));
        assertNotNull(manager.findById(1));
        assertTrue(manager.findById(1).isCompleted());
        assertTrue(task.isCompleted());

    }

    @Test
    void completeTaskShouldReturnFalseWhenIdIsMissing() {//完成不存在的任务
        TaskManager manager = new TaskManager();

        assertFalse(manager.completeTask(99));
        assertEquals(0, manager.getTasks().size());

    }

    @Test
    void deleteTaskShouldRemoveExistingTask() {//删除已有任务
        TaskManager manager = new TaskManager();

        Task task = new Task(1, "学习测试");
        Task task1 = new Task(2, "学习 SQL");

        manager.addTask(task);
        manager.addTask(task1);

        boolean deleted = manager.deleteTask(1);

        assertTrue(deleted);
        assertNull(manager.findById(1));
        assertEquals(1, manager.getTasks().size());
        assertNotNull(manager.findById(2));
        assertEquals("学习 SQL", manager.findById(2).getTitle());
    }

    @Test
    void deleteTaskShouldReturnFalseWhenIdIsMissing() {//删除不存在的任务
        TaskManager manager = new TaskManager();

        Task task = new Task(1, "学习测试");

        manager.addTask(task);

        assertFalse(manager.deleteTask(99));
        assertNotNull(manager.findById(1));
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    void deleteTaskShouldReturnFalseWhenAlreadyDeleted() {//重复删除
        TaskManager manager = new TaskManager();

        Task task = new Task(1, "学习测试");

        manager.addTask(task);

        assertTrue(manager.deleteTask(1));
        assertFalse(manager.deleteTask(1));
        assertEquals(0, manager.getTasks().size());
    }

}