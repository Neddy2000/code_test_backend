package uk.gov.hmcts.reform.dev.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.dto.PaginatedTaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.entities.Task;
import uk.gov.hmcts.reform.dev.entities.User;
import uk.gov.hmcts.reform.dev.exceptions.NotFoundException;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public PaginatedTaskResponse getAllTasks(int page, int size) {
        log.debug("Fetching tasks from database (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> taskPage = taskRepository.findAll(pageable);
        
        List<TaskDto> tasks = taskPage.getContent().stream()
                .map(TaskDto::fromEntity)
                .toList();
        
        return PaginatedTaskResponse.builder()
                .tasks(tasks)
                .totalTasks(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .currentPage(taskPage.getNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID id) {
        log.debug("Fetching task with ID: {}", id);
        Task task = getTaskEntity(id);
        return TaskDto.fromEntity(task);
    }

    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());
        User assignee = request.getAssignedToId() != null 
            ? userService.getUserEntity(request.getAssignedToId()) : null;
        User reporter = request.getReporterId() != null 
            ? userService.getUserEntity(request.getReporterId()) : null;

        Task task = Task.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(request.getPriority())
            .status(request.getStatus())
            .dueDate(request.getDueDate())
            .assignee(assignee)
            .reporter(reporter)
            .build();

        task = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", task.getId());
        return TaskDto.fromEntity(task);
    }

    // @Transactional
    // public TaskDto updateTaskStatus(UUID id, UpdateTaskStatusRequest request) {
    //     log.info("Updating status for task with ID: {} to {}", id, request.getStatus());
    //     Task task = getTaskEntity(id);
    //     task.setStatus(request.getStatus());
    //     task = taskRepository.save(task);
    //     log.info("Task status updated successfully for ID: {}", id);
    //     return TaskDto.fromEntity(task);
    // }

    @Transactional
    public TaskDto updateTask(UUID id, UpdateTaskRequest request) {
        log.info("Updating task with ID: {}", id);
        Task task = getTaskEntity(id);
        
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        
        if (request.getAssignedToId() != null) {
            task.setAssignee(userService.getUserEntity(request.getAssignedToId()));
        } else {
            task.setAssignee(null);
        }

        task = taskRepository.save(task);
        log.info("Task updated successfully for ID: {}", id);
        return TaskDto.fromEntity(task);
    }

    @Transactional
    public void deleteTask(UUID id) {
        log.info("Attempting to delete task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            log.warn("Task not found for deletion with ID: {}", id);
            throw new NotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
        log.info("Successfully deleted task with ID: {}", id);
    }

    private Task getTaskEntity(UUID id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
    }
}
