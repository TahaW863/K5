package edu.lsu.observer.model;

import com.github.dockerjava.api.model.Statistics;
import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document (collection = "docker_stats")
public class DockerStatsModel {
    @Id
    private String id;
    private String containerId;
    private Statistics stats;
    private Date timestamp;
}
