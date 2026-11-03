package taskapp.model;

public class Task {
    private final int id;
    private String title;
    private boolean completed;

    public Task(int id, String title) {
        if (id <= 0) {
            throw new IllegalArgumentException("任务编号必须大于0");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("标题不能为空");
        }

        this.id = id;
        this.title = title;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        completed = true;
    }

    public boolean rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            return false;
        }

        title = newTitle;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Task other = (Task) obj;
        return this.id == other.id;
        /* 比较当前任务和 other 的编号 */
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
        /* 根据 id 生成哈希值，可使用 Integer.hashCode(id) */
    }
}