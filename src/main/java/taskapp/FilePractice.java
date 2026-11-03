package taskapp;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import taskapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class FilePractice {

    private static String toLine(Task task) {
        return task.getId() + "\t"
                + task.getTitle() + "\t"
                + task.isCompleted();
    }

    private static Task fromLine(String line) {
        String[] parts = line.split("\t", -1);

        if (parts.length != 3) {
            throw new IllegalArgumentException("文件格式错误，应有3个字段");
        }
        else if (!parts[2].equals("true") && !parts[2].equals("false")) {
            throw new IllegalArgumentException("文件格式错误：完成状态不合法");
        }

        Task task = new Task(Integer.parseInt(parts[0]), parts[1]);

        if (parts[2].equals("true")) {
            task.complete();
        }
        return task;
    }

    private static void saveTask(Path path, Task task) throws IOException {
        Files.writeString(path, toLine(task));
    }

    private static Task loadTask(Path path) throws IOException {
        return fromLine(Files.readString(path));
    }

    private static void saveTasks(Path path, List<Task> tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(toLine(task));
        }
        Files.write(path, lines);
    }

    private static List<Task> loadTasks(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            tasks.add(fromLine(line));
        }
        return tasks;
    }


    public static void main(String[] args) {
        Path path = Path.of("file-practice.txt");

        System.out.println(path.toAbsolutePath());

        Task task1 = new Task(1, "学习 Java");
        task1.complete();
        Task task2 = new Task(2, "学习 Java2");
        Task task3 = new Task(3, "学习 Java3");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);


        try {
            // 写入文件
            saveTasks(path, tasks);
            // 读取文件
            List<Task> restoredTasks = loadTasks(path);
            // 打印读取的内容
            System.out.println(toLine(task1));
            System.out.println(toLine(task2));
            System.out.println(toLine(task3));
            for (Task task : restoredTasks) {
                System.out.println(toLine(task));
            }
        }
        catch (IOException e) {
            System.out.println("文件操作失败：" + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            System.out.println("文件中的任务数据不合法:" + e.getMessage());
        }


    }
}
