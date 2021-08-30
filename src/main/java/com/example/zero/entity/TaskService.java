package com.example.zero.entity;


import com.example.zero.task.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class TaskService {
    private final TaskRepository taskRepository;

    void saveTask(Task task) {
        taskRepository.save(task);
    }

    void deleteTask(Long id)
    {
        taskRepository.deleteById(id);
    }

}
