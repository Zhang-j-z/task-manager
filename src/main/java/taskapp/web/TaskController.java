package taskapp.web;

import taskapp.service.TaskService;
import taskapp.web.error.InvalidTaskQueryException;
import taskapp.web.request.CreateTaskRequest;
import taskapp.web.request.RenameTaskRequest;
import taskapp.web.request.UpdateCompletedRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskapp.model.Task;

import java.net.URI;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;


@RestController
public class TaskController {

    @GetMapping("/tasks/demo")
    public Task demo() {
        return taskService.demo();
    }

    private final TaskService taskService;

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> findTaskById(
            @PathVariable("id") int taskId) throws SQLException {

        // 1. 调用 Service，取得 Task
        Task task = taskService.findTaskById(taskId);
        // 2. 如果 Task 是 null，返回 404
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        // 3. 否则返回 200，并带上 Task
        else return ResponseEntity.ok(task);
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<Void> renameTask(
            @PathVariable("id") int taskId,
            @RequestBody RenameTaskRequest request) throws SQLException {

        // 1. 取得 title，先检查是否为 null 或空白
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build(); // 400：标题不合法
        }
        // 2. 合法时调用 Service
        boolean renamed = taskService.renameTask(taskId, request.getTitle());
        // 3. 根据结果返回状态码
        if (renamed) {
            return ResponseEntity.noContent().build();  // 204：修改成功
        }
        else return ResponseEntity.notFound().build();  // 404：任务不存在
    }

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> findAll(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset)
            throws SQLException {
        if (limit == null) {
            if (offset != null) {
                throw new InvalidTaskQueryException("传 offset 时必须同时传 limit");
            }
            if (completed == null) {
                return ResponseEntity.ok(taskService.findAll());
            }
            return ResponseEntity.ok(taskService.findByCompleted(completed));
        }

        if (limit < 1 || limit > 100) {
            throw new InvalidTaskQueryException("limit 必须在 1 到 100 之间");
        }
        int actualOffset = offset == null ? 0 : offset;
        if (actualOffset < 0) {
            throw new InvalidTaskQueryException("offset 不能小于 0");
        }
        return ResponseEntity.ok(taskService.findPage(completed, limit, actualOffset));
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(
            @RequestBody CreateTaskRequest request) throws SQLException {

        // 先校验：编号必须大于 0，标题不能为 null 或空白
        // 不合法时返回 400
        if (request.getTitle() == null || request.getTitle().isBlank()
         || request.getId() < 1) {
            return ResponseEntity.badRequest().build(); // 400：标题不合法
        }


        try {
            // 调用 Service，用 Task task 接收结果
            // 返回 201
            Task task = taskService.createTask(request.getId(), request.getTitle());
            return ResponseEntity.created(
                    URI.create("/tasks/" + task.getId())
            ).body(task);
        } catch (SQLIntegrityConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") int taskId) throws SQLException {

        // 1. 编号 <= 0，返回 400
        if (taskId < 1) return ResponseEntity.badRequest().build();
        // 2. 调用一次 Service，用 boolean deleted 接收结果
        boolean deleted = taskService.deleteTask(taskId);
        // 3. true 返回 204，false 返回 404
        if (deleted) return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }

    @PatchMapping("/tasks/{id}/completed")
    public ResponseEntity<Void> updateCompleted(
            @PathVariable("id") int taskId,
            @RequestBody UpdateCompletedRequest request)
            throws SQLException {

        // 编号 <= 0，或者 completed 为 null：400
        if (taskId < 1 || request.getCompleted() == null) {
            return ResponseEntity.badRequest().build(); // 400：标题不合法
        }
        // 调用一次 Service，接收 boolean 结果
        boolean updated = taskService.updateCompleted(taskId, request.getCompleted());
        // true：204；false：404
        if (updated) return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }
}