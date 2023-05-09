package edu.lsu.publisher.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DockerStatsSummaryDto {
    private String id;
    private String name;
    private String command;
    private String containerId;
    private float cpuPercent;
    private float memoryUsage;
    private float memoryLimit;
    private float networkI;
    private float networkO;
    private Date timestamp;
}
