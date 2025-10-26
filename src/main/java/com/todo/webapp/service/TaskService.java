package com.todo.webapp.service;

import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.entity.Task;
import com.todo.webapp.entity.User;
import com.todo.webapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.webapp.security.AuthenticatedUserContext;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskDto createTask(TaskDto dto){

        User currentUser = AuthenticatedUserContext.getCurrentUser();


        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Task.Status.PENDING)
                .user(currentUser)
                .createdAt(LocalDateTime.now())
                .build();

        Task saved = taskRepository.save(task);
        return  toDto(saved);
    }

    public List<TaskDto> getRecentPendingTasks() {
        User currentUser = AuthenticatedUserContext.getCurrentUser();

        return taskRepository.findTop5ByUserAndStatusOrderByCreatedAtDesc(currentUser, Task.Status.PENDING)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void markTaskAsCompleted(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(Task.Status.COMPLETED);
        taskRepository.save(task);
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .build();
    }
}
