package uk.gov.hmcts.reform.dev.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedTaskResponse {
    private List<TaskDto> tasks;
    private long totalTasks;
    private int totalPages;
    private int currentPage;
}
