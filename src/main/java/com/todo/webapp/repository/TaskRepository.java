package com.todo.webapp.repository;

import com.todo.webapp.entity.Task;
import com.todo.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findTop5ByUserAndStatusOrderByCreatedAtDesc(User user, Task.Status status);

    Long countByUserAndStatusNot(User user, Task.Status status);

    Long countByUserAndStatus(User user, Task.Status status);
}