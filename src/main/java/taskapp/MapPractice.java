package taskapp;

import java.util.Map;
import java.util.HashMap;
import taskapp.model.Task;

public class MapPractice {
    public static void main(String[] args) {
        Map<Integer, Task> tasksById = new HashMap<>();


        Task task1 = new Task(1, "学习 Map");
        Task task2 = new Task(2, "再次学习 Map");

        tasksById.put(1, task1);
        tasksById.put(2, task2);

        Task found2 = tasksById.get(2);
        System.out.println(found2.getTitle());

        int size = tasksById.size();
        System.out.println(size);

        Task found1 = tasksById.get(1);
        System.out.println(found1.getTitle());

        Task newTask = new Task(3, "新任务");

        boolean isContains = tasksById.containsKey(newTask.getId());
        if (isContains) {
            System.out.println("编号已存在");
        }
        else {
            tasksById.put(newTask.getId(), newTask);
            System.out.println("添加成功");
        }

        System.out.println("操作后数量：" + tasksById.size());

        System.out.println(found1.getTitle());

        Task found = tasksById.get(newTask.getId());

        if (found != null) {
            System.out.println("该编号对应的标题：" + found.getTitle());
        }
    }
}