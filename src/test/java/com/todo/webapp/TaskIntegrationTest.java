package com.todo.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.webapp.controller.TaskController;
import com.todo.webapp.dto.TaskDto;
import com.todo.webapp.dto.TaskInputDto;
import com.todo.webapp.entity.Task;
import com.todo.webapp.entity.User;
import com.todo.webapp.interceptor.JwtInterceptor;
import com.todo.webapp.repository.TaskRepository;
import com.todo.webapp.repository.UserRepository;
import com.todo.webapp.security.AuthenticatedUserContext;
import com.todo.webapp.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskService taskService;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        // Make the interceptor always pass
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void createTask_integrationTest() throws Exception {
        // Arrange: create a user in H2
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .cognitoSub("cog_sub")
                .build();
        userRepository.save(user);

        TaskInputDto inputDto = new TaskInputDto();
        inputDto.setTitle("Integration Task");
        inputDto.setDescription("Test description");

        // Mock authenticated user context (if you use it in service)
        Mockito.mockStatic(AuthenticatedUserContext.class)
                .when(AuthenticatedUserContext::getCurrentUser)
                .thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Task"))
                .andExpect(jsonPath("$.description").value("Test description"));

        // Verify saved in DB
        Task savedTask = taskRepository.findAll().get(0);
        assertEquals("Integration Task", savedTask.getTitle());
        assertEquals(user.getId(), savedTask.getUser().getId());
    }



    @Test
    void getRecentPendingTasks_integrationTest() throws Exception {
        User user = userRepository.save(User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .cognitoSub("cog_sub")
                .build());

        for (int i = 1; i <= 6; i++) {
            taskRepository.save(Task.builder()
                    .title("Task " + i)
                    .description("Desc " + i)
                    .status(Task.Status.PENDING)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        Mockito.mockStatic(AuthenticatedUserContext.class)
                .when(AuthenticatedUserContext::getCurrentUser)
                .thenReturn(user);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5)) // top 5
                .andExpect(jsonPath("$[0].title").value("Task 6")); // most recent first
    }


}
