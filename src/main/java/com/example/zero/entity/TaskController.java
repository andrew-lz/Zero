package com.example.zero.entity;

import com.example.dto.TaskEditDto;
import com.example.zero.task.TaskRepository;
import com.example.zero.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public String tasks(@RequestParam(name = "sort", required = false) String sort, Model model, Model modelBacklog, Model modelSelectedForDevelopment, Model modelInProgress, Model modelDone) {
        Sort sortCondition = null;
        if (sort != null) {
            String[] str = sort.split(",", 2);
            sortCondition = Sort.by(Sort.Direction.valueOf(str[1]), str[0]);
        } else {
            sortCondition = Sort.by(Sort.Direction.DESC, "createdDate");
        }
        model.addAttribute("tasks", taskRepository.findAll(sortCondition));
        modelBacklog.addAttribute("backlogTasks", taskRepository.findAllByTaskStatus(TaskStatus.BACKLOG, sortCondition));
        modelSelectedForDevelopment.addAttribute("selectedForDevelopmentTasks", taskRepository.findAllByTaskStatus(TaskStatus.SELECTED_FOR_DEVELOPMENT, sortCondition));
        modelInProgress.addAttribute("inProgressTasks", taskRepository.findAllByTaskStatus(TaskStatus.IN_PROGRESS, sortCondition));
        modelDone.addAttribute("doneTasks", taskRepository.findAllByTaskStatus(TaskStatus.DONE, sortCondition));
        return "tasks";
    }

    @GetMapping("/tasks/add")
    public String addTask(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("users", userRepository.findAllByDeletedFalseOrderByFirstNameAscLastNameAsc());
        return "add-task";
    }

    @PostMapping("/tasks/add")
    @Transactional
    public String addPostTask(@AuthenticationPrincipal User user, @ModelAttribute Task task, Model model) {
        model.addAttribute("task", task);
        task.setReporter(user);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}/details")
    public String infoTasks(@PathVariable(value = "id") long id, Model model) {
        taskRepository.findById(id).ifPresent(o -> model.addAttribute("task", o));
        return "task-details";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTasks(@PathVariable(value = "id") long id, Model model) {
        ModelMapper modelMapper = new ModelMapper();
        TaskEditDto taskEditDto = modelMapper.map(taskRepository.getOne(id), TaskEditDto.class);
        model.addAttribute("taskEditDto", taskEditDto);
        model.addAttribute("users", userRepository.findAllByDeletedFalseOrderByFirstNameAscLastNameAsc());
        return "task-edit";
    }

    @PostMapping("/tasks/{id}/edit")
    @Transactional
    public String updateTask(@PathVariable(value = "id") long id, @ModelAttribute TaskEditDto taskEditDto, Model model) {
        model.addAttribute("taskEditDto", taskEditDto);
        Task task = taskRepository.getOne(id);
        task.setDescription(taskEditDto.getDescription());
        task.setAssignee(taskEditDto.getAssignee());
        task.setTaskPriority(taskEditDto.getTaskPriority());
        task.setTaskStatus(taskEditDto.getTaskStatus());
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/remove")
    @Transactional
    public String deleteTask(@PathVariable(value = "id") long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
}
