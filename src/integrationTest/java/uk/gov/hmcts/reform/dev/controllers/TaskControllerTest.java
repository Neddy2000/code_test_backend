package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.PaginatedTaskResponse;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskRequest;
import uk.gov.hmcts.reform.dev.entities.TaskPriority;
import uk.gov.hmcts.reform.dev.entities.TaskStatus;
import uk.gov.hmcts.reform.dev.exceptions.NotFoundException;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient ObjectMapper objectMapper;

    @MockitoBean
    private transient TaskService taskService;

    @DisplayName("Should return all tasks with pagination")
    @Test
    void getAllTasks() throws Exception {
        PaginatedTaskResponse response = PaginatedTaskResponse.builder()
                .tasks(List.of(TaskDto.builder()
                        .id(UUID.randomUUID())
                        .title("Test Task")
                        .status(TaskStatus.TODO)
                        .priority(TaskPriority.MEDIUM)
                        .build()))
                .totalTasks(1)
                .totalPages(1)
                .currentPage(0)
                .build();

        when(taskService.getAllTasks(0, 10)).thenReturn(response);

        mockMvc.perform(get("/api/tasks")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].title").value("Test Task"))
                .andExpect(jsonPath("$.totalTasks").value(1));
    }

    @DisplayName("Should return a single task by ID")
    @Test
    void getTaskById_Success() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskDto taskDto = TaskDto.builder()
                .id(taskId)
                .title("Single Task")
                .build();

        when(taskService.getTaskById(taskId)).thenReturn(taskDto);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Single Task"));
    }

    @DisplayName("Should return 404 when task not found")
    @Test
    void getTaskById_NotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.getTaskById(taskId)).thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Should create a new task")
    @Test
    void createTask() throws Exception {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("New Task")
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.TODO)
                .build();

        TaskDto createdTask = TaskDto.builder()
                .id(UUID.randomUUID())
                .title("New Task")
                .build();

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @DisplayName("Should update an existing task")
    @Test
    void updateTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Task")
                .build();

        TaskDto updatedTask = TaskDto.builder()
                .id(taskId)
                .title("Updated Task")
                .build();

        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class))).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @DisplayName("Should delete an existing task")
    @Test
    void deleteTask_Success() throws Exception {
        UUID taskId = UUID.randomUUID();
        doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Should return 404 when deleting non-existent task")
    @Test
    void deleteTask_NotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        doThrow(new NotFoundException("Task not found")).when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }
}
