package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.entities.TaskPriority;
import uk.gov.hmcts.reform.dev.entities.TaskStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "Priority is mandatory")
    private TaskPriority priority;

    @NotNull(message = "Status is mandatory")
    private TaskStatus status;

    private Instant dueDate;

    private UUID assignedToId;
    
    private UUID reporterId;
}
