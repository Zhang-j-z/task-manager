package taskapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskapp.repository.TaskWriteMapper;
import taskapp.web.error.TaskNotFoundException;

import java.util.List;

@Service
public class TaskBatchService {//分批处理
    private final TaskWriteMapper mapper;

    public TaskBatchService(TaskWriteMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public void completeBatch(List<Integer> ids) {
        for(int id : ids) {
            if (mapper.markCompleted(id) == 0) {
                throw new TaskNotFoundException();
            }
        }
    }
}
