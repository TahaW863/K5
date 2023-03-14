package edu.lsu.publisher.model;

import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document (collection = "docker_logs")
public class DockerLogsModel {
    @Id
    private String id;
    private String containerId;
    @Indexed(unique = true)
    private String logs;
    private Date timestamp;

}
