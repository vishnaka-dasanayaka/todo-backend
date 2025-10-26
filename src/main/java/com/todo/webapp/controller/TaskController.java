package com.todo.webapp.controller;

import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto created = taskService.createTask(taskDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getRecentTasks() {
        List<TaskDto> tasks = taskService.getRecentPendingTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> markTaskCompleted(@PathVariable Long id) {
        taskService.markTaskAsCompleted(id);
        return ResponseEntity.noContent().build();
    }

}
