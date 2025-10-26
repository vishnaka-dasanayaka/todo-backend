package com.todo.webapp.repository;

import com.todo.webapp.entity.Task;
import com.todo.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Fetch only the 5 most recent pending tasks of a specific user
    List<Task> findTop5ByUserAndStatusOrderByCreatedAtDesc(User user, Task.Status status);

    // Optional: fetch all active (non-completed) tasks
    List<Task> findByUserAndStatusNot(User user, Task.Status status);
}