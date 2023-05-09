package edu.lsu.publisher.model;

import com.github.dockerjava.api.model.Container;
import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class ContainerModel {
    @Id
    private String id;
    private Container container;
    private String imageNameWithTag;
    private String command;
    private VolumeModel volumeModel;
}
