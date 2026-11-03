package taskapp.model;

import java.util.Set;
import java.util.HashSet;

public class SetPractice {

    public static void main(String[] args) {
        Set<String> tags = new HashSet<>();

        System.out.println(tags.add("Java"));
        System.out.println(tags.add("Java"));
        System.out.println(tags.add("SQL"));
        System.out.println(tags.size());
        System.out.println(tags.contains("Java"));

        for (String tag : tags) {
            System.out.println(tag);
        }

        Set<Task> tasks = new HashSet<>();

        Task a = new Task(1, "学习Java");
        Task b = new Task(1, "学习Java");

        System.out.println(a == b);

        System.out.println(tasks.add(a));
        System.out.println(tasks.add(b));
        System.out.println(tasks.add(a));

        System.out.println(tasks.size());

        System.out.println(a.equals(b));
        System.out.println(a.equals(a));

        Task c = new Task(2, "另一个任务");

        System.out.println(a.equals(c)); // 应为 false
        System.out.println(tasks.add(c)); // 应为 true
        System.out.println(tasks.size()); // 应为 2

        a.rename("改名后的任务");

        System.out.println(tasks.contains(a));
        System.out.println(tasks.contains(new Task(1, "完全不同的标题")));
    }
}
