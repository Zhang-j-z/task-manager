package taskapp;

import taskapp.model.Task;

public class SimpleTaskPrinter implements TaskPrinter{
    @Override
    public void print(Task task) {
        System.out.println(task.getId() + "：" + task.getTitle());
    }
}
