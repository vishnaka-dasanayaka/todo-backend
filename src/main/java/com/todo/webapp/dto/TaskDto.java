package com.todo.webapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Boolean completed;
}
