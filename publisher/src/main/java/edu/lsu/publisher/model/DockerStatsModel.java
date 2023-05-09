package edu.lsu.publisher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.model.Statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document (collection = "docker_stats")
public class DockerStatsModel {
    @Id
    @JsonProperty("_id")
    private String id;
    private String containerId;
    private Statistics stats;
    private Date timestamp;
}
