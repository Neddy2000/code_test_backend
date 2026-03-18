package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.PaginatedTaskResponse;
import uk.gov.hmcts.reform.dev.dto.TaskDto;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskRequest;
import uk.gov.hmcts.reform.dev.entities.Task;
import uk.gov.hmcts.reform.dev.entities.TaskPriority;
import uk.gov.hmcts.reform.dev.entities.TaskStatus;
import uk.gov.hmcts.reform.dev.entities.User;
import uk.gov.hmcts.reform.dev.exceptions.NotFoundException;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private UUID taskId;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        user = User.builder().id(userId).name("testuser").build();
        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .assignee(user)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnPaginatedResponse() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        PaginatedTaskResponse response = taskService.getAllTasks(page, size);

        assertThat(response.getTasks()).hasSize(1);
        assertThat(response.getTotalTasks()).isEqualTo(1);
        assertThat(response.getTasks().get(0).getTitle()).isEqualTo("Test Task");
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void getTaskById_ShouldReturnTaskDto_WhenTaskExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(taskId);

        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskById_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void createTask_ShouldCreateAndReturnTaskDto() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .assignedToId(userId)
                .build();

        when(userService.getUserEntity(userId)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(taskId);
            return t;
        });

        TaskDto result = taskService.createTask(request);

        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.getAssignedTo().getId()).isEqualTo(userId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTaskDto() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Title")
                .status(TaskStatus.DONE)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.updateTask(taskId, request);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void deleteTask_ShouldDelete_WhenTaskExists() {
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTask_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Task not found");
    }
}
