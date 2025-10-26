package com.todo.webapp.service;

import com.todo.webapp.dto.DashboardDataDto;
import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.dto.TaskInputDto;
import com.todo.webapp.dto.TaskUpdateInputDto;
import com.todo.webapp.entity.Task;
import com.todo.webapp.entity.User;
import com.todo.webapp.repository.TaskRepository;
import com.todo.webapp.security.AuthenticatedUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void testCreateTaskPending() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("First Name");

        Mockito.mockStatic(AuthenticatedUserContext.class).when(AuthenticatedUserContext::getCurrentUser).thenReturn(mockUser);

        TaskInputDto inputDto = new TaskInputDto();
        inputDto.setTitle("Test Task");
        inputDto.setDescription("Task description");

        Task savedTask = Task.builder()
                .id(1L)
                .title(inputDto.getTitle())
                .description(inputDto.getDescription())
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDto result = taskService.createTask(inputDto);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("Task description", result.getDescription());
        assertEquals(false, result.getCompleted());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTaskCompleted() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("First Name");

        Mockito.mockStatic(AuthenticatedUserContext.class).when(AuthenticatedUserContext::getCurrentUser).thenReturn(mockUser);

        TaskInputDto inputDto = new TaskInputDto();
        inputDto.setTitle("Test Task");
        inputDto.setDescription("Task description");

        Task savedTask = Task.builder()
                .id(1L)
                .title(inputDto.getTitle())
                .description(inputDto.getDescription())
                .status(Task.Status.COMPLETED)
                .user(mockUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDto result = taskService.createTask(inputDto);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("Task description", result.getDescription());
        assertEquals(true, result.getCompleted());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getRecentPendingTasks() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("First Name");

        Mockito.mockStatic(AuthenticatedUserContext.class)
                .when(AuthenticatedUserContext::getCurrentUser)
                .thenReturn(mockUser);

        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Desc 1")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Desc 2")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        Task task3 = Task.builder()
                .id(3L)
                .title("Task 3")
                .description("Desc 3")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(3))
                .build();

        Task task4 = Task.builder()
                .id(4L)
                .title("Task 4")
                .description("Desc 4")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(10))
                .build();

        Task task5 = Task.builder()
                .id(5L)
                .title("Task 5")
                .description("Desc 5")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(5))
                .build();

        Task task6 = Task.builder()
                .id(6L)
                .title("Task 6")
                .description("Desc 6")
                .status(Task.Status.PENDING)
                .user(mockUser)
                .createdAt(LocalDateTime.now().minusHours(6))
                .build();

        List<Task> tasks = List.of(task1, task2, task3, task5, task6);

        when(taskRepository.findTop5ByUserAndStatusOrderByCreatedAtDesc(mockUser, Task.Status.PENDING))
                .thenReturn(tasks);

        List<TaskDto> result = taskService.getRecentPendingTasks();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("Task 1", result.get(0).getTitle()); // most recent first
        assertEquals("Task 5", result.get(3).getTitle());

        verify(taskRepository, times(1))
                .findTop5ByUserAndStatusOrderByCreatedAtDesc(mockUser, Task.Status.PENDING);
    }

    @Test
    void markTaskAsCompleted_validStatus_updatesTask() {
        Task task = Task.builder()
                .id(1L)
                .status(Task.Status.PENDING)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.markTaskAsCompleted(1L, "COMPLETED");

        assertEquals(Task.Status.COMPLETED, task.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void markTaskAsCompleted_taskNotFound_throwsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.markTaskAsCompleted(1L, "COMPLETED"));

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void markTaskAsCompleted_invalidStatus_throwsException() {
        Task task = Task.builder()
                .id(1L)
                .status(Task.Status.PENDING)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.markTaskAsCompleted(1L, "INVALID"));

        assertEquals("Invalid status value: INVALID", exception.getMessage());
    }

    @Test
    void updateTask_taskExists_updatesSuccessfully() {
        Task task = Task.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskUpdateInputDto updateDto = new TaskUpdateInputDto();
        updateDto.setId(1L);
        updateDto.setTitle("New Title");
        updateDto.setDescription("New Description");

        TaskDto result = taskService.updateTask(updateDto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_taskNotFound_throwsException() {
        TaskUpdateInputDto updateDto = new TaskUpdateInputDto();
        updateDto.setId(1L);
        updateDto.setTitle("New Title");
        updateDto.setDescription("New Description");

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.updateTask(updateDto));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getDashboardData_returnsCorrectCounts() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("Vishnaka");
        mockUser.setLastname("Dasanayaka");
        mockUser.setEmail("vishnaka@gmail.com");

        Mockito.mockStatic(AuthenticatedUserContext.class)
                .when(AuthenticatedUserContext::getCurrentUser)
                .thenReturn(mockUser);

        when(taskRepository.countByUserAndStatusNot(mockUser, Task.Status.DELETED)).thenReturn(10L);
        when(taskRepository.countByUserAndStatus(mockUser, Task.Status.PENDING)).thenReturn(4L);
        when(taskRepository.countByUserAndStatus(mockUser, Task.Status.COMPLETED)).thenReturn(6L);

        DashboardDataDto result = taskService.getDashboardData();

        assertNotNull(result);
        assertEquals("Vishnaka Dasanayaka", result.getName());
        assertEquals("vishnaka@gmail.com", result.getEmail());
        assertEquals(10L, result.getTotal());
        assertEquals(4L, result.getPending());
        assertEquals(6L, result.getCompleted());

        verify(taskRepository, times(1)).countByUserAndStatusNot(mockUser, Task.Status.DELETED);
        verify(taskRepository, times(1)).countByUserAndStatus(mockUser, Task.Status.PENDING);
        verify(taskRepository, times(1)).countByUserAndStatus(mockUser, Task.Status.COMPLETED);
    }
}