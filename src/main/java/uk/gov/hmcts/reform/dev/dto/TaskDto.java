package uk.gov.hmcts.reform.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.entities.Task;
import uk.gov.hmcts.reform.dev.entities.TaskPriority;
import uk.gov.hmcts.reform.dev.entities.TaskStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private UserDto assignedTo;
    private UserDto assignedBy;

    public static TaskDto fromEntity(Task task) {
        if (task == null) {
            return null;
        }
        return TaskDto.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .priority(task.getPriority())
            .dueDate(task.getDueDate())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .assignedTo(UserDto.fromEntity(task.getAssignee()))
            .assignedBy(UserDto.fromEntity(task.getReporter()))
            .build();
    }
}
