package com.todo.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDataDto {
    private String name;
    private String email;
    private Long total;
    private Long pending;
    private Long completed;
}
