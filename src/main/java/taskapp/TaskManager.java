package taskapp;

import taskapp.model.Task;

/*
import java.util.ArrayList;
import java.util.List;
 */

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TaskManager {
    private final Map<Integer, Task> tasksById = new HashMap<>();

    boolean addTask(Task newTask) {
        /*
        if (newTask == null) {
            return false;
        }
        for (Task existingtask : tasks) {
            if (newTask.getId() == existingtask.getId()) {
                return false;
            }
        }

        tasks.add(newTask);
        return true;

         */

        if (newTask == null || tasksById.containsKey(newTask.getId())) {
            return false;
        }

        tasksById.put(newTask.getId(), newTask);
        return true;
    }

    Task findById(int id) {
        // 按编号查找，找不到返回 null
        /*
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;

         */

            return tasksById.get(id);
            //键不存在时，get 本来就返回 null，不用先查一次 containsKey
    }

    boolean completeTask(int id) {
        Task task = findById(id);

        if (task == null) {
            return false;
        }

        task.complete();
        return true;
    }


    void printTasks(TaskPrinter printer) {
        // 打印每个任务的编号、标题和完成状态
        /*
        for (Task task : tasks) {
            printer.print(task);
        }

         */

        for (Task task : tasksById.values()) {
            // values() 提供 Map 中所有的任务对象
            printer.print(task);
        }
    }

    boolean renameTask(int id, String newTitle) {
        Task task = findById(id);

        if (task == null) {
            return false;
        }

        return task.rename(newTitle);
    }

    boolean deleteTask(int id) {
        //删除任务
        /*
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task.getId() == id) {
                tasks.remove(i);
                return true;
            }
        }
        return false;

         */

        Task removed = tasksById.remove(id);
        if (removed == null) {
            return false;
        }
        return true;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasksById.values());
    }

}