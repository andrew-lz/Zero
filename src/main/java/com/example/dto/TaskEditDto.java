package com.example.dto;

import com.example.zero.entity.TaskPriority;
import com.example.zero.entity.TaskStatus;
import com.example.zero.entity.User;
import lombok.Data;

@Data
public class TaskEditDto {

    private String description;

    private User assignee;

    private TaskStatus taskStatus;

    private TaskPriority taskPriority;
}
