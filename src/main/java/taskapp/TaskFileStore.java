package taskapp;

import taskapp.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskFileStore {

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


    public static void saveTasks(Path path, List<Task> tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(toLine(task));
        }
        Files.write(path, lines);
    }

    public static List<Task> loadTasks(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(fromLine(lines.get(i)));
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("第" + (i + 1) + "行：" + e.getMessage());
            }
        }
        return tasks;
    }
}
