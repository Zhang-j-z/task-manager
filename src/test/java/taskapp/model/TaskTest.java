package taskapp.model;

import org.junit.jupiter.api.Test;
import taskapp.model.Task;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void constructorShouldCreateTaskWithValidArguments() {//合法参数创建成功
        Task task = new Task(1, "学习测试");

        assertEquals(1, task.getId());
        assertEquals("学习测试", task.getTitle());
        assertFalse(task.isCompleted());
    }

    @Test
    void constructorShouldRejectZeroId() {//编号0
        assertThrows(IllegalArgumentException.class,
                () -> new Task(0, "学习测试"));
    }

    @Test
    void constructorShouldRejectNegativeId() {//编号小于0
        assertThrows(IllegalArgumentException.class,
                () -> new Task(-1, "学习测试"));
    }

    @Test
    void constructorShouldRejectNullTitle() {//标题为null
        assertThrows(IllegalArgumentException.class,
                () -> new Task(1, null));
    }

    @Test
    void constructorShouldRejectEmptyTitle() {//标题为空
        assertThrows(IllegalArgumentException.class,
                () -> new Task(1, ""));
    }

    @Test
    void constructorShouldRejectBlankTitle() {//标题空格
        assertThrows(IllegalArgumentException.class,
                () -> new Task(1, " "));
    }
}
