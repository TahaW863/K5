package edu.lsu.main.model;

import com.github.dockerjava.api.model.Container;
import edu.lsu.main.enums.ContainerStatusEnum;
import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
