package edu.lsu.publisher.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DockerStatsSummaryDto {
    private String containerId;
    private float cpuPercent;
    private float memoryUsage;
    private float memoryLimit;
    private float networkI;
    private float networkO;
}
