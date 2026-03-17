package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.PaginatedTaskResponse;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskRequest;
// import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Endpoints for managing caseworker tasks.")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks.")
    public ResponseEntity<PaginatedTaskResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get tasks (page: {}, size: {})", page, size);
        return ResponseEntity.ok(taskService.getAllTasks(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a distinct task by its unique ID.")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable UUID id) {
        log.info("Get task by ID API called: {}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create a task", description = "Creates a new task with given details.")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest request) {
        log.info("Create task API called: {}", request.getTitle());
        TaskDto createdTask = taskService.createTask(request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // @PatchMapping("/{id}/status")
    // @Operation(summary = "Update task status", description = "Allows updating only the status field of a particular task.")
    // public ResponseEntity<TaskDto> updateTaskStatus(
    //         @PathVariable UUID id,
    //         @Valid @RequestBody UpdateTaskStatusRequest request) {
    //     log.info("Update task status API called: {}", id, request.getStatus());
    //     return ResponseEntity.ok(taskService.updateTaskStatus(id, request));
    // }

    @PatchMapping("/{id}")
    @Operation(summary = "Update task", description = "Allows updating multiple fields of a particular task.")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        log.info("Update task API called: {}", id, request);
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Permanently removes a task.")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        log.info("Delete task API called: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
