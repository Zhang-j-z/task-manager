package taskapp;

import taskapp.model.Task;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager();
        Path path = Path.of("tasks.txt");



        try {
            loadIntoManager(path, manager);

        } catch (NoSuchFileException e) {
            System.out.println("暂无历史数据");
        } catch (IOException e) {
            System.out.println("加载失败：" + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("文件数据不合法：" + e.getMessage());
            return;
        }

        boolean running = true;

        while (running) {
            System.out.println("1. 添加任务");
            System.out.println("2. 查看任务");
            System.out.println("3. 完成任务");
            System.out.println("4. 删除任务");
            System.out.println("5. 重命名任务");
            System.out.println("0. 退出");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": {
                        handleAddTask(scanner, manager);
                        break;
                    }

                    case "2": {
                        //选择显示方式
                        System.out.println("请选择显示方式");
                        System.out.println("1.简略");
                        System.out.println("2.详细");
                        String op = scanner.nextLine();
                        TaskPrinter printer;

                        if (op.equals("1")) {
                            printer = new SimpleTaskPrinter();
                        } else if (op.equals("2")) {
                            printer = new DetailTaskPrinter();
                        } else {
                            System.out.println("没有这个显示方式");
                            break;
                        }

                        System.out.println("当前任务列表：");
                        manager.printTasks(printer);
                        break;
                    }

                    case "3": {
                        //完成任务，调用管理器完成
                        handleCompleteTask(scanner, manager);
                        break;
                    }

                    case "4": {
                        //删除任务
                        handleDeleteTask(scanner, manager);
                        break;
                    }

                    case "5": {
                        //重命名任务
                        handleRenameTask(scanner, manager);
                        break;
                    }

                    case "0": {
                        List<Task> tasks = manager.getTasks();
                        TaskFileStore.saveTasks(path, tasks);
                        running = false;
                        System.out.println("再见！");
                        break;
                    }

                    default: {
                        System.out.println("没有这个选项，请重新选择");
                        break;
                    }
                }
            }
            catch (IllegalArgumentException e) {
                System.out.println("输入有误：" + e.getMessage());
            }
            catch (IOException e) {
                System.out.println("文件操作失败：" + e.getMessage());
            }
        }

    }

    private static void handleAddTask(Scanner scanner, TaskManager manager) {
        // 1. 提示输入编号，读取一行并转换成 int
        System.out.println("请输入任务编号:");
        String temp = scanner.nextLine();
        int id = Integer.parseInt(temp);


        // 2. 提示输入标题，读取一行
        System.out.println("请输入任务标题:");
        String title = scanner.nextLine();

        // 3. 用输入的数据创建 taskapp.model.Task
        Task newTask = new Task(id, title);

        // 4. 调用 manager.addTask，根据返回值打印添加结果
        boolean added = manager.addTask(newTask);
        if (added) {
            System.out.println("你输入的标题是：" + title);
        }
        else {
            System.out.println("添加失败：编号已存在");
        }
    }

    private static void handleDeleteTask(Scanner scanner, TaskManager manager) {
        System.out.println("请选择要删除的任务编号：");
        String temp = scanner.nextLine();
        int id = Integer.parseInt(temp);
        boolean deleted = manager.deleteTask(id);
        if (deleted) {
            System.out.println("已删除该任务！");
        }
        else {
            System.out.println("任务不存在！");
        }
    }

    private static void handleCompleteTask(Scanner scanner, TaskManager manager) {
        System.out.println("请选择要完成的任务编号：");
        String temp = scanner.nextLine();
        int id = Integer.parseInt(temp);
        boolean completed = manager.completeTask(id);
        if (completed) {
            System.out.println("已完成该任务！");
        }
        else {
            System.out.println("完成任务失败！");
        }
    }

    private static void handleRenameTask(Scanner scanner, TaskManager manager) {
        System.out.println("请选择要重命名的任务编号：");
        String temp = scanner.nextLine();
        int id = Integer.parseInt(temp);

        System.out.println("请输入新标题：");
        String newTitle = scanner.nextLine();

        boolean renamed = manager.renameTask(id, newTitle);
        if (!renamed) {
            System.out.println("任务不存在或新标题不合法");
        }
        else {
            System.out.println("重命名成功，新标题为：" + newTitle);
        }
    }

    private static void loadIntoManager(Path path, TaskManager manager) throws IOException{
        List<Task> loadedTasks = TaskFileStore.loadTasks(path);
        // TODO：遍历列表，添加到 manager
        // TODO：如果添加返回 false，抛异常说明编号重复
        for (Task task : loadedTasks) {
            boolean added = manager.addTask(task);
            if (!added) {
                throw new IllegalArgumentException("任务编号重复:" + task.getId());
            }
        }
    }
}