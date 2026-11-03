package taskapp;

import taskapp.model.Task;

public class DetailTaskPrinter implements TaskPrinter{
    @Override
    public void print(Task task) {
        System.out.println(task.getId() + "：" + task.getTitle());
        if (task.isCompleted()){
            System.out.println("已完成");
        }
        else {
            System.out.println("未完成");
        }
    }

}
