package com.example.zero.task;

import com.example.zero.entity.Task;
import com.example.zero.entity.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long>, JpaRepository<Task, Long> {
    List<Task> findAllByTaskStatus(TaskStatus taskStatus, Sort sort);
}
