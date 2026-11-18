package taskapp;

import taskapp.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JdbcPractice {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/task_learning";
        String user = "root";

        System.out.println("请输入MySQL密码:");

        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("连接成功");
            System.out.println(connection.getCatalog());

            TaskRepository repository = new TaskRepository(connection);

            List<Task> tasks = repository.findAll();

            System.out.println(tasks.size());
            for (Task task : tasks) {
                System.out.println(task.getId() + " | " + task.getTitle() + " | " + task.isCompleted());
            }

            System.out.println("请输入要查询的任务编号:");
            int taskId = Integer.parseInt(scanner.nextLine());
            Task found = repository.findTaskById(taskId);

            if (found == null) {
                System.out.println("任务不存在");
            } else {
                System.out.println(found.getId() + " | " + found.getTitle() + " | " + found.isCompleted());
            }


        }


    }
}
