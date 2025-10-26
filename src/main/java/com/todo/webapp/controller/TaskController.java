package com.todo.webapp.controller;

import com.todo.webapp.dto.DashboardDataDto;
import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.dto.TaskInputDto;
import com.todo.webapp.dto.TaskUpdateInputDto;
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
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskInputDto taskDto) {
        TaskDto created = taskService.createTask(taskDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getRecentTasks() {
        List<TaskDto> tasks = taskService.getRecentPendingTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<Void> markTaskCompleted(@PathVariable Long id, @PathVariable String status) {
        taskService.markTaskAsCompleted(id, status);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskUpdateInputDto taskDto){
        TaskDto updated = taskService.updateTask(taskDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/data")
    public ResponseEntity<DashboardDataDto> getDashboardData(){
        DashboardDataDto data = taskService.getDashboardData();
        return ResponseEntity.ok(data);
    }

}
