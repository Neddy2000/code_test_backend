package uk.gov.hmcts.reform.dev.dto;

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
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant dueDate;
    private UUID assignedToId;
}
