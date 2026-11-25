package taskapp.service;

import org.springframework.stereotype.Service;
import taskapp.repository.TaskCountMapper;

@Service
public class TaskStatisticsService {
    private final TaskCountMapper mapper;

    public TaskStatisticsService(TaskCountMapper mapper) {
        this.mapper = mapper;
    }

    public long count(Boolean completed) {
        return mapper.count(completed);
    }
}
