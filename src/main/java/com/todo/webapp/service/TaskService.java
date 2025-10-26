package com.todo.webapp.service;

import com.todo.webapp.dto.DashboardDataDto;
import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.dto.TaskInputDto;
import com.todo.webapp.dto.TaskUpdateInputDto;
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

    public TaskDto createTask(TaskInputDto dto){

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

    public void markTaskAsCompleted(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        try {
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            task.setStatus(taskStatus);
            taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }

    public TaskDto updateTask(TaskUpdateInputDto taskDto) {
        Task task = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        try {
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            taskRepository.save(task);
            return toDto(task);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid inputs");
        }
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .completed(task.getStatus() == Task.Status.COMPLETED )
                .build();
    }

    public DashboardDataDto getDashboardData() {
        User currentUser = AuthenticatedUserContext.getCurrentUser();

        Long totalTasks = taskRepository.countByUserAndStatusNot(currentUser, Task.Status.DELETED);
        Long pendingTasks = taskRepository.countByUserAndStatus(currentUser, Task.Status.PENDING);
        Long completedTasks = taskRepository.countByUserAndStatus(currentUser, Task.Status.COMPLETED);

        return new DashboardDataDto(
                currentUser.getFirstname() + " " + currentUser.getLastname(),
                currentUser.getEmail(),
                totalTasks,
                pendingTasks,
                completedTasks
        );

    }
}
