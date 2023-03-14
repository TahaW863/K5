package edu.lsu.observer.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING;

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
